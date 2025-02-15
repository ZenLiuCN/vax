package vax.query;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.temporal.Temporal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

/**
 * @author Zen.Liu
 * @since 2025-02-15
 */
@SuppressWarnings({"unused"})
public interface Fountain {

    interface Statement<T> {
        BuildContext context();

        interface Query<T> extends Statement<T> {
            Clause.From From();

            Clause.Where Where();

            Clause.Select Select();

            Clause.OrderBy OrderBy();

            Clause.Having Having();

            Clause.Limit Limit();

            Clause.GroupBy GroupBy();
        }

        interface Create extends Statement<Integer> {
            Catalog.Table<?> table();

            Clause.Values Values();

            Clause.Where Where();

            Clause.OrderBy OrderBy();

            Clause.Having Having();

            Clause.Limit Limit();

            Clause.GroupBy GroupBy();
        }

        interface Delete extends Statement<Integer> {
            Catalog.Table<?> table();

            Clause.Where Where();

            Clause.OrderBy OrderBy();

            Clause.Having Having();

            Clause.Limit Limit();

            Clause.GroupBy GroupBy();
        }

        interface Update extends Statement<Integer> {
            Catalog.Table<?> table();

            Clause.Assigns Assigns();

            Clause.Where Where();

            Clause.OrderBy OrderBy();

            Clause.Having Having();

            Clause.Limit Limit();

            Clause.GroupBy GroupBy();
        }

        interface UpdateQuery<T> extends Statement<T> {
            Catalog.Table<?> table();

            Clause.Values Values();

            Clause.Where Where();

            Clause.OrderBy OrderBy();

            Clause.Having Having();

            Clause.Limit Limit();

            Clause.GroupBy GroupBy();

            Clause.Select Select();
        }

        interface QueryCreate<T> extends Statement<Integer> {
            Query<T> query();

            Clause.Into Into();
        }

    }

    interface Clause {

        interface Values extends Clause {}

        interface Value extends Clause {}

        interface Assigns extends Clause {}

        interface Assign extends Clause {}

        interface Where extends Clause {}

        interface From extends Clause {}

        interface OrderBy extends Clause {}

        interface Select extends Clause {}

        interface GroupBy extends Clause {}

        interface Having extends Clause {}

        interface Limit extends Clause {}

        interface Into extends Clause {}


    }

    sealed interface Func<T> extends Expr<T> {
        non-sealed interface Aggregate<T> extends Func<T> {
            record COUNT(Catalog.Column<?> v) implements Aggregate<Integer> {}

            record MAX<T extends Number>(Catalog.Column<T> v) implements Aggregate<T> {}

            record AVG<T extends Number>(Catalog.Column<T> v) implements Aggregate<T> {}

            record MIN<T extends Number>(Catalog.Column<T> v) implements Aggregate<T> {}

            record SUM<T extends Number>(Catalog.Column<T> v) implements Aggregate<T> {}

            record STDEV<J extends Number, T extends Number>(Catalog.Column<J> v) implements Aggregate<T> {}

        }

        non-sealed interface Scalar<T> extends Func<T> {
            record Concat(Expr.text v, Expr.text[] v1) implements Scalar<String>, Expr.text, Value.text {}

            interface Cast<J, T> extends Scalar<T>, Value<T> {
                Expr<J> v();

                Class<J> type();

                Class<T> target();

            }
        }
    }

    interface Catalog<T> extends Expr<T> {
        interface Table<T> extends Catalog<T> {}

        interface Column<T> extends Catalog<T> {}

    }


    interface Expr<T> {
        interface Operation<T> extends Expr<T> {}

        record Exists(Expr<?> v) implements bool, Operation<Boolean> {}

        sealed interface Comparison extends bool, Operation<Boolean> {
            record Range<T extends Comparable<T>>(Expr<T> v, Expr<T> v1, Expr<T> v2, boolean reverse) implements
                                                                                                      Comparison {}

            record IsNull<T>(Expr<T> v, boolean reverse) implements Comparison {}

            record Equality<T>(Expr<T> v, Expr<T> v1, boolean reverse, boolean caseInsensitive) implements Comparison {}

            record Greater<T extends Comparable<T>>(Expr<T> v, Expr<T> v1) implements Comparison {}

            record GreaterEqual<T extends Comparable<T>>(Expr<T> v, Expr<T> v1) implements Comparison {}

            record Lesser<T extends Comparable<T>>(Expr<T> v, Expr<T> v1) implements Comparison {}

            record LesserEqual<T extends Comparable<T>>(Expr<T> v, Expr<T> v1) implements Comparison {}

            record In<T extends Comparable<T>>(Expr<T> v, Expr<T>[] vs, boolean reverse) implements Comparison {}

            enum LikeMode {
                PREFIX, SUFFIX, MIDDLE
            }

            record Like(text v, text v1, LikeMode mode, boolean reverse) implements Comparison {}

        }

        sealed interface Arithmetic<T extends Number> extends number<T>, Operation<T> {
            record Addition<T extends Number>(Expr<T> v, Expr<T> v1) implements Arithmetic<T> {}

            record Subtraction<T extends Number>(Expr<T> v, Expr<T> v1) implements Arithmetic<T> {}

            record Multiplication<T extends Number>(Expr<T> v, Expr<T> v1) implements Arithmetic<T> {}

            record Modulus<T extends Number>(Expr<T> v, Expr<T> v1) implements Arithmetic<T> {}

            record Negation<T extends Number>(Expr<T> v) implements Arithmetic<T> {}

            record Division<T extends Number>(Expr<T> v) implements Arithmetic<T> {}
        }

        sealed interface Bitwise<T extends Number> extends number<T>, Operation<T> {
            record Shift<T extends Number>(Expr<T> v, Expr<T> v1, boolean left, boolean arithmetic) implements
                                                                                                    Bitwise<T> {}

            record AND<T extends Number>(Expr<T> v, Expr<T> v1) implements Bitwise<T> {}

            record OR<T extends Number>(Expr<T> v, Expr<T> v1) implements Bitwise<T> {}

            record NOT<T extends Number>(Expr<T> v) implements Bitwise<T> {}

            record XOR<T extends Number>(Expr<T> v) implements Bitwise<T> {}
        }

        sealed interface Logical extends bool, Operation<Boolean> {
            record OR(bool[] v) implements Logical {}

            record AND(bool[] v) implements Logical {}

            record NOT(bool v) implements Logical {}
        }

        sealed interface Json<T> extends Expr<T> {
            record Query<J, T>(json<J> v, String path, int holder, Class<T> type) implements Json<T> {}

            record Exists<J>(json<J> v, String path, int holder) implements Json<Boolean>, bool {}

            record Modify<J, T>(json<J> v, String path, int holder, Expr<T> v1) implements Json<T>, Clause.Assign {}

            record Delete<J, T>(json<J> v, String path, int holder, Class<T> type) implements Json<T>, Clause.Assign {}
        }

        //region Base Type Expr
        interface bool extends Expr<Boolean> {}

        sealed interface number<T extends Number> extends Expr<T> {}

        non-sealed interface integer<T extends Number> extends number<T> {}

        non-sealed interface decimal<T extends Number> extends number<T> {}

        non-sealed interface numeric<T extends Number> extends number<T> {}

        interface temporal<T extends Temporal> extends Expr<T> {}

        interface text extends Expr<String> {}

        sealed interface binary<T> extends Expr<T> {}

        non-sealed interface blob extends binary<Buffer> {}

        non-sealed interface bin extends binary<byte[]> {}

        sealed interface json<T> extends Expr<T> {}

        non-sealed interface jsonObject extends json<JsonObject> {}

        non-sealed interface jsonArray extends json<JsonArray> {}
        //endregion
    }

    interface Value<T> extends Expr<T> {

        //region Types
        interface bool extends Expr.bool, Value<Boolean> {}

        sealed interface number<T extends Number> extends Expr.number<T>, Value<T> {}

        non-sealed interface integer<T extends Number> extends number<T>, Expr.integer<T> {}

        non-sealed interface decimal<T extends Number> extends number<T>, Expr.decimal<T> {}

        non-sealed interface numeric<T extends Number> extends number<T>, Expr.numeric<T> {}

        interface temporal<T extends Temporal> extends Value<T>, Expr.temporal<T> {}

        interface text extends Value<String>, Expr.text {}

        sealed interface binary<T> extends Value<T>, Expr.binary<T> {}

        non-sealed interface blob extends binary<Buffer>, Expr.blob {}

        non-sealed interface bin extends binary<byte[]>, Expr.bin {}

        sealed interface json<T> extends Value<T>, Expr.json<T> {}

        non-sealed interface jsonObject extends json<JsonObject>, Expr.jsonObject {}

        non-sealed interface jsonArray extends json<JsonArray>, Expr.jsonArray {}
        //endregion
    }

    interface Dialect {
        char quote();

        IntObjectMap<String> constants();

        default Rendered render(Statement<?> stmt) {
            var c = new RenderContext(quote(), stmt.context());
            if (stmt instanceof Statement.Query<?> q) return query(q, c).rendered();
            if (stmt instanceof Statement.Create q) return create(q, c).rendered();
            if (stmt instanceof Statement.Update q) return update(q, c).rendered();
            if (stmt instanceof Statement.Delete q) return delete(q, c).rendered();
            if (stmt instanceof Statement.QueryCreate<?> q) return queryCreate(q, c).rendered();
            if (stmt instanceof Statement.UpdateQuery<?> q) return updateQuery(q, c).rendered();
            throw new UnsupportedOperationException("statement '" + stmt.getClass() + "' not support ");
        }

        RenderContext updateQuery(Statement.UpdateQuery<?> q, RenderContext c);

        RenderContext queryCreate(Statement.QueryCreate<?> q, RenderContext c);

        RenderContext delete(Statement.Delete q, RenderContext c);

        RenderContext update(Statement.Update q, RenderContext c);

        RenderContext create(Statement.Create q, RenderContext c);

        RenderContext query(Statement.Query<?> q, RenderContext c);
    }

    record Rendered(String query, Map<String, Object> parameters, IntObjectMap<Class<?>> holders) {}

    record Writer(StringBuilder b, char q, IntObjectMap<Object> tags) {
        public Writer(char q) {
            this(new StringBuilder(), q, new IntObjectHashMap<>());
        }

        Writer quoted(CharSequence v) {
            b.append(q).append(v).append(q);
            return this;
        }

        Writer space() {
            b.append(' ');
            return this;
        }

        Writer period() {
            b.append('.');
            return this;
        }

        Writer comma() {
            b.append(',');
            return this;
        }

        Writer backquote() {
            b.append('`');
            return this;
        }

        Writer exclamation() {
            b.append('!');
            return this;
        }

        Writer semicolon() {
            b.append(';');
            return this;
        }

        Writer colon() {
            b.append(':');
            return this;
        }

        Writer backslash() {
            b.append('\\');
            return this;
        }

        Writer slash() {
            b.append('/');
            return this;
        }

        Writer underscore() {
            b.append('_');
            return this;
        }

        Writer plus() {
            b.append('+');
            return this;
        }

        Writer minus() {
            b.append('-');
            return this;
        }

        Writer equals() {
            b.append('=');
            return this;
        }

        Writer asterisk() {
            b.append('*');
            return this;
        }

        Writer ampersand() {
            b.append('&');
            return this;
        }

        Writer pound() {
            b.append('#');
            return this;
        }

        Writer percent() {
            b.append('%');
            return this;
        }

        Writer tilde() {
            b.append('~');
            return this;
        }

        Writer dollar() {
            b.append('$');
            return this;
        }

        Writer question() {
            b.append('$');
            return this;
        }

        Writer verticalBar() {
            b.append('|');
            return this;
        }

        Writer quote(Consumer<Writer> act) {
            return surround('\'', act);
        }

        Writer quoteDouble(Consumer<Writer> act) {
            return surround('"', act);
        }

        Writer parentheses(Consumer<Writer> act) {
            return surround('(', ')', act);
        }

        Writer brackets(Consumer<Writer> act) {
            return surround('[', ']', act);
        }

        Writer braces(Consumer<Writer> act) {
            return surround('{', '}', act);
        }

        Writer write(char c) {
            b.append(c);
            return this;
        }

        Writer surround(char p, char s, Consumer<Writer> act) {
            b.append(p);
            act.accept(this);
            b.append(s);
            return this;
        }

        Writer surround(char p, Consumer<Writer> act) {
            b.append(p);
            act.accept(this);
            b.append(p);
            return this;
        }

        Writer join(char p, CharSequence... v) {
            if (v.length == 0) return this;
            for (var cs : v) {
                b.append(cs);
                b.append(p);
            }
            b.setLength(b.length() - 1);
            return this;
        }

        Writer join(char d, char p, char s, CharSequence... v) {
            if (v.length == 0) return this;
            b.append(p);
            for (var cs : v) {
                b.append(cs);
                b.append(d);
            }
            b.setLength(b.length() - 1);
            b.append(s);
            return this;
        }

        Writer tagged(Object v) {
            tags.put(b.length(), v);
            return this;
        }
        Writer resetTage(Object v){
            var t=tag(v);
            if(t<0) throw new IllegalArgumentException("tag "+v+" not found");
            b.setLength(t);
            tags.remove(t);
            return this;
        }
        int tag(Object v) {
            return StreamSupport.stream(tags.entries().spliterator(), false)
                                .filter(x -> x.value().equals(v))
                                .findFirst()
                                .map(IntObjectMap.PrimitiveEntry::key).orElse(-1);
        }
    }

    record RenderContext(Writer w
            , Map<String, Object> arguments
            , IntObjectMap<Object> parameters
            , IntObjectMap<Class<?>> holders) {
        public RenderContext(char q, IntObjectMap<Object> parameters, IntObjectMap<Class<?>> holders) {
            this(new Writer(q), new LinkedHashMap<>(), parameters, holders);
        }

        public RenderContext(char quote, BuildContext context) {
            this(quote, context.parameters, context.holders);
        }

        public Rendered rendered() {
            return new Rendered(w.b.toString(), arguments, holders);
        }
    }

    record BuildContext(IntObjectMap<Object> parameters, IntObjectMap<Class<?>> holders) {
        public BuildContext() {
            this(new IntObjectHashMap<>(), new IntObjectHashMap<>());
        }

        public int addParameter(Object v) {
            var k = parameters.size();
            parameters.put(k, v);
            return k;
        }

        public int addHolder(Class<?> v) {
            var k = holders.size();
            holders.put(k, v);
            return k;
        }

        public BuildContext addConstHolder(int k) {
            assert k < 0 : "invalid constant";
            holders.put(k, Object.class);
            return this;
        }
    }
}
