package vax.common.units;

import vax.common.XElement;
import vax.common.units.FunctorX.JsonObjectAccessor;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.Optional;

/**
 * @author Zen.Liu
 * @since 2024-11-10
 */
public interface SchemaX<E extends XElement> {
    Class<E> $type();

    Map<String, JsonObjectAccessor<? super Object>> $properties();

    default JsonObject $copyProperties(JsonObject v) {
        var o = JsonObject.of();
        $properties().keySet().forEach((k) -> o.put(k, v.getValue(k)));
        return o;
    }

    @SuppressWarnings("unchecked")
    default <T> Optional<T> $get(JsonObject o, String property) {
        var acc = $properties().get(property);
        if (acc == null) return Optional.empty();
        return Optional.ofNullable((T) acc.apply(o, property));
    }

    default <T> Optional<JsonObject> $set(JsonObject o, String property, T v) {
        return Optional.ofNullable(o).map(x -> {
            var acc = $properties().get(property);
            if (acc == null) return x;
            acc.accept(o, property, v);
            return x;
        });
    }
}
