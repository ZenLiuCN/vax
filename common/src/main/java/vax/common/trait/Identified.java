package vax.common.trait;

import io.vertx.core.json.JsonObject;

/**
 * Something with an identity of 64bit integer
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Identified extends Fields {
    String FIELD_ID_$_0Long = "id";

    static JsonObject purify(JsonObject j) {
        j.remove(FIELD_ID_$_0Long);
        return j;
    }

    Long id();
}
