package vax.query;

import io.netty.util.collection.LongObjectMap;

/**
 * Expression model
 *
 * @author Zen.Liu
 * @since 2024-12-08
 */
public interface Stmt {
    /**
     * context parameters
     */
    LongObjectMap<Object> parameters();

    /**
     * context placeholders
     */
    LongObjectMap<Class<?>> placeHolders();

    interface FinalQuery<T> extends Stmt {

    }
    interface FinalCreation extends Stmt {

    }
    interface FinalMutation extends Stmt {

    }
}
