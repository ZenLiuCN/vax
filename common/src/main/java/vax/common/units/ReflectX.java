package vax.common.units;

import lombok.SneakyThrows;

import java.lang.reflect.Array;

/**
 * @author Zen.Liu
 * @since 2025-01-12
 */
public interface ReflectX {
    @SuppressWarnings("unchecked")
    @SneakyThrows
    static <T> Class<T> forName(String name) {
        return (Class<T>) Class.forName(name);
    }
    @SuppressWarnings("unchecked")
    @SneakyThrows
    static <T> Class<T[]> array(Class<T> type) {
      return (Class<T[]>) Array.newInstance(type, 0).getClass();
    }

}
