package vax.query;

import io.netty.util.collection.LongObjectMap;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Zen.Liu
 * @since 2025-02-15
 */
public interface Dialect {
    char quote();

    record Rendered(
            CharSequence sql,
            Map<String, Object> parameter,
            LongObjectMap<Class<?>> placeHolder
    ) {}

    record Context(
            Writer q,
            Map<String, Object> p,
            LongObjectMap<Class<?>> placeHolder,
            LongObjectMap<Object> parameter
    ) {
        Context(char quote, Statement<?> stmt) {
            this(new Writer.SqlWriter(quote), new LinkedHashMap<>(), stmt.placeholders(), stmt.parameters());
        }

        public Rendered render() {
            return new Rendered(q.$b().toString(), p, placeHolder);
        }
    }

    default Rendered render(Statement<?> statement) {
        var ctx = new Context(quote(), statement);
        if (statement instanceof Statement.Query<?> q) {
            return query(q, ctx).render();
        } else if (statement instanceof Statement.Delete q) {
            return this.delete(q, ctx).render();
        } else if (statement instanceof Statement.Update q) {
            return this.update(q, ctx).render();
        } else if (statement instanceof Statement.Create q) {
            return this.create(q, ctx).render();
        } else throw new UnsupportedOperationException("statement of type " + statement.getClass());
    }

    default Context create(Statement.Create q, Context ctx) {

        return null;
    }

    Context update(Statement.Update q, Context ctx);

    Context delete(Statement.Delete q, Context ctx);

    Context query(Statement.Query<?> q, Context ctx);

    interface MySQL extends Dialect {

    }


    sealed interface Writer {
        StringBuilder $b();

        char $quote();

        default Writer quote(Object v) {
            var q = $quote();
            $b().append(q).append(v).append(q);
            return this;
        }


        record SqlWriter(StringBuilder $b, char $quote) implements Writer {
            SqlWriter(char quote) {
                this(new StringBuilder(), quote);
            }
        }
    }
}
