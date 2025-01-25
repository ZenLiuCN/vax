package vax.common.units;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jooq.lambda.tuple.Tuple.tuple;

/**
 * @author Zen.Liu
 * @since 2025-01-12
 */
public interface Introspect {
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

    sealed interface Func extends Introspect {
        static Func of(MethodHandle h) {
            return new fn(h, null);
        }

        static int sizeOf(List<?> v) {
            return v == null ? 0 : v.size();
        }

        record fn(MethodHandle $h, @Nullable List<Object> $curried) implements Func {
            fn(MethodHandle $h) {
                this($h, null);
            }

            Func $curry(Object v) {
                if ($curried == null) return new fn($h, new ArrayList<>(List.of(v)));
                $curried.add(v);
                return this;
            }

            @Override
            public Object[] $expands(Object... args) {
                if ($curried == null || $curried.isEmpty()) return args;
                if (args.length == 0) return $curried.toArray();
                var o = Arrays.copyOf($curried.toArray(), $curried.size() + args.length);
                System.arraycopy(args, 0, o, $curried.size(), args.length);
                return o;
            }
        }

        static Func curry(Func fn, Object v) {
            if (fn instanceof fn f) return f.$curry(v);
            var cu = fn.$curried();
            if (cu == null) return new fn(fn.$h(), new ArrayList<>(List.of(v)));
            var c = new ArrayList<>(cu);
            c.add(v);
            return new fn(fn.$h(), c);
        }

        @SneakyThrows
        static Object invoke(MethodHandle h, Object... args) {
            return h.invoke(args);
        }


        MethodHandle $h();

        @Nullable List<Object> $curried();

        Object[] $expands(Object... args);

        //region objects
        @SneakyThrows
        default Object apply(Object... args) {
            return $h().invoke($expands(args));
        }

        @SneakyThrows
        default Object applyExact(Object... args) {
            return $h().invokeExact($expands(args));
        }

        @SneakyThrows
        default Object applyArgs(Object... args) {
            return $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default Object applyArgs(List<Object> args) {
            return $h().invokeWithArguments($expands(args));
        }

        @SuppressWarnings("unchecked")
        @SneakyThrows
        default <T> T applyAs(Object... args) {
            return (T) $h().invoke($expands(args));
        }

        @SuppressWarnings("unchecked")
        @SneakyThrows
        default <T> T applyExactAs(Object... args) {
            return (T) $h().invokeExact($expands(args));
        }

        @SuppressWarnings("unchecked")
        @SneakyThrows
        default <T> T applyArgsAs(Object... args) {
            return (T) $h().invokeWithArguments($expands(args));
        }

        @SuppressWarnings("unchecked")
        @SneakyThrows
        default <T> T applyArgsAs(List<Object> args) {
            return (T) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default <T> T get(Class<T> type) {
            assert parameterCount() == 0;
            if (type.isPrimitive()) {
                if (type == int.class) return null;
            }
            return type.cast($h().invoke());
        }

        @SuppressWarnings("unchecked")
        @SneakyThrows
        default <T> T get() {
            assert parameterCount() == 0 && !returnsPrimitive();
            return (T) ($h().invoke());
        }

        //endregion

        //region primitives
        @SneakyThrows
        default boolean applyBool(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == boolean.class;
            return (boolean) $h().invoke($expands(args));
        }

        @SneakyThrows
        default boolean applyExactBool(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == boolean.class;
            return (boolean) $h().invokeExact($expands(args));
        }

        @SneakyThrows
        default boolean applyArgsBool(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == boolean.class;
            return (boolean) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default boolean applyArgsBool(List<Object> args) {
            assert parameterCount() == args.size()
                    && returnsPrimitive()
                    && returnType() == boolean.class;
            return (boolean) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default boolean bool() {
            assert parameterCount() == 0
                    && returnsPrimitive()
                    && returnType() == boolean.class;
            return (boolean) $h().invoke();
        }

        @SneakyThrows
        default char applyRune(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == char.class;
            return (char) $h().invoke($expands(args));
        }

        @SneakyThrows
        default char applyExactRune(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == char.class;
            return (char) $h().invokeExact($expands(args));
        }

        @SneakyThrows
        default char applyArgsRune(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == char.class;
            return (char) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default char applyArgsRune(List<Object> args) {
            assert parameterCount() == args.size()
                    && returnsPrimitive()
                    && returnType() == char.class;
            return (char) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default char rune() {
            assert parameterCount() == 0
                    && returnsPrimitive()
                    && returnType() == char.class;
            return (char) $h().invoke();
        }

        @SneakyThrows
        default byte applyI8(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == byte.class;
            return (byte) $h().invoke($expands(args));
        }

        @SneakyThrows
        default byte applyExactI8(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == byte.class;
            return (byte) $h().invokeExact($expands(args));
        }

        @SneakyThrows
        default byte applyArgsI8(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == byte.class;
            return (byte) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default byte applyArgsI8(List<Object> args) {
            assert parameterCount() == args.size()
                    && returnsPrimitive()
                    && returnType() == byte.class;
            return (byte) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default byte i8() {
            assert parameterCount() == 0
                    && returnsPrimitive()
                    && returnType() == byte.class;
            return (byte) $h().invoke();
        }

        @SneakyThrows
        default short applyI16(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == short.class;
            return (short) $h().invoke($expands(args));
        }

        @SneakyThrows
        default short applyExactI16(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == short.class;
            return (short) $h().invokeExact($expands(args));
        }

        @SneakyThrows
        default short applyArgsI16(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == short.class;
            return (short) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default short applyArgsI16(List<Object> args) {
            assert parameterCount() == args.size()
                    && returnsPrimitive()
                    && returnType() == short.class;
            return (short) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default short i16() {
            assert parameterCount() == 0
                    && returnsPrimitive()
                    && returnType() == short.class;
            return (short) $h().invoke();
        }

        @SneakyThrows
        default int applyI32(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == int.class;
            return (int) $h().invoke($expands(args));
        }

        @SneakyThrows
        default int applyExactI32(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == int.class;
            return (int) $h().invokeExact($expands(args));
        }

        @SneakyThrows
        default int applyArgsI32(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == int.class;
            return (int) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default int applyArgsI32(List<Object> args) {
            assert parameterCount() == args.size()
                    && returnsPrimitive()
                    && returnType() == int.class;
            return (int) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default int i32() {
            assert parameterCount() == 0
                    && returnsPrimitive()
                    && returnType() == int.class;
            return (int) $h().invoke();
        }

        @SneakyThrows
        default long applyI64(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == long.class;
            return (long) $h().invoke($expands(args));
        }

        @SneakyThrows
        default long applyExactI64(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == long.class;
            return (long) $h().invokeExact($expands(args));
        }

        @SneakyThrows
        default long applyArgsI64(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == long.class;
            return (long) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default long applyArgsI64(List<Object> args) {
            assert parameterCount() == args.size()
                    && returnsPrimitive()
                    && returnType() == long.class;
            return (long) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default long i64() {
            assert parameterCount() == 0
                    && returnsPrimitive()
                    && returnType() == long.class;
            return (long) $h().invoke();
        }

        @SneakyThrows
        default float applyF32(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == float.class;
            return (float) $h().invoke($expands(args));
        }

        @SneakyThrows
        default float applyExactF32(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == float.class;
            return (float) $h().invokeExact($expands(args));
        }

        @SneakyThrows
        default float applyArgsF32(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == float.class;
            return (float) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default float applyArgsF32(List<Object> args) {
            assert parameterCount() == args.size()
                    && returnsPrimitive()
                    && returnType() == float.class;
            return (float) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default float f32() {
            assert parameterCount() == 0
                    && returnsPrimitive()
                    && returnType() == float.class;
            return (float) $h().invoke();
        }

        @SneakyThrows
        default double applyF64(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == double.class;
            return (double) $h().invoke($expands(args));
        }

        @SneakyThrows
        default double applyExactF64(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == double.class;
            return (double) $h().invokeExact($expands(args));
        }

        @SneakyThrows
        default double applyArgsF64(Object... args) {
            assert parameterCount() == args.length
                    && returnsPrimitive()
                    && returnType() == double.class;
            return (double) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default double applyArgsF64(List<Object> args) {
            assert parameterCount() == args.size()
                    && returnsPrimitive()
                    && returnType() == double.class;
            return (double) $h().invokeWithArguments($expands(args));
        }

        @SneakyThrows
        default double f64() {
            assert parameterCount() == 0
                    && returnsPrimitive()
                    && returnType() == double.class;
            return (double) $h().invoke();
        }
        //endregion

        default Func currying(Object v) {
            assert parameterCount() > sizeOf($curried());
            return curry(this, v);
        }

        //region curry primitives
        default Func currying(boolean v) {
            assert parameterCount() > sizeOf($curried());
            return curry(this, v);
        }

        default Func currying(char v) {
            assert parameterCount() > sizeOf($curried());
            return curry(this, v);
        }

        default Func currying(byte v) {
            assert parameterCount() > sizeOf($curried());
            return curry(this, v);
        }

        default Func currying(short v) {
            assert parameterCount() > sizeOf($curried());
            return curry(this, v);
        }

        default Func currying(int v) {
            assert parameterCount() > sizeOf($curried());
            return curry(this, v);
        }

        default Func currying(long v) {
            assert parameterCount() > sizeOf($curried());
            return curry(this, v);
        }

        default Func currying(float v) {
            assert parameterCount() > sizeOf($curried());
            return curry(this, v);
        }

        default Func currying(double v) {
            assert parameterCount() > sizeOf($curried());
            return curry(this, v);
        }
        //endregion

//        static void primitives() {
//            Toolkit.getDefaultToolkit()
//                   .getSystemClipboard()
//                   .setContents(new StringSelection(Seq.of(
//                                                               tuple("boolean", "Bool")
//                                                               , tuple("char", "Rune")
//                                                               , tuple("byte", "I8")
//                                                               , tuple("short", "I16")
//                                                               , tuple("int", "I32")
//                                                               , tuple("long", "I64")
//                                                               , tuple("float", "F32")
//                                                               , tuple("double", "F64")
//                                                          )
//                                                       .map(u -> """
//                                                                       @SneakyThrows
//                                                                       default %1$s apply%2$s(Object... args) {
//                                                                          assert parameterCount() ==args.length
//                                                                           && returnsPrimitive()
//                                                                           && returnType() == %1$s.class;
//                                                                           return (%1$s) $h().invoke($expands(args));
//                                                                       }
//
//                                                                       @SneakyThrows
//                                                                       default %1$s applyExact%2$s(Object... args) {
//                                                                          assert parameterCount() ==args.length
//                                                                           && returnsPrimitive()
//                                                                           && returnType() == %1$s.class;
//                                                                           return (%1$s) $h().invokeExact($expands(args));
//                                                                       }
//
//                                                                       @SneakyThrows
//                                                                       default %1$s applyArgs%2$s(Object... args) {
//                                                                          assert parameterCount() ==args.length
//                                                                           && returnsPrimitive()
//                                                                           && returnType() == %1$s.class;
//                                                                           return (%1$s) $h().invokeWithArguments($expands(args));
//                                                                       }
//
//                                                                       @SneakyThrows
//                                                                       default %1$s applyArgs%2$s(List<Object> args) {
//                                                                          assert parameterCount() ==args.size()
//                                                                           && returnsPrimitive()
//                                                                           && returnType() == %1$s.class;
//                                                                           return (%1$s) $h().invokeWithArguments($expands(args));
//                                                                       }
//                                                                       @SneakyThrows
//                                                                       default %1$s %3$s() {
//                                                                           assert parameterCount() == 0
//                                                                           && returnsPrimitive()
//                                                                           && returnType() == %1$s.class;
//                                                                           return (%1$s) $h().invoke();
//                                                                       }
//                                                               """.formatted(
//                                                               u.v1
//                                                               , u.v2
//                                                               ,
//                                                               Character.toLowerCase(
//                                                                       u.v2.charAt(
//                                                                               0)) + u.v2.substring(
//                                                                       1))
//                                                           )
//                                                       .toString("\n", "//region primitives\n", "//endregion")), null);
//        }
//
//        static void curry() {
//            var pri = List.of(tuple("boolean", "Bool")
//                    , tuple("char", "Rune")
//                    , tuple("byte", "I8")
//                    , tuple("short", "I16")
//                    , tuple("int", "I32")
//                    , tuple("long", "I64")
//                    , tuple("float", "F32")
//                    , tuple("double", "F64"));
//            Toolkit.getDefaultToolkit()
//                   .getSystemClipboard()
//                   .setContents(new StringSelection(Seq.seq(pri)
//                                                       .map(u -> """
//                                                               default Func currying(%1$s v) {
//                                                                        assert parameterCount() > sizeOf($curried());
//                                                                        return curry(this, v);
//                                                                    }
//                                                               """.formatted(
//                                                               u.v1
//                                                                            ))
//                                                       .toString("\n", "//region curry primitives\n", "//endregion")),
//                                null);
//        }
//
//        public static void main(String[] args) {
//            curry() ;
//        }


        //region MethodType
        default boolean returnsPrimitive() {
            return $h().type().returnType().isPrimitive();
        }

        default boolean hasPrimitive() {
            return $h().type().hasPrimitives();
        }

        default boolean hasWrappers() {
            return $h().type().hasWrappers();
        }

        default int parameterCount() {
            return $h().type().parameterCount();
        }

        default Class<?> returnType() {
            return $h().type().returnType();
        }

        default Class<?>[] parameters() {
            return $h().type().parameterArray();
        }

        default List<Class<?>> parametersList() {
            return $h().type().parameterList();
        }
        //endregion

        default Func bind(Object self) {
            return new fn($h().bindTo(self));
        }
    }

    public static void main(String[] args) {
        var z = MethodHandles.zero(int.class);
        var v = Func.of(z).i32();
        System.out.println(v);
    }

}
