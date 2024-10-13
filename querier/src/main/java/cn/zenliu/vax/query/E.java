package cn.zenliu.vax.query;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.lambda.tuple.Tuple2;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.List;

@SuppressWarnings("unused")
public interface E<T> extends u.R, u.T<T> {

    @Override
    default void $render(u.Renderer renderer) {
        renderer.render(this);
    }

    interface Sets<U> extends E<U> {
        String $name();
    }

    interface Col<U, T> extends E<T> {
        String $name();

        Sets<U> $sets();
    }

    interface Op<T> extends E<T> {
        record Assign<T>(E<T> t, E<T> v) implements Op<Void> {
            @Override
            public Class<Void> $type() {
                return Void.class;
            }
        }

    }

    interface Math<T extends Number> extends Op<T>, E.Num<T> {
        E<T> t();

        default Class<T> $type() {
            return t().$type();
        }

        record PLUS<T extends Number>(E<T> t, E<T> v1) implements E.Num<T>, Math<T> {


        }

        record MINUS<T extends Number>(E<T> t, E<T> v1) implements E.Num<T>, Math<T> {


        }

        record MUL<T extends Number>(E<T> t, E<T> v1) implements E.Num<T>, Math<T> {


        }

        record DIV<T extends Number>(E<T> t, E<T> v1) implements E.Num<T>, Math<T> {


        }

        record MOD<T extends Number>(E<T> t, E<T> v1) implements E.Num<T>, Math<T> {


        }

        record NEG<T extends Number>(E<T> t) implements E.Num<T>, Math<T> {


        }
    }

    interface Bit<T extends Number> extends Math<T> {
        record AND<T extends Number>(E<T> t, E<T> v1) implements E.Num<T>, Math<T> {


        }

        record OR<T extends Number>(E<T> t, E<T> v1) implements E.Num<T>, Math<T> {


        }

        record NOT<T extends Number>(E<T> t) implements E.Num<T>, Math<T> {


        }

        record XOR<T extends Number>(E<T> t, E<T> v1) implements E.Num<T>, Math<T> {


        }
    }

    interface Logic extends Op<Boolean>, E.Bool {
        record AND(E<Boolean> t, E<Boolean> v1) implements Logic {


        }

        record OR(E<Boolean> t, E<Boolean> v1) implements Logic {


        }

        record NOT(E<Boolean> t) implements Logic {

        }
    }

    interface Comparison extends Op<Boolean>, E.Bool {

        enum TextMode {
            CaseSensitive,
            CaseInsensitive,
            NotText,
        }

        enum MatchMode {
            STARTS,
            ENDS,
            INCLUDE,
        }


        record EQ<T>(E<T> t, E<T> v1, TextMode mode) implements Comparison {
            EQ(E<T> t, E<T> v1) {
                this(t, v1, TextMode.NotText);
            }
        }

        record NE<T>(E<T> t, E<T> v1, TextMode mode) implements Comparison {
            NE(E<T> t, E<T> v1) {
                this(t, v1, TextMode.NotText);
            }
        }

        record IsNull<T>(E<T> t) implements Comparison {

        }

        record NotNull<T>(E<T> t) implements Comparison {

        }

        record GT<T>(E<T> t, E<T> v1) implements Comparison {

        }

        record LT<T>(E<T> t, E<T> v1) implements Comparison {

        }

        record GE<T>(E<T> t, E<T> v1) implements Comparison {

        }

        record LE<T>(E<T> t, E<T> v1) implements Comparison {

        }

        record IN<T>(E<T> t, List<? extends E<T>> v1) implements Comparison {

        }

        record NotIN<T>(E<T> t, List<? extends E<T>> v1) implements Comparison {

        }

        record BTW<T>(E<T> t, E<T> v1, E<T> v2) implements Comparison {

        }

        record LIKE<T>(E<T> t, E<T> v1, MatchMode mode) implements Comparison {

        }
    }

    interface Stmt<T> extends E<T> {
        record Grouping() implements E<Void> {
            @Override
            public Class<Void> $type() {
                return Void.class;
            }
        }

        record Having(E<Boolean> cond) {
        }

        record Sorting(E<? extends Number> v, boolean asc) {
        }

        interface Query<T> extends Stmt<T> {
            Boolean distinct();

            List<Col<?, ?>> cols();

            Sets<?> target();

            E<Boolean> condition();

            Tuple2<E<Integer>, E<Integer>> offsets();

            Grouping grouping();

            Having having();

            Sorting sorting();
        }

        interface Mutate extends Stmt<Void> {

        }
    }

    interface V<T> extends E<T> {
        default Bool eq(E<T> v) {
            return new Comparison.EQ<>(this, v);
        }

        default Bool ne(E<T> v) {
            return new Comparison.NE<>(this, v);
        }

        default Bool isNull() {
            return new Comparison.IsNull<>(this);
        }

        default Bool notNull() {
            return new Comparison.NotNull<>(this);
        }

        @SuppressWarnings("unchecked")
        default Bool in(E<T>... v) {
            return new Comparison.IN<>(this, List.of(v));
        }

        default Bool in(List<E<T>> v) {
            return new Comparison.IN<>(this, v);
        }

        @SuppressWarnings("unchecked")
        default Bool notIn(E<T>... v) {
            return new Comparison.NotIN<>(this, List.of(v));
        }

        default Bool notIn(List<E<T>> v) {
            return new Comparison.NotIN<>(this, v);
        }
    }

    interface Param<T> extends V<T> {
        T v();

        record Bool(Boolean v) implements Param<Boolean>, E.Bool {
        }

        record I8(Byte v) implements Param<Byte>, E.I8 {
        }

        record I16(Short v) implements Param<Short>, E.I16 {
        }

        record I32(Integer v) implements Param<Integer>, E.I32 {
        }

        record I64(Long v) implements Param<Long>, E.I64 {
        }

        record F32(Float v) implements Param<Float>, E.F32 {
        }

        record F64(Double v) implements Param<Double>, E.F64 {
        }

        record Numeric(io.vertx.sqlclient.data.Numeric v) implements Param<io.vertx.sqlclient.data.Numeric>, E.Numeric {
        }

        record Decimal(BigDecimal v) implements Param<BigDecimal>, E.Decimal {
        }

        record Text(String v) implements Param<String>, E.Text {
        }

        record Date(LocalDate v) implements Param<LocalDate>, E.Date {
        }

        record DateTime(LocalDateTime v) implements Param<LocalDateTime>, E.DateTime {
        }

        record DateTimeTZ(OffsetDateTime v) implements Param<OffsetDateTime>, E.DateTimeTZ {
        }

        record Timestamp(Instant v) implements Param<Instant>, E.Timestamp {
        }

        record JObject(JsonObject v) implements Param<JsonObject>, E.JObject {
        }

        record JArray(JsonArray v) implements Param<JsonArray>, E.JArray {
        }

        record Binary(byte[] v) implements Param<byte[]>, E.Binary {
        }

        record Blob(Buffer v) implements Param<Buffer>, E.Blob {
        }
    }

    interface Hold<T> extends V<T> {
        int n();

        record Bool(int n) implements Hold<Boolean>, E.Bool {
        }

        record I8(int n) implements Hold<Byte>, E.I8 {
        }

        record I16(int n) implements Hold<Short>, E.I16 {
        }

        record I32(int n) implements Hold<Integer>, E.I32 {
        }

        record I64(int n) implements Hold<Long>, E.I64 {
        }

        record F32(int n) implements Hold<Float>, E.F32 {
        }

        record F64(int n) implements Hold<Double>, E.F64 {
        }

        record Numeric(int n) implements Hold<io.vertx.sqlclient.data.Numeric>, E.Numeric {
        }

        record Decimal(int n) implements Hold<BigDecimal>, E.Decimal {
        }

        record Text(int n) implements Hold<String>, E.Text {
        }

        record Date(int n) implements Hold<LocalDate>, E.Date {
        }

        record DateTime(int n) implements Hold<LocalDateTime>, E.DateTime {
        }

        record DateTimeTZ(int n) implements Hold<OffsetDateTime>, E.DateTimeTZ {
        }

        record Timestamp(int n) implements Hold<Instant>, E.Timestamp {
        }

        record JObject(int n) implements Hold<JsonObject>, E.JObject {
        }

        record JArray(int n) implements Hold<JsonArray>, E.JArray {
        }

        record Binary(int n) implements Hold<byte[]>, E.Binary {
        }

        record Blob(int n) implements Hold<Buffer>, E.Blob {
        }
    }

    interface Const<T> extends V<T> {
        String s();

        record I16(String s) implements Const<Short>, E.I16 {
        }

        record I32(String s) implements Const<Integer>, E.I32 {
        }

        record I64(String s) implements Const<Long>, E.I64 {
        }

        record F32(String s) implements Const<Float>, E.F32 {
        }

        record F64(String s) implements Const<Double>, E.F64 {
        }

        record Numeric(String s) implements Const<io.vertx.sqlclient.data.Numeric>, E.Numeric {
        }

        record Decimal(String s) implements Const<BigDecimal>, E.Decimal {
        }

        record Text(String s) implements Const<String>, E.Text {
        }

        record Date(String s) implements Const<LocalDate>, E.Date {
        }

        record DateTime(String s) implements Const<LocalDateTime>, E.DateTime {
        }

        record DateTimeTZ(String s) implements Const<OffsetDateTime>, E.DateTimeTZ {
        }

        record Timestamp(String s) implements Const<Instant>, E.Timestamp {
        }

        record JObject(String s) implements Const<JsonObject>, E.JObject {
        }

        record JArray(String s) implements Const<JsonArray>, E.JArray {
        }

        record Binary(String s) implements Const<byte[]>, E.Binary {
        }

        record Blob(String s) implements Const<Buffer>, E.Blob {
        }

    }

    interface Num<T extends Number> extends V<T> {
        default Bool gt(E<T> v) {
            return new Comparison.GT<>(this, v);
        }

        default Bool lt(E<T> v) {
            return new Comparison.LT<>(this, v);
        }

        default Bool ge(E<T> v) {
            return new Comparison.GE<>(this, v);
        }

        default Bool le(E<T> v) {
            return new Comparison.LE<>(this, v);
        }

        default Bool btw(E<T> l, E<T> h) {
            return new Comparison.BTW<>(this, l, h);
        }

        default Num<T> neg() {
            return new Math.NEG<>(this);
        }

        default Num<T> plus(E<T> v) {
            return new Math.PLUS<>(this, v);
        }

        default Num<T> minus(E<T> v) {
            return new Math.MINUS<>(this, v);
        }

        default Num<T> mul(E<T> v) {
            return new Math.MUL<>(this, v);
        }

        default Num<T> div(E<T> v) {
            return new Math.DIV<>(this, v);
        }

        default Num<T> mod(E<T> v) {
            return new Math.MOD<>(this, v);
        }

        default Num<T> bAnd(E<T> v) {
            return new Bit.AND<>(this, v);
        }

        default Num<T> bOr(E<T> v) {
            return new Bit.OR<>(this, v);
        }

        default Num<T> bXor(E<T> v) {
            return new Bit.XOR<>(this, v);
        }

        default Num<T> bNot() {
            return new Bit.NOT<>(this);
        }

    }

    interface I8 extends Num<Byte> {
        @Override
        default Class<Byte> $type() {
            return Byte.class;
        }
    }

    interface I16 extends Num<Short> {
        @Override
        default Class<Short> $type() {
            return Short.class;
        }
    }

    interface I32 extends Num<Integer> {
        @Override
        default Class<Integer> $type() {
            return Integer.class;
        }
    }

    interface I64 extends Num<Long> {
        @Override
        default Class<Long> $type() {
            return Long.class;
        }
    }

    interface F32 extends Num<Float> {
        @Override
        default Class<Float> $type() {
            return Float.class;
        }
    }

    interface F64 extends Num<Double> {
        @Override
        default Class<Double> $type() {
            return Double.class;
        }
    }

    interface Decimal extends Num<BigDecimal> {
        @Override
        default Class<BigDecimal> $type() {
            return BigDecimal.class;
        }
    }

    interface Numeric extends Num<io.vertx.sqlclient.data.Numeric> {
        @Override
        default Class<io.vertx.sqlclient.data.Numeric> $type() {
            return io.vertx.sqlclient.data.Numeric.class;
        }
    }

    interface Tempo<T extends Temporal> extends E<T> {
        default Bool gt(E<T> v) {
            return new Comparison.GT<>(this, v);
        }

        default Bool lt(E<T> v) {
            return new Comparison.LT<>(this, v);
        }

        default Bool ge(E<T> v) {
            return new Comparison.GE<>(this, v);
        }

        default Bool le(E<T> v) {
            return new Comparison.LE<>(this, v);
        }

        default Bool btw(E<T> l, E<T> h) {
            return new Comparison.BTW<>(this, l, h);
        }
    }

    interface Date extends Tempo<LocalDate> {
        @Override
        default Class<LocalDate> $type() {
            return LocalDate.class;
        }
    }

    interface Time extends Tempo<LocalTime> {
        @Override
        default Class<LocalTime> $type() {
            return LocalTime.class;
        }

    }

    interface DateTime extends Tempo<LocalDateTime> {
        @Override
        default Class<LocalDateTime> $type() {
            return LocalDateTime.class;
        }
    }

    interface DateTimeTZ extends Tempo<OffsetDateTime> {
        @Override
        default Class<OffsetDateTime> $type() {
            return OffsetDateTime.class;
        }
    }

    interface Timestamp extends Tempo<Instant> {
        @Override
        default Class<Instant> $type() {
            return Instant.class;
        }
    }

    interface Bool extends V<Boolean> {
        @Override
        default Class<Boolean> $type() {
            return Boolean.class;
        }

        default Bool not() {
            return new Logic.NOT(this);
        }

        default Bool and(E<Boolean> v) {
            return new Logic.AND(this, v);
        }

        default Bool or(E<Boolean> v) {
            return new Logic.OR(this, v);
        }
    }

    interface Text extends V<String> {
        @Override
        default Class<String> $type() {
            return String.class;
        }

        @Override
        default Bool eq(E<String> v) {
            return new Comparison.EQ<>(this, v, Comparison.TextMode.CaseSensitive);
        }

        @Override
        default Bool ne(E<String> v) {
            return new Comparison.NE<>(this, v, Comparison.TextMode.CaseSensitive);
        }

        default Bool ciEq(E<String> v) {
            return new Comparison.EQ<>(this, v, Comparison.TextMode.CaseInsensitive);
        }

        default Bool ciNe(E<String> v) {
            return new Comparison.NE<>(this, v, Comparison.TextMode.CaseInsensitive);
        }

        default Bool startWith(E<String> v) {
            return new Comparison.LIKE<>(this, v, Comparison.MatchMode.STARTS);
        }

        default Bool endWith(E<String> v) {
            return new Comparison.LIKE<>(this, v, Comparison.MatchMode.ENDS);
        }

        default Bool contains(E<String> v) {
            return new Comparison.LIKE<>(this, v, Comparison.MatchMode.INCLUDE);
        }
    }

    interface Json<T> extends Op<T> {
        record JORNum<T extends Number>(E<JsonObject> v, Class<T> $type, E<String> path) implements Json<T>, Num<T> {
        }

        record JORBool(E<JsonObject> v, E<String> path) implements Json<Boolean>, Bool {
        }

        record JORText(E<JsonObject> v, E<String> path) implements Json<String>, Text {
        }

        record JORJObject(E<JsonObject> v, E<String> path) implements Json<JsonObject>, JObject {
        }

        record JORJArray(E<JsonObject> v, E<String> path) implements Json<JsonArray>, JArray {
        }

        record JORHas(E<JsonObject> v, E<String> path) implements Json<Boolean>, Bool {
        }

        record JARNum<T extends Number>(E<JsonArray> v, Class<T> $type, E<Integer> index) implements Json<T>, Num<T> {
        }

        record JARBool(E<JsonArray> v, E<Integer> index) implements Json<Boolean>, Bool {
        }

        record JARText(E<JsonArray> v, E<Integer> index) implements Json<String>, Text {
        }

        record JARJObject(E<JsonArray> v, E<Integer> index) implements Json<JsonObject>, JObject {
        }

        record JARJArray(E<JsonObject> v, E<Integer> index) implements Json<JsonArray>, JArray {
        }

        record JARHas(E<JsonArray> v, E<Integer> index) implements Json<Boolean>, Bool {
        }
    }

    interface JObject extends V<JsonObject> {
        @Override
        default Class<JsonObject> $type() {
            return JsonObject.class;
        }
    }

    interface JArray extends V<JsonArray> {
        @Override
        default Class<JsonArray> $type() {
            return JsonArray.class;
        }
    }

    interface Binary extends V<byte[]> {
        @Override
        default Class<byte[]> $type() {
            return byte[].class;
        }
    }

    interface Blob extends V<Buffer> {
        @Override
        default Class<Buffer> $type() {
            return Buffer.class;
        }
    }
}