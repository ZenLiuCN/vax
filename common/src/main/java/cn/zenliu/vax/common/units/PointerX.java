package cn.zenliu.vax.common.units;

import cn.zenliu.vax.common.units.FunctorX.JsonArrayReader;
import cn.zenliu.vax.common.units.FunctorX.JsonArrayWriter;
import cn.zenliu.vax.common.units.FunctorX.JsonObjectReader;
import cn.zenliu.vax.common.units.FunctorX.JsonObjectWriter;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.pointer.JsonPointer;
import io.vertx.core.json.pointer.JsonPointerIterator;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * @author Zen.Liu
 * @since 2024-11-10
 */
public interface PointerX<J> extends JsonPointer {
    static PointerX<JsonObject> of(String path, boolean createMissing) {
        return new pointer<>(JsonPointer.from(path), createMissing);
    }

    static PointerX<JsonObject> of(JsonPointer p, boolean createMissing) {
        return p instanceof PointerX<?> x ? x.forObject().withCreateMissing(createMissing) : new pointer<JsonObject>(p, createMissing);
    }

    @SuppressWarnings("unchecked")
    default PointerX<JsonObject> forObject() {
        return (PointerX<JsonObject>) this;
    }

    @SuppressWarnings("unchecked")
    default PointerX<JsonArray> forArray() {
        return (PointerX<JsonArray>) this;
    }

    PointerX<J> withCreateMissing(boolean createMissing);

    //region Overrides
    @Override
    default Object query(Object objectToQuery, JsonPointerIterator iterator) {
        return raw().query(objectToQuery, iterator);
    }

    @Override
    default boolean isRootPointer() {
        return raw().isRootPointer();
    }

    @Override
    default boolean isLocalPointer() {
        return raw().isLocalPointer();
    }

    @Override
    default boolean isParent(JsonPointer child) {
        return raw().isParent(child);
    }


    @Override
    default URI toURI() {
        return raw().toURI();
    }

    @Override
    default URI getURIWithoutFragment() {
        return raw().getURIWithoutFragment();
    }

    @Override
    default JsonPointer append(JsonPointer pointer) {
        return raw().append(pointer);
    }

    @Override
    default Object queryOrDefault(Object objectToQuery, JsonPointerIterator iterator, Object defaultValue) {
        return raw().queryOrDefault(objectToQuery, iterator, defaultValue);
    }

    @Override
    default List<Object> tracedQuery(Object objectToQuery, JsonPointerIterator iterator) {
        return raw().tracedQuery(objectToQuery, iterator);
    }

    @Override
    default Object write(Object objectToWrite, JsonPointerIterator iterator, Object newElement, boolean createOnMissing) {
        return raw().write(objectToWrite, iterator, newElement, createOnMissing);
    }

    default boolean isParent(PointerX<?> child) {
        return raw().isParent(child.raw());
    }

    @Override
    PointerX<J> append(String token);

    @Override
    PointerX<J> append(int index);

    @Override
    PointerX<J> append(List<String> tokens);


    PointerX<J> append(PointerX<?> pointer);

    @Override
    PointerX<J> parent();

    @Override
    PointerX<J> copy();

    //endregion
    JsonPointer raw();

    boolean createMissing();

    //region Primitive methods
    default Optional<Object> query(J json) {
        return Optional.ofNullable(json).map(this::queryJson);
    }

    default Optional<List<Object>> trace(J json) {
        return Optional.ofNullable(json).map(j -> tracedQuery(j, JsonPointerIterator.JSON_ITERATOR));
    }


    @SuppressWarnings("unchecked")
    default Optional<J> put(J json, Object v) {
        return Optional.ofNullable((J) write(json, JsonPointerIterator.JSON_ITERATOR, v, createMissing()));
    }

    //endregion
    //region Primitive
    default Optional<JsonObject> jsonObject(J o) {
        return query(o).map(x -> x instanceof JsonObject v ? v : null);
    }

    default Optional<J> jsonObject(J o, JsonObject v) {
        return put(o, v);
    }

    default Optional<JsonArray> jsonArray(J o) {
        return query(o).map(x -> x instanceof JsonArray v ? v : null);
    }

    default Optional<J> jsonArray(J o, JsonArray v) {
        return put(o, v);
    }

    default Optional<String> string(J o) {
        return query(o).map(x -> x instanceof String v ? v : null);
    }

    default Optional<J> string(J o, String v) {
        return put(o, v);
    }

    default Optional<Number> number(J o) {
        return query(o).map(x -> x instanceof Number v ? v : null);
    }

    default Optional<J> number(J o, Number v) {
        return put(o, v);
    }

    default Optional<Boolean> bool(J o) {
        return query(o).map(x -> x instanceof Boolean v ? v : null);
    }

    default Optional<J> bool(J o, Boolean v) {
        return put(o, v);
    }

    //endregion
    //region Advanced

    default Optional<Integer> i32(J o) {
        return query(o).map(x -> x instanceof Number v ? v.intValue() : null);
    }

    default Optional<J> i32(J o, Integer v) {
        return put(o, v);
    }


    default Optional<Long> i64(J o) {
        return query(o).map(x -> {
            try {
                return x instanceof Number v ? Long.valueOf(v.longValue()) : x instanceof String s ? Long.parseLong(s) : null;
            } catch (NumberFormatException ignore) {
                return null;
            }
        });
    }

    default Optional<J> i64(J o, Long v) {
        return put(o, v);
    }

    default Optional<Float> f32(J o) {
        return query(o).map(x -> x instanceof Number v ? v.floatValue() : null);
    }

    default Optional<J> f32(J o, Float v) {
        return put(o, v);
    }

    default Optional<Double> f64(J o) {
        return query(o).map(x -> x instanceof Number v ? v.doubleValue() : null);
    }

    default Optional<J> f64(J o, Double v) {
        return put(o, v);
    }

    default Optional<BigDecimal> dec(J o) {
        return query(o).map(x -> {
            try {
                return x instanceof Double v ? BigDecimal.valueOf(v) : x instanceof String s ? new BigDecimal(s) : null;
            } catch (NumberFormatException ignore) {
                return null;
            }
        });
    }

    default Optional<J> dec(J o, BigDecimal v) {
        return put(o, v == null ? null : v.toString());
    }

    //endregion
    //region Container

    default <V, C extends Collection<V>> Optional<C> many(J o, IntFunction<C> ctor, JsonArrayReader<V> v) {
        return jsonArray(o).map(x -> {
            var n = x.size();
            var c = ctor.apply(n);
            if (n == 0) return c;
            for (int i = 0; i < n; i++) {
                c.add(v.apply(x, i));
            }
            return c;
        });
    }

    default <V, C extends Collection<V>> Optional<J> many(J o, C c, JsonArrayWriter<V> v) {
        var n = c.size();
        if (n == 0) return put(o, JsonArray.of());
        var j = new JsonArray(new ArrayList<>(n));
        n = 0;
        for (V v1 : c) {
            v.accept(j, n, v1);
            n++;
        }
        return put(o, j);
    }


    default <K, V, C extends Map<K, V>> Optional<C> map(J o, IntFunction<C> ctor, JsonArrayReader<K> k, JsonArrayReader<V> v) {
        return jsonArray(o).map(x -> {
            var n = x.size();
            var c = ctor.apply(n);
            if (n == 0) return c;
            for (int i = 0; i < n; i += 2) {
                c.put(k.apply(x, i), v.apply(x, i + 1));
            }
            return c;
        });
    }

    default <K, V, C extends Map<K, V>> Optional<J> map(J o, C c, JsonArrayWriter<K> k, JsonArrayWriter<V> v) {
        var n = c.size();
        if (n == 0) return put(o, JsonArray.of());
        var j = new JsonArray(new ArrayList<>(n * 2));
        var i = 0;
        for (var e : c.entrySet()) {
            k.accept(j, i, e.getKey());
            i++;
            v.accept(j, i, e.getValue());
        }
        return put(o, j);
    }

    default <K, V, C extends Map<K, V>> Optional<C> mapObj(J o, IntFunction<C> ctor, Function<String, K> k, JsonObjectReader<V> v) {
        return jsonObject(o).map(j -> {
            var m = ctor.apply(j.size());
            for (var e : j) {
                m.put(k.apply(e.getKey()), v.apply(j, e.getKey()));
            }
            return m;
        });
    }

    default <K, V, C extends Map<K, V>> Optional<J> mapObj(J o, C c, Function<K, String> k, JsonObjectWriter<V> v) {
        var j = JsonObject.of();
        for (var e : c.entrySet()) {
            v.accept(j, k.apply(e.getKey()), e.getValue());
        }
        return put(o, j);
    }

    default <V> Optional<V[]> array(J o, V[] zero, JsonArrayReader<V> v) {
        return jsonArray(o).map(x -> {
            var n = x.size();
            var c = Arrays.copyOf(zero, n);
            if (n == 0) return c;
            for (int i = 0; i < n; i++) {
                c[i] = v.apply(x, i);
            }
            return c;
        });
    }

    default <V> Optional<J> array(J o, V[] c, JsonArrayWriter<V> v) {
        var n = c.length;
        if (n == 0) return put(o, JsonArray.of());
        var j = new JsonArray(new ArrayList<>(n));
        n = 0;
        for (V v1 : c) {
            v.accept(j, n, v1);
            n++;
        }
        return put(o, j);
    }

    default <V> Optional<Iterator<V>> iter(J o, JsonArrayReader<V> v) {
        return jsonArray(o).map(j -> {
            var n = j.size();
            var i = new int[]{-1};
            return new iterator<>(() -> i[0] < n, () -> v.apply(j, i[0] += 1));
        });
    }

    default <V> Optional<Iterator<V>> iterValue(J o, JsonArrayReader<V> v) {
        return jsonArray(o).map(j -> {
            var n = j.size();
            var i = new int[]{-1};
            return new iterator<>(() -> i[0] < n, () -> v.apply(j, i[0] += 2));
        });
    }

    default <V> Optional<Iterator<V>> iterKey(J o, JsonArrayReader<V> v) {
        return jsonArray(o).map(j -> {
            var n = j.size();
            var i = new int[]{-2};
            return new iterator<>(() -> i[0] < n, () -> v.apply(j, i[0] += 2));
        });
    }

    default <V> Optional<Iterator<V>> iterObjKey(J o, Function<String, V> v) {
        return jsonObject(o).map(j -> {
            var x = j.getMap().keySet().iterator();
            return new iterator<>(x::hasNext, () -> v.apply(x.next()));
        });
    }

    default <V> Optional<Iterator<V>> iterObjValue(J o, JsonObjectReader<V> v) {
        return jsonObject(o).map(j -> {
            var x = j.getMap().keySet().iterator();
            return new iterator<>(x::hasNext, () -> v.apply(j, x.next()));
        });
    }


    //endregion
    //region Enhanced
    static <V extends Enum<V>> IntFunction<V> enumOrd(Class<V> type) {
        var consts = type.getEnumConstants();
        return x -> x < 0 || x >= consts.length ? null : consts[x];
    }

    static <V extends Enum<V>> Function<String, V> enumName(Class<V> type) {
        return x -> {
            if (x.isBlank()) return null;
            try {
                return Enum.valueOf(type, x);
            } catch (Exception ignore) {
                return null;
            }
        };
    }

    default <V extends Enum<V>> Optional<V> enumOrd(J o, IntFunction<V> v) {
        return i32(o).map(v::apply);
    }

    default <V extends Enum<V>> Optional<J> enumOrd(J o, V v) {
        return put(o, v == null ? null : v.ordinal());
    }

    default <V extends Enum<V>> Optional<V> enumName(J o, Function<String, V> v) {
        return string(o).map(v);
    }

    default <V extends Enum<V>> Optional<J> enumName(J o, V v) {
        return put(o, v == null ? null : v.name());
    }

    //endregion
    record pointer<J>(JsonPointer raw, boolean createMissing) implements PointerX<J> {
        @Override
        public PointerX<J> withCreateMissing(boolean createMissing) {
            return new pointer<>(raw, createMissing);
        }

        //region Delegates
        public PointerX<J> append(String token) {
            return new pointer<>(raw.append(token), createMissing);
        }

        @Override
        public PointerX<J> append(int index) {
            return new pointer<>(raw.append(index), createMissing);
        }

        @Override
        public PointerX<J> append(List<String> tokens) {
            return new pointer<>(raw.append(tokens), createMissing);
        }

        @Override
        public PointerX<J> append(PointerX<?> pointer) {
            return new pointer<>(raw.append(pointer.raw()), createMissing);
        }

        @Override
        public PointerX<J> parent() {
            return new pointer<>(raw.parent(), createMissing);
        }

        @Override
        public PointerX<J> copy() {
            return new pointer<>(raw.copy(), createMissing);
        }


        //endregion
        @Override
        public String toString() {
            return raw().toString();
        }
    }

    record iterator<V>(BooleanSupplier more, Supplier<V> iter) implements Iterator<V> {

        @Override
        public boolean hasNext() {
            return more.getAsBoolean();
        }

        @Override
        public V next() {
            return iter.get();
        }
    }
}
