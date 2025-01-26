package vax.codegen.utilties;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.type.*;
import java.util.Optional;

/**
 * @author Zen.Liu
 * @since 2025-01-26
 */
public
interface Typical extends Utility {
    record impl(TypeMirror raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv) implements Typical {}

    static Typical of(TypeMirror t, RoundEnvironment roundEnv, ProcessingEnvironment procEnv) {
        if (t instanceof ExecutableType e)
            return Executable.of(e, roundEnv, procEnv);
        if (t instanceof PrimitiveType e)
            return Primitive.of(e, roundEnv, procEnv);
        if (t instanceof WildcardType e)
            return Wildcard.of(e, roundEnv, procEnv);
        if (t instanceof UnionType e)
            return Union.of(e, roundEnv, procEnv);
        if (t instanceof ArrayType e)
            return Array.of(e, roundEnv, procEnv);
        if (t instanceof TypeVariable e)
            return Variable.of(e, roundEnv, procEnv);
        if (t instanceof DeclaredType e)
            return Declared.of(e, roundEnv, procEnv);
        if (t instanceof NullType e)
            return Null.of(e, roundEnv, procEnv);
        if (t instanceof IntersectionType e)
            return Intersection.of(e, roundEnv, procEnv);
        throw new IllegalStateException("unknown type of " + t);
    }

    TypeMirror raw();

    default Optional<Elemental> element() {
        return Optional.ofNullable(raw())
                       .flatMap(this::asTypeElement)
                       .map(s -> Elemental.of(s, roundEnv(), procEnv()));
    }

    default Optional<Elemental.Type> type() {
        return Optional.ofNullable(raw())
                       .flatMap(this::asTypeElement)
                       .map(s -> new Elemental.Type.impl(s, roundEnv(), procEnv()));
    }

    interface Executable extends Typical {
        static Executable of(ExecutableType raw, RoundEnvironment roundEnv,
                             ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }

        record impl(
                ExecutableType raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv
        ) implements Executable {}

        @Override
        ExecutableType raw();
    }

    interface Primitive extends Typical {
        static Primitive of(PrimitiveType raw,
                            RoundEnvironment roundEnv,
                            ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }

        record impl(
                PrimitiveType raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv
        ) implements Primitive {}

        @Override
        PrimitiveType raw();
    }

    interface Wildcard extends Typical {
        static Wildcard of(WildcardType raw,
                           RoundEnvironment roundEnv,
                           ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }

        record impl(
                WildcardType raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv
        ) implements Wildcard {}

        @Override
        WildcardType raw();
    }

    interface Union extends Typical {
        static Union of(UnionType raw,
                        RoundEnvironment roundEnv,
                        ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }

        record impl(
                UnionType raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv
        ) implements Union {}

        @Override
        UnionType raw();
    }

    interface Array extends Typical {
        static Array of(ArrayType raw,
                        RoundEnvironment roundEnv,
                        ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }

        record impl(
                ArrayType raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv
        ) implements Array {}

        @Override
        ArrayType raw();
    }

    interface Variable extends Typical {
        static Variable of(TypeVariable raw,
                           RoundEnvironment roundEnv,
                           ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }

        record impl(
                TypeVariable raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv
        ) implements Variable {}

        @Override
        TypeVariable raw();
    }

    interface Declared extends Typical {
        static Declared of(DeclaredType raw,
                           RoundEnvironment roundEnv,
                           ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }

        record impl(
                DeclaredType raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv
        ) implements Declared {}

        @Override
        DeclaredType raw();
    }

    interface Null extends Typical {
        static Null of(NullType raw,
                       RoundEnvironment roundEnv,
                       ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }

        record impl(
                NullType raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv
        ) implements Null {}

        @Override
        NullType raw();
    }

    interface Intersection extends Typical {
        static Intersection of(IntersectionType raw,
                               RoundEnvironment roundEnv,
                               ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }

        record impl(
                IntersectionType raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv
        ) implements Intersection {}

        @Override
        IntersectionType raw();
    }
}
