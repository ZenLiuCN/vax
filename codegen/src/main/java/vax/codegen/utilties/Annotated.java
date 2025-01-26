package vax.codegen.utilties;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Zen.Liu
 * @since 2025-01-26
 */
public interface Annotated extends Utility {
    record impl(AnnotationMirror raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv,
                AtomicReference<Map<? extends ExecutableElement, ? extends AnnotationValue>> values
    ) implements Annotated {
        public impl(RoundEnvironment roundEnv, ProcessingEnvironment procEnv, AnnotationMirror raw) {
            this(raw, roundEnv, procEnv, new AtomicReference<>());
            values.set(getElementValuesWithDefaults(raw));
        }

        @Override
        public Map<? extends ExecutableElement, ? extends AnnotationValue> value() {
            return values.get();
        }
    }

    AnnotationMirror raw();

    Map<? extends ExecutableElement, ? extends AnnotationValue> value();

    default <T> Optional<T> value(String name, Class<T> type) {
        return value().keySet().stream()
                      .filter(x -> x.getSimpleName().toString().equals(name))
                      .findFirst()
                      .map(x -> value().get(x))
                      .map(AnnotationValue::getValue)
                      .filter(type::isInstance)
                      .map(type::cast)
                ;
    }

    default Optional<String> string(String name) {
        return value(name, String.class);
    }

    default Optional<Byte> i8(String name) {
        return value(name, Byte.class);
    }

    default Optional<Short> i16(String name) {
        return value(name, Short.class);
    }

    default Optional<Integer> i32(String name) {
        return value(name, Integer.class);
    }

    default Optional<Long> i64(String name) {
        return value(name, Long.class);
    }
}
