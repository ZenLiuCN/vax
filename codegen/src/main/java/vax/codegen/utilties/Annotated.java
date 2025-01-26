package vax.codegen.utilties;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Zen.Liu
 * @since 2025-01-26
 */
public interface Annotated extends Utility {
    interface AnnotatedValue extends Utility {
        record impl(AnnotationValue raw,
                    RoundEnvironment roundEnv,
                    ProcessingEnvironment procEnv
        ) implements AnnotatedValue {

        }

        AnnotationValue raw();

        default <T> Optional<T> value(Class<T> type) {
            return Optional.ofNullable(raw())
                           .filter(type::isInstance)
                           .map(type::cast);
        }
        default Optional<String> string() {
            return value(String.class);
        }

        default Optional<Boolean> bool() {
            return value(Boolean.class);
        }

        default Optional<Character> character() {
            return value(Character.class);
        }

        default Optional<Byte> i8() {
            return value(Byte.class);
        }

        default Optional<Short> i16() {
            return value(Short.class);
        }

        default Optional<Integer> i32() {
            return value(Integer.class);
        }

        default Optional<Long> i64() {
            return value(Long.class);
        }

        default Optional<Float> f32() {
            return value(Float.class);
        }

        default Optional<Double> f64() {
            return value(Double.class);
        }

        default Optional<TypeMirror> type() {
            return value(TypeMirror.class);
        }

        default Optional<VariableElement> enumerate() {
            return value(VariableElement.class);
        }
    }

    record impl(AnnotationMirror raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv,
                AtomicReference<Map<? extends ExecutableElement, ? extends AnnotationValue>> values
    ) implements Annotated {
        public impl(AnnotationMirror raw, RoundEnvironment roundEnv, ProcessingEnvironment procEnv) {
            this(raw, roundEnv, procEnv, new AtomicReference<>());
            values.set(raw.getElementValues());
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


    @SuppressWarnings("unchecked")
    default Optional<List<AnnotatedValue>> array(String name) {
        return value().keySet().stream()
                      .filter(x -> x.getSimpleName().toString().equals(name))
                      .findFirst()
                      .map(x -> value().get(x))
                      .filter(List.class::isInstance)
                      .map(List.class::cast)
                      .map(x -> x.isEmpty()
                              ? List.of() :
                              x.stream()
                               .map(i -> new AnnotatedValue.impl(((AnnotationValue) i), roundEnv(), procEnv()))
                               .toList())
                ;
    }

    default Optional<String> string(String name) {
        return value(name, String.class);
    }

    default Optional<Boolean> bool(String name) {
        return value(name, Boolean.class);
    }

    default Optional<Character> character(String name) {
        return value(name, Character.class);
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

    default Optional<Float> f32(String name) {
        return value(name, Float.class);
    }

    default Optional<Double> f64(String name) {
        return value(name, Double.class);
    }

    default Optional<TypeMirror> type(String name) {
        return value(name, TypeMirror.class);
    }

    default Optional<VariableElement> enumerate(String name) {
        return value(name, VariableElement.class);
    }
}
