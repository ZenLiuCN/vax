package vax.common.trait;

import io.vertx.core.json.JsonObject;

/**
 * Something with a version
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Versioned extends Identified , Fields {
    String FIELD_VERSION_$I32 = "version";

    static JsonObject purify(JsonObject j) {
        Identified.purify(j);
        j.remove(FIELD_VERSION_$I32);
        return j;
    }

    int version();
}
