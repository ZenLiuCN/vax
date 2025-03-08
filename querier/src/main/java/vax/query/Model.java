package vax.query;

import java.util.List;
import java.util.Map;

/**
 * @author Zen.Liu
 * @since 2025-03-07
 */
public interface Model<E> {
    /**
     * the fields assign with names
     */
    Map<String, Field<?>> $fields();

    /**
     * the order of field names
     */
    List<String> $order();

    record Virtual<T>(List<String> $order, Map<String, Field<?>> $fields, State.Status original, String name) implements
                                                                                                              Model<T> {}


    interface Alias<T> {
        T as(String alias);
    }

    interface Field<T> extends Value<T> {
        record Virtual<T>(Value<T> v, String name) implements Field<T> {}
    }
}
