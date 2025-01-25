package vax.common.units;

import io.vertx.core.json.JsonObject;

import java.util.Optional;

/**
 * Type converts from and into json
 * @author Zen.Liu
 * @since 2025-01-12
 */
public interface Json {
    static <T> T primitive(T v, String name) {
        if (v == null) throw DomainError.badRequest().system("corrupt primitive {}").get(name);
        return v;
    }

    static long i64j(JsonObject v, String name) {
        return Optional.ofNullable(v.getString(name))
                .map(Long::parseLong)
                .orElseThrow(() -> DomainError.badRequest().system("missing {}").get(name));
    }

    static long i64(JsonObject v, String name) {
        return Optional.ofNullable(v.getLong(name))
                .orElseThrow(() -> DomainError.badRequest().system("missing {}").get(name));
    }

    static int i32(JsonObject v, String name) {
        return primitive(v.getInteger(name), name);
    }

    static boolean bool(JsonObject v, String name) {
        return primitive(v.getBoolean(name), name);
    }

    static short i16(JsonObject v, String name) {
        return primitive(v.getNumber(name), name).shortValue();
    }

    static byte i8(JsonObject v, String name) {
        return primitive(v.getNumber(name), name).byteValue();
    }
}
