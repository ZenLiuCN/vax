package vax.codegen.utilties;

import com.squareup.javapoet.ClassName;
import io.netty.util.internal.logging.MessageFormatter;
import lombok.SneakyThrows;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.*;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zen.Liu
 * @since 2025-01-25
 */
public interface Utility extends Types, Elements, Filer {
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

    //region Delegate
    @SneakyThrows
    @Override
    default JavaFileObject createClassFile(CharSequence name, Element... originatingElements) {
        return filer().createClassFile(name, originatingElements);
    }

    @SneakyThrows
    @Override
    default JavaFileObject createSourceFile(CharSequence name, Element... originatingElements) {
        return filer().createSourceFile(name, originatingElements);
    }

    @SneakyThrows
    @Override
    default FileObject createResource(JavaFileManager.Location location, CharSequence moduleAndPkg,
                                      CharSequence relativeName, Element... originatingElements) {
        return filer().createResource(location, moduleAndPkg, relativeName, originatingElements);
    }

    @SneakyThrows
    @Override
    default FileObject getResource(JavaFileManager.Location location, CharSequence moduleAndPkg,
                                   CharSequence relativeName) {
        return filer().getResource(location, moduleAndPkg, relativeName);
    }

    @Override
    default PackageElement getPackageElement(CharSequence name) {
        return elements().getPackageElement(name);
    }

    @Override
    default TypeElement getTypeElement(CharSequence name) {
        return elements().getTypeElement(name);
    }

    @Override
    default Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(
            AnnotationMirror a) {
        return elements().getElementValuesWithDefaults(a);
    }

    @Override
    default String getDocComment(Element e) {
        return elements().getDocComment(e);
    }

    @Override
    default boolean isDeprecated(Element e) {
        return elements().isDeprecated(e);
    }

    @Override
    default Name getBinaryName(TypeElement type) {
        return elements().getBinaryName(type);
    }

    @Override
    default List<? extends Element> getAllMembers(TypeElement type) {
        return elements().getAllMembers(type);
    }

    @Override
    default List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e) {
        return elements().getAllAnnotationMirrors(e);
    }

    @Override
    default boolean hides(Element hider, Element hidden) {
        return elements().hides(hider, hidden);
    }

    @Override
    default boolean overrides(ExecutableElement overrider, ExecutableElement overridden, TypeElement type) {
        return elements().overrides(overrider, overridden, type);
    }

    @Override
    default String getConstantExpression(Object value) {
        return elements().getConstantExpression(value);
    }

    @Override
    default void printElements(Writer w, Element... elements) {
        elements().printElements(w, elements);
    }

    @Override
    default Name getName(CharSequence cs) {
        return elements().getName(cs);
    }

    @Override
    default boolean isFunctionalInterface(TypeElement type) {
        return elements().isFunctionalInterface(type);
    }

    @Override
    default boolean isSameType(TypeMirror t1, TypeMirror t2) {
        return types().isSameType(t1, t2);
    }

    @Override
    default boolean isSubtype(TypeMirror t1, TypeMirror t2) {
        return types().isSubtype(t1, t2);
    }

    @Override
    default boolean isAssignable(TypeMirror t1, TypeMirror t2) {
        return types().isAssignable(t1, t2);
    }

    @Override
    default boolean contains(TypeMirror t1, TypeMirror t2) {
        return types().contains(t1, t2);
    }

    @Override
    default boolean isSubsignature(ExecutableType m1, ExecutableType m2) {
        return types().isSubsignature(m1, m2);
    }

    @Override
    default List<? extends TypeMirror> directSupertypes(TypeMirror t) {
        return types().directSupertypes(t);
    }

    @Override
    default TypeMirror erasure(TypeMirror t) {
        return types().erasure(t);
    }

    @Override
    default TypeElement boxedClass(PrimitiveType p) {
        return types().boxedClass(p);
    }

    @Override
    default PrimitiveType unboxedType(TypeMirror t) {
        return types().unboxedType(t);
    }

    @Override
    default TypeMirror capture(TypeMirror t) {
        return types().capture(t);
    }

    @Override
    default PrimitiveType getPrimitiveType(TypeKind kind) {
        return types().getPrimitiveType(kind);
    }

    @Override
    default NullType getNullType() {
        return types().getNullType();
    }

    @Override
    default NoType getNoType(TypeKind kind) {
        return types().getNoType(kind);
    }

    @Override
    default ArrayType getArrayType(TypeMirror componentType) {
        return types().getArrayType(componentType);
    }

    @Override
    default WildcardType getWildcardType(TypeMirror extendsBound, TypeMirror superBound) {
        return types().getWildcardType(extendsBound, superBound);
    }

    @Override
    default DeclaredType getDeclaredType(TypeElement typeElem, TypeMirror... typeArgs) {
        return types().getDeclaredType(typeElem, typeArgs);
    }

    @Override
    default DeclaredType getDeclaredType(DeclaredType containing, TypeElement typeElem, TypeMirror... typeArgs) {
        return types().getDeclaredType(containing, typeElem, typeArgs);
    }

    @Override
    default TypeMirror asMemberOf(DeclaredType containing, Element element) {
        return types().asMemberOf(containing, element);
    }

    @Override
    default PackageElement getPackageOf(Element t) {
        return elements().getPackageOf(t);
    }

    @Override
    default Element asElement(TypeMirror t) {
        return types().asElement(t);
    }
    //endregion

    //region Filter
    default List<VariableElement> fields(Iterable<? extends Element> elements){
            return ElementFilter.fieldsIn(elements);
    }
    default Set<VariableElement> fields(Set<? extends Element> elements){
        return ElementFilter.fieldsIn(elements);
    }
    default List<RecordComponentElement> recordComponents(Iterable<? extends Element> elements){
        return ElementFilter.recordComponentsIn(elements);
    }
    default Set<RecordComponentElement> recordComponents(Set<? extends Element> elements){
        return ElementFilter.recordComponentsIn(elements);
    }

    default List<ExecutableElement> constructors(Iterable<? extends Element> elements){
        return ElementFilter.constructorsIn(elements);
    }
    default Set<ExecutableElement> constructors(Set<? extends Element> elements){
        return ElementFilter.constructorsIn(elements);
    }
    default List<ExecutableElement> methods(Iterable<? extends Element> elements){
        return ElementFilter.methodsIn(elements);
    }
    default Set<ExecutableElement> methods(Set<? extends Element> elements){
        return ElementFilter.methodsIn(elements);
    }
    default List<TypeElement> types(Iterable<? extends Element> elements){
        return ElementFilter.typesIn(elements);
    }
    default Set<TypeElement> types(Set<? extends Element> elements){
        return ElementFilter.typesIn(elements);
    }
    default List<PackageElement> packages(Iterable<? extends Element> elements){
        return ElementFilter.packagesIn(elements);
    }
    default Set<PackageElement> packages(Set<? extends Element> elements){
        return ElementFilter.packagesIn(elements);
    }
    default List<ModuleElement> modules(Iterable<? extends Element> elements){
        return ElementFilter.modulesIn(elements);
    }
    default Set<ModuleElement> modules(Set<? extends Element> elements){
        return ElementFilter.modulesIn(elements);
    }
    default List<ModuleElement.ExportsDirective> exports(Iterable<? extends ModuleElement.Directive> elements){
        return ElementFilter.exportsIn(elements);
    }
    default List<ModuleElement.OpensDirective> opens(Iterable<? extends ModuleElement.Directive> elements){
        return ElementFilter.opensIn(elements);
    }
    default List<ModuleElement.ProvidesDirective> provides(Iterable<? extends ModuleElement.Directive> elements){
        return ElementFilter.providesIn(elements);
    }
    default List<ModuleElement.RequiresDirective> requires(Iterable<? extends ModuleElement.Directive> elements){
        return ElementFilter.requiresIn(elements);
    }
    default List<ModuleElement.UsesDirective> uses(Iterable<? extends ModuleElement.Directive> elements){
        return ElementFilter.usesIn(elements);
    }


    //endregion


    default TypeElement asTypeElement(Class<?> t) {
        return getTypeElement(t.getCanonicalName());
    }

    default boolean isSameType(TypeMirror t0, Class<?> t) {
        return isSameType(t0, asTypeElement(t).asType());
    }

    default Optional<TypeElement> asTypeElement(TypeMirror t) {
        return Optional.ofNullable(types().asElement(t))
                       .filter(x -> x instanceof TypeElement)
                       .map(TypeElement.class::cast)
                ;
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


}

