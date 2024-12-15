package vax.query;

import io.vertx.sqlclient.Row;

import java.util.List;

/**
 * @author Zen.Liu
 * @since 2024-12-08
 */
public interface Reader {
    interface FieldReader<T> {
        T apply(Row row, int pos);
    }

    interface FieldSetter<T> {
        T apply(T o, Object v);
    }

    interface ModelReader<T> {
        List<FieldReader<?>> fields();

        List<FieldSetter<T>> setter();

        T ctor();

        default T apply(Row row, int start) {
            var c = ctor();
            var n = 0;
            for (var r : fields()) {
                c = setter().get(n).apply(c, r.apply(row, start));
                start++;
                n++;
            }
            return c;
        }
    }
}
