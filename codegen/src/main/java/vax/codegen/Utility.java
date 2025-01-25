package vax.codegen;

import com.squareup.javapoet.ClassName;
import io.netty.util.internal.logging.MessageFormatter;
import lombok.SneakyThrows;
import lombok.With;
import lombok.experimental.Delegate;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zen.Liu
 * @since 2025-01-25
 */
public interface Utility  {
    //region Logging
    default void info(Element e, String formatter, Object... args) {
        var t = MessageFormatter.arrayFormat(formatter, args);
        procEnv().getMessager().printMessage(Diagnostic.Kind.NOTE, t.getMessage(), e);
    }

    @SneakyThrows
    default void error(Element e, String formatter, Object... args) {
        var t = MessageFormatter.arrayFormat(formatter, args);
        procEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, t.getMessage(), e);
        var err = t.getThrowable();
        if (err != null) throw err;
    }

    default void warn(Element e, String formatter, Object... args) {
        var t = MessageFormatter.arrayFormat(formatter, args);
        procEnv().getMessager().printMessage(Diagnostic.Kind.WARNING, t.getMessage(), e);
    }
    //endregion

    ProcessingEnvironment procEnv();

    RoundEnvironment roundEnv();


    default Types types() {
        return procEnv().getTypeUtils();
    }

    default Filer filer() {
        return procEnv().getFiler();
    }

    default Elements elements() {
        return procEnv().getElementUtils();
    }

    default TypeElement asTypeElement(Class<?> t) {
        return elements().getTypeElement(t.getCanonicalName());
    }
    default boolean isSameType(TypeMirror t0,Class<?> t) {
        return types().isSameType(t0,asTypeElement(t).asType());
    }
    default Optional<TypeElement> asTypeElement(TypeMirror t) {
        return Optional.ofNullable(types().asElement(t))
                       .filter(x -> x instanceof TypeElement)
                       .map(TypeElement.class::cast)
                ;
    }
    default PackageElement getPackageOf(Element t){
      return   elements().getPackageOf(t);
    }
    default Element asElement(TypeMirror t){
        return   types().asElement(t);
    }

    default String typeIdentity(TypeMirror t, TypeElement element) {
        Objects.requireNonNull(t);
        var n = ClassName.get(t);
        if (n.isPrimitive()) return "$" + t.getKind().name().toUpperCase();
        var tye = asTypeElement(t).orElse(null);
        if (tye == null) {
            warn(element, "unknown of {} with {}", t, element);
            return t.toString().toUpperCase()
                    .replaceAll("\\.", "_")
                    .replaceAll("\\$", "_");
        }
        var x = ClassName.get(tye);
        if (x.isBoxedPrimitive()) return x.simpleName().toUpperCase();
        var pkg = x.packageName()
                   .replace("io.vertx.core.json", "")
                   .replace("org.jooq.lambda.tuple", "")
                   .replace("java.time", "")
                   .replace("java.lang", "")
                   .replace("io.netty.util.collection", "")
                   .replace("java.util.collection", "")
                   .toUpperCase()
                   .replaceAll("\\.", "_")
                   .replaceAll("\\$", "_");
        var name = pkg + "_" + x.simpleName();
        if (t instanceof DeclaredType dt && !isSameType(t, Class.class)) {
            // info(element,"process type arguments of {}",t);
            var ta = dt.getTypeArguments()
                       .stream()
                       .map(v -> typeIdentity(v, element))
                       .collect(Collectors.joining("$"));
            if (!ta.isBlank()) name += "$$$" + ta;
        }
        return name;
    }

    @SneakyThrows
    default Path projectPath() {
        var resource = filer().createResource(StandardLocation.CLASS_OUTPUT, "", "tmp" + Instant.now().toEpochMilli(),
                                            (Element[]) null);
        var uri = resource.toUri();
        if (!Objects.equals(uri.getScheme(), "file")) {
            return Paths.get("");
        }
        var projectPath = Paths.get(resource.toUri()).getParent().getParent();
        resource.delete();
        if (projectPath.toAbsolutePath().endsWith("target")) {
            projectPath = projectPath.getParent();
        }
        return projectPath;
    }
    default <T extends Element> Stream<T> enclosed(
            Element element,
            Predicate<Element> select,
            Class<T> clazz) {
        if (element.getKind() == ElementKind.PACKAGE) {
            return Stream.concat(element.getEnclosedElements()
                                        .stream()
                                        .filter(e -> switch (e.getKind()) {
                                            case INTERFACE, CLASS, ENUM, ANNOTATION_TYPE, RECORD -> true;
                                            default -> false;
                                        })
                                        .flatMap(x -> x.getEnclosedElements().stream()),
                                 element.getEnclosedElements().stream())
                         .filter(select)
                         .filter(clazz::isInstance)
                         .map(clazz::cast);
        }
        return element.getEnclosedElements().stream()
                      .filter(select)
                      .filter(clazz::isInstance)
                      .map(clazz::cast);
    }

    interface Elemental extends Utility {
        Element raw();

        default Optional<ePackage> pkg() {
            return Optional.of(raw())
                           .filter(x -> x.getKind().isClass() || x.getKind().isInterface())
                           .map(this::getPackageOf)
                           .map(s -> new ePackage(s, roundEnv(), procEnv()));
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

        default Optional<tType> uTypeMirror() {
            return Optional.ofNullable(raw().asType())
                           .filter(x -> x.getKind() != TypeKind.ERROR)
                           .map(s -> new tType(s, roundEnv(), procEnv()));
        }

        default Optional<eType> uType() {
            return Optional.ofNullable(raw())
                           .filter(TypeElement.class::isInstance)
                           .map(TypeElement.class::cast)
                           .map(s -> new eType(s, roundEnv(), procEnv()));
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

    interface Typical extends Utility {
        TypeMirror raw();

        default Optional<eElement> element() {
            return Optional.ofNullable(raw())
                           .flatMap(this::asTypeElement)
                           .map(s -> new eElement(s, roundEnv(), procEnv()));
        }

        default Optional<eType> uType() {
            return Optional.ofNullable(raw())
                           .flatMap(this::asTypeElement)
                           .map(s -> new eType(s, roundEnv(), procEnv()));
        }
    }

    interface TypeParameter extends Utility {
        Parameterizable raw();

        default Stream<eTypeParameter> typeParameters() {
            return raw().getTypeParameters().stream()
                        .map(s -> new eTypeParameter(s, roundEnv(), procEnv()));
        }
    }
    @With
    record eElement(
            @Delegate(types = Element.class) Element raw,
            RoundEnvironment roundEnv,
            ProcessingEnvironment procEnv
    ) implements Elemental {

    }

    @With
    record eExecutable(
            @Delegate(types = ExecutableElement.class) ExecutableElement raw,
            RoundEnvironment roundEnv,
            ProcessingEnvironment procEnv
    ) implements Elemental, TypeParameter {
        public tType returns() {
            return new tType(raw.getReturnType(), roundEnv, procEnv);
        }

        public Stream<eVariable> parameters() {
            return raw.getParameters().stream()
                      .map(s -> new eVariable(s, roundEnv, procEnv));
        }

    }

    @With
    record eTypeParameter(
            @Delegate(types = TypeParameterElement.class) TypeParameterElement raw,
            RoundEnvironment roundEnv,
            ProcessingEnvironment procEnv
    ) implements Elemental {
    }

    @With
    record eModule(
            @Delegate(types = ModuleElement.class) ModuleElement raw,
            RoundEnvironment roundEnv,
            ProcessingEnvironment procEnv
    ) implements Elemental {
    }

    @With
    record eVariable(
            @Delegate(types = VariableElement.class) VariableElement raw,
            RoundEnvironment roundEnv,
            ProcessingEnvironment procEnv
    ) implements Elemental {
    }

    @With
    record eType(
            @Delegate(types = TypeElement.class) TypeElement raw,
            RoundEnvironment roundEnv,
            ProcessingEnvironment procEnv
    ) implements Elemental {
        public Stream<eExecutable> methods() {
            return enclosed(raw, e -> e.getKind() == ElementKind.METHOD, ExecutableElement.class)
                    .map(e -> new eExecutable(e, roundEnv, procEnv));
        }

        public Stream<eExecutable> constructors() {
            return enclosed(raw, e -> e.getKind() == ElementKind.CONSTRUCTOR, ExecutableElement.class)
                    .map(e -> new eExecutable(e, roundEnv, procEnv));
        }

        public Stream<eVariable> fields() {
            return enclosed(raw, e -> e.getKind() == ElementKind.FIELD, VariableElement.class)
                    .map(e -> new eVariable(e, roundEnv, procEnv));
        }
    }

    @With
    record ePackage(
            @Delegate(types = PackageElement.class) PackageElement raw,
            RoundEnvironment roundEnv,
            ProcessingEnvironment procEnv
    ) implements Elemental {
    }

    @With
    record eRecordComponent(
            @Delegate(types = RecordComponentElement.class) RecordComponentElement raw,
            RoundEnvironment roundEnv,
            ProcessingEnvironment procEnv
    ) implements Elemental {
    }

    @With
    record tType(
            @Delegate(types = TypeMirror.class) TypeMirror raw,
            RoundEnvironment roundEnv,
            ProcessingEnvironment procEnv
    ) implements Typical {

    }

    @With
    record tDeclared(
            @Delegate(types = DeclaredType.class) DeclaredType raw,
            RoundEnvironment roundEnv,
            ProcessingEnvironment procEnv
    ) implements Typical {

    }

    @With
    record uAnnotationMirror(
            @Delegate(types = AnnotationMirror.class) AnnotationMirror raw,
            RoundEnvironment roundEnv,
            ProcessingEnvironment procEnv
    ) implements Utility {
    }
}

