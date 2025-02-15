package vax.common.units;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @author Zen.Liu
 * @since 2025-02-10
 */
public interface Fn {
    /**
     * use Consumer as UnaryOperator
     */
    static <T> UnaryOperator<T> accept(Consumer<T> v) {
        return t -> {
            v.accept(t);
            return t;
        };
    }
    static <T> Function<Optional<T>,T> require(Supplier<? extends RuntimeException> error) {
        return t -> t.orElseThrow(error);
    }
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    static <T> T orNull(Optional<T> v) {
        return v.orElse(null);
    }
    static <T> Function<T,Optional<T>> maybe() {
        return Optional::ofNullable;
    }
}
