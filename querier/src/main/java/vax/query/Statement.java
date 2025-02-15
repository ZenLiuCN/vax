package vax.query;

import io.netty.util.collection.LongObjectMap;

/**
 * @author Zen.Liu
 * @since 2025-01-18
 */
public interface Statement<T> extends Expr<T> {
    LongObjectMap<Object> parameters();

    LongObjectMap<Class<?>> placeholders();

    interface Query<T> extends Statement<T> {

    }

    interface Update extends Statement<Integer> {}

    interface Delete extends Statement<Integer> {}

    interface Create extends Statement<Integer> {}
}
