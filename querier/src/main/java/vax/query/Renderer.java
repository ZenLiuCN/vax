package vax.query;

import io.netty.util.collection.LongObjectMap;
import io.vertx.sqlclient.Row;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * renderer SPI
 *
 * @author Zen.Liu
 * @since 2024-12-08
 */
public interface Renderer {
    String identity();

    /**
     * @param stmt the statement
     * @param b    the query builder
     * @return parameters
     */
    LongObjectMap<Object> render(Stmt stmt, StringBuilder b);

    interface BuiltIn {
        /**
         * constant of current timestamp
         */
        void now(StringBuilder b);

        /**
         * quota a name
         */
        void quota(StringBuilder b, CharSequence raw);
    }

    interface EnumerationConverter {
        <T extends Enum<T>> Function<T, Object> enumTo(Class<T> type);

        <T extends Enum<T>> BiFunction<Row, Integer, T> enumFrom(Class<T> type);
    }
}
