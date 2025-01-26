package vax.codegen.utilties;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * @author Zen.Liu
 * @since 2025-01-26
 */
public
interface Elemental extends Utility {
    record impl(
            Element raw,
            RoundEnvironment roundEnv,
            ProcessingEnvironment procEnv
    ) implements Elemental {}

    static Elemental of(Element e, RoundEnvironment roundEnv, ProcessingEnvironment procEnv) {
        if (e instanceof TypeElement x) return Type.of(x, roundEnv, procEnv);
        if (e instanceof PackageElement x) return Package.of(x, roundEnv, procEnv);
        if (e instanceof ModuleElement x) return Module.of(x, roundEnv, procEnv);
        if (e instanceof ExecutableElement x) return Executable.of(x, roundEnv, procEnv);
        if (e instanceof VariableElement x) return Variable.of(x, roundEnv, procEnv);
        if (e instanceof TypeParameterElement x) return TypeParameter.of(x, roundEnv, procEnv);
        throw new IllegalStateException("unknown element " + e);
    }

    interface Type extends Elemental {
        @Override
        TypeElement raw();

        record impl(TypeElement raw, RoundEnvironment roundEnv, ProcessingEnvironment procEnv) implements Type {}

        static Type of(TypeElement raw,
                       RoundEnvironment roundEnv,
                       ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }
    }

    interface Package extends Elemental {
        static Package of(PackageElement raw,
                          RoundEnvironment roundEnv,
                          ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }

        @Override
        PackageElement raw();

        record impl(PackageElement raw, RoundEnvironment roundEnv, ProcessingEnvironment procEnv) implements
                                                                                                  Package {}
    }

    interface Variable extends Elemental {
        static Variable of(VariableElement raw,
                           RoundEnvironment roundEnv,
                           ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }

        @Override
        VariableElement raw();

        record impl(VariableElement raw, RoundEnvironment roundEnv, ProcessingEnvironment procEnv) implements
                                                                                                   Variable {}
    }

    interface Parameterized extends Utility {}

    interface TypeParameter extends Elemental {
        static TypeParameter of(TypeParameterElement raw,
                                RoundEnvironment roundEnv,
                                ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }

        @Override
        TypeParameterElement raw();

        record impl(
                TypeParameterElement raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv
        ) implements TypeParameter {}
    }

    interface Executable extends Elemental {
        static Executable of(ExecutableElement raw,
                             RoundEnvironment roundEnv,
                             ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }

        @Override
        ExecutableElement raw();

        record impl(
                ExecutableElement raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv
        ) implements Executable {}
    }

    interface Module extends Elemental {
        static Module of(ModuleElement raw,
                         RoundEnvironment roundEnv,
                         ProcessingEnvironment procEnv) {
            return new impl(raw, roundEnv, procEnv);
        }

        @Override
        ModuleElement raw();

        record impl(
                ModuleElement raw,
                RoundEnvironment roundEnv,
                ProcessingEnvironment procEnv
        ) implements Module {}
    }

    Element raw();

    default Optional<Package> pkg() {
        return Optional.of(raw())
                       .filter(x -> x.getKind().isClass() || x.getKind().isInterface())
                       .map(this::getPackageOf)
                       .map(s -> new Package.impl(s, roundEnv(), procEnv()));
    }

    default boolean isInterface() {
        return raw().getKind().isInterface();
    }

    default boolean isAnnotation() {
        return raw().getKind() == ElementKind.ANNOTATION_TYPE;
    }

    default boolean isClass() {
        return raw().getKind().isClass();
    }

    default boolean isEnum() {
        return raw().getKind() == ElementKind.ENUM;
    }

    default boolean isRecord() {
        return raw().getKind() == ElementKind.RECORD;
    }

    default boolean isField() {
        return raw().getKind() == ElementKind.FIELD;
    }

    default boolean isEnumConstant() {
        return raw().getKind() == ElementKind.ENUM_CONSTANT;
    }

    default boolean isMethod() {
        return raw().getKind() == ElementKind.METHOD;
    }

    default boolean isConstructor() {
        return raw().getKind() == ElementKind.CONSTRUCTOR;
    }

    default boolean isParameter() {
        return raw().getKind() == ElementKind.PARAMETER;
    }

    default boolean isPackage() {
        return raw().getKind() == ElementKind.PACKAGE;
    }

    default Optional<Typical> typical() {
        return Optional.ofNullable(raw().asType())
                       .filter(x -> x.getKind() != TypeKind.ERROR)
                       .map(s -> Typical.of(s, roundEnv(), procEnv()));
    }

    default Optional<Type> type() {
        return Optional.ofNullable(raw())
                       .filter(TypeElement.class::isInstance)
                       .map(TypeElement.class::cast)
                       .map(s -> new Type.impl(s, roundEnv(), procEnv()));
    }

    default Set<Modifier> modifiers() {
        return raw().getModifiers();
    }

    default boolean modifier(Modifier modifier) {
        return modifiers().contains(modifier);
    }

    default boolean modifiers(Modifier... modifier) {
        return modifiers().containsAll(Arrays.asList(modifier));
    }

    default boolean isPublic() {return modifier(Modifier.PUBLIC);}

    default boolean isProtected() {return modifier(Modifier.PROTECTED);}

    default boolean isPrivate() {return modifier(Modifier.PRIVATE);}

    default boolean isAbstract() {return modifier(Modifier.ABSTRACT);}

    default boolean isDefault() {return modifier(Modifier.DEFAULT);}

    default boolean isStatic() {return modifier(Modifier.STATIC);}

    default boolean isSealed() {return modifier(Modifier.SEALED);}

    default boolean isNonSealed() {return modifier(Modifier.NON_SEALED);}

    default boolean isFinal() {return modifier(Modifier.FINAL);}

    default boolean isVolatile() {return modifier(Modifier.VOLATILE);}

    default boolean isSynchronized() {return modifier(Modifier.SYNCHRONIZED);}

    default boolean isNative() {return modifier(Modifier.NATIVE);}

}
