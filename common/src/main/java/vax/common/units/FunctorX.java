package vax.common.units;

import com.google.auto.service.AutoService;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.SneakyThrows;
import org.jooq.lambda.function.Consumer3;

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * @author Zen.Liu
 * @since 2024-11-10
 */
public interface FunctorX {
    default String $type() {
        return this.getClass().getSimpleName();
    }

    default boolean $factory() {
        return false;
    }

    default Function<JsonObject, FunctorX> $make() {
        throw new UnsupportedOperationException("should have implement");
    }

    static Optional<FunctorX> resolve(String type, JsonObject conf) {
        return $.resolve(type, conf);
    }

    final class $ {
        static Map<String, Function<JsonObject, FunctorX>> registry;
        static final Object init = new Object();

        static Optional<FunctorX> resolve(String type, JsonObject conf) {
            if (registry == null) {
                synchronized (init) {
                    ServiceLoader.load(FunctorX.class)
                            .forEach(f -> {
                                if (f.$factory()) {
                                    registry.put(f.$type(), f.$make());
                                }
                            });
                }
            }
            return Optional.ofNullable(registry.get(type)).map(f -> f.apply(conf));
        }
    }

    interface JsonArrayReader<T> extends BiFunction<JsonArray, Integer, T>, FunctorX {
        T apply(JsonArray o, int i);

        @Override
        default T apply(JsonArray objects, Integer integer) {
            return apply(objects, (int) integer);
        }

    }

    interface JsonArrayWriter<T> extends Consumer3<JsonArray, Integer, T>, FunctorX {
        void accept(JsonArray o, int i, T v);

        @Override
        default void accept(JsonArray v1, Integer v2, T v3) {
            accept(v1, (int) v2, v3);
        }
    }

    interface JsonObjectReader<T> extends BiFunction<JsonObject, String, T>, FunctorX {
        T apply(JsonObject o, String k);

    }

    interface JsonObjectWriter<T> extends Consumer3<JsonObject, String, T>, FunctorX {
        void accept(JsonObject o, String k, T v);
    }

    interface JsonObjectAccessor<T> extends JsonObjectWriter<T>, JsonObjectReader<T> {

    }

    interface ObjectProperty<T> extends FunctorX {
        @Override
        default boolean $factory() {
            return true;
        }

        ObjectProperty<T> $make(String field);

        @Override
        default Function<JsonObject, FunctorX> $make() {
            return c -> $make(c.getString("property"));
        }

        JsonObject set(JsonObject o, T v);

        T get(JsonObject o);

        abstract class Base<T> implements ObjectProperty<T> {
            protected final JsonObjectReader<T> r;
            protected final JsonObjectWriter<T> w;
            protected final String k;

            protected Base(JsonObjectReader<T> r, JsonObjectWriter<T> w, String k) {
                this.r = r;
                this.w = w;
                this.k = k;
            }

            @Override
            public JsonObject set(JsonObject o, T v) {
                w.accept(o, k, v);
                return o;
            }

            @Override
            public T get(JsonObject o) {
                return r.apply(o, k);
            }
        }

        abstract class Simple<T> extends Base<T> {
            @SuppressWarnings("unchecked")
            @SneakyThrows
            static <T> Simple<T> invoke(Constructor<?> ctr, String s) {
                return (Simple<T>) ctr.newInstance(s);
            }

            protected final Function<String, Simple<T>> ctor;

            @SneakyThrows
            protected Simple(JsonObjectReader<T> r, String k) {
                super(r, JsonObject::put, k);
                var c = this.getClass().getConstructor(String.class);
                ctor = f -> invoke(c, f);
            }

            @Override
            public ObjectProperty<T> $make(String field) {
                return ctor.apply(field);
            }
        }

        @AutoService(FunctorX.class)
        final class ArrayObjectProperty extends Simple<JsonArray> {
            public ArrayObjectProperty() {
                this("");
            }

            public ArrayObjectProperty(String k) {
                super(JsonObject::getJsonArray, k);
            }

        }
        @AutoService(FunctorX.class)
        final class ObjectObjectProperty extends Simple<JsonObject> {
            public ObjectObjectProperty() {
                this("");
            }

            public ObjectObjectProperty(String k) {
                super(JsonObject::getJsonObject, k);
            }

        }
        @AutoService(FunctorX.class)
        final class StringObjectProperty extends Simple<String> {
            public StringObjectProperty() {
                this("");
            }

            public StringObjectProperty(String k) {
                super(JsonObject::getString, k);
            }

        }

        @AutoService(FunctorX.class)
        final class BooleanObjectProperty extends Simple<Boolean> {
            public BooleanObjectProperty() {
                this("");
            }

            public BooleanObjectProperty(String k) {
                super(JsonObject::getBoolean, k);
            }

        }

        @AutoService(FunctorX.class)
        final class Int32ObjectProperty extends Simple<Integer> {
            public Int32ObjectProperty() {
                this("");
            }

            public Int32ObjectProperty(String k) {
                super(JsonObject::getInteger, k);
            }

        }

        @AutoService(FunctorX.class)
        final class Int64ObjectProperty extends Simple<Long> {
            public Int64ObjectProperty() {
                this("");
            }

            public Int64ObjectProperty(String k) {
                super(JsonObject::getLong, k);
            }

        }
        @AutoService(FunctorX.class)
        final class Float64ObjectProperty extends Simple<Double> {
            public Float64ObjectProperty() {
                this("");
            }

            public Float64ObjectProperty(String k) {
                super(JsonObject::getDouble, k);
            }

        }
        @AutoService(FunctorX.class)
        final class Float32ObjectProperty extends Simple<Float> {
            public Float32ObjectProperty() {
                this("");
            }

            public Float32ObjectProperty(String k) {
                super(JsonObject::getFloat, k);
            }

        }
        @AutoService(FunctorX.class)
        final class BinaryObjectProperty extends Simple<byte[]> {
            public BinaryObjectProperty() {
                this("");
            }

            public BinaryObjectProperty(String k) {
                super(JsonObject::getBinary, k);
            }

        }
        @AutoService(FunctorX.class)
        final class BlobObjectProperty extends Simple<Buffer> {
            public BlobObjectProperty() {
                this("");
            }

            public BlobObjectProperty(String k) {
                super(JsonObject::getBuffer, k);
            }

        }
        @AutoService(FunctorX.class)
        final class InstantObjectProperty extends Simple<Instant> {
            public InstantObjectProperty() {
                this("");
            }

            public InstantObjectProperty(String k) {
                super(JsonObject::getInstant, k);
            }

        }
    }

    interface ArrayProperty<T> extends FunctorX {
        @Override
        default boolean $factory() {
            return true;
        }

        ArrayProperty<T> $make(int field);

        @Override
        default Function<JsonObject, FunctorX> $make() {
            return c -> $make(c.getInteger("index"));
        }

        JsonArray set(JsonArray o, T v);

        T get(JsonArray o);


        abstract class Base<T> implements ArrayProperty<T> {
            protected final JsonArrayReader<T> r;
            protected final JsonArrayWriter<T> w;
            protected final int k;

            protected Base(JsonArrayReader<T> r, JsonArrayWriter<T> w, int k) {
                this.r = r;
                this.w = w;
                this.k = k;
            }

            @Override
            public JsonArray set(JsonArray o, T v) {
                w.accept(o, k, v);
                return o;
            }

            @Override
            public T get(JsonArray o) {
                return r.apply(o, k);
            }
        }

        abstract class Simple<T> extends Base<T> {
            @SuppressWarnings("unchecked")
            @SneakyThrows
            static <T> Simple<T> invoke(Constructor<?> ctr, int s) {
                return (Simple<T>) ctr.newInstance(s);
            }

            protected final IntFunction<Simple<T>> ctor;

            @SneakyThrows
            protected Simple(JsonArrayReader<T> r, int k) {
                super(r, JsonArray::set, k);
                var c = this.getClass().getConstructor(int.class);
                ctor = f -> invoke(c, f);
            }

            @Override
            public ArrayProperty<T> $make(int field) {
                return ctor.apply(field);
            }
        }

        @AutoService(FunctorX.class)
        final class ArrayArrayProperty extends Simple<JsonArray> {
            public ArrayArrayProperty() {
                this(-1);
            }

            public ArrayArrayProperty(int k) {
                super(JsonArray::getJsonArray, k);
            }

        }
        @AutoService(FunctorX.class)
        final class ObjectArrayProperty extends Simple<JsonObject> {
            public ObjectArrayProperty() {
                this(-1);
            }

            public ObjectArrayProperty(int k) {
                super(JsonArray::getJsonObject, k);
            }

        }
        @AutoService(FunctorX.class)
        final class StringArrayProperty extends Simple<String> {
            public StringArrayProperty() {
                this(-1);
            }

            public StringArrayProperty(int k) {
                super(JsonArray::getString, k);
            }

        }
        @AutoService(FunctorX.class)
        final class BooleanArrayProperty extends Simple<Boolean> {
            public BooleanArrayProperty() {
                this(-1);
            }

            public BooleanArrayProperty(int k) {
                super(JsonArray::getBoolean, k);
            }

        }
        @AutoService(FunctorX.class)
        final class Int32ArrayProperty extends Simple<Integer> {
            public Int32ArrayProperty() {
                this(-1);
            }

            public Int32ArrayProperty(int k) {
                super(JsonArray::getInteger, k);
            }

        }

        @AutoService(FunctorX.class)
        final class Int64ArrayProperty extends Simple<Long> {
            public Int64ArrayProperty() {
                this(-1);
            }

            public Int64ArrayProperty(int k) {
                super(JsonArray::getLong, k);
            }

        }
        @AutoService(FunctorX.class)
        final class Float64ArrayProperty extends Simple<Double> {
            public Float64ArrayProperty() {
                this(-1);
            }

            public Float64ArrayProperty(int k) {
                super(JsonArray::getDouble, k);
            }

        }
        @AutoService(FunctorX.class)
        final class Float32ArrayProperty extends Simple<Float> {
            public Float32ArrayProperty() {
                this(-1);
            }

            public Float32ArrayProperty(int k) {
                super(JsonArray::getFloat, k);
            }

        }
        @AutoService(FunctorX.class)
        final class BinaryArrayProperty extends Simple<byte[]> {
            public BinaryArrayProperty() {
                this(-1);
            }

            public BinaryArrayProperty(int k) {
                super(JsonArray::getBinary, k);
            }

        }
        @AutoService(FunctorX.class)
        final class BlobArrayProperty extends Simple<Buffer> {
            public BlobArrayProperty() {
                this(-1);
            }

            public BlobArrayProperty(int k) {
                super(JsonArray::getBuffer, k);
            }

        }
        @AutoService(FunctorX.class)
        final class InstantArrayProperty extends Simple<Instant> {
            public InstantArrayProperty() {
                this(-1);
            }

            public InstantArrayProperty(int k) {
                super(JsonArray::getInstant, k);
            }

        }
    }
}
