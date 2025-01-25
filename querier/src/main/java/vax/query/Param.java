package vax.query;

/**
 * @author Zen.Liu
 * @since 2025-01-18
 */
sealed public interface Param<T> extends Value<T> {

    record param<T>(T v) implements Param<T> {}

    static <T> Value<T> of(T v) {
        return new param<>(v);
    }



    static bool of(boolean v) {
        return v ? Raw.TRUE : Raw.FALSE;
    }

}
