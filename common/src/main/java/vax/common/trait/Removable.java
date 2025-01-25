package vax.common.trait;

import io.vertx.core.json.JsonObject;

/**
 * Something maybe marked removed
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Removable extends Fields {
    String FIELD_REMOVED_$BOOL = "removed";

    static JsonObject purify(JsonObject j) {
        j.remove(Removable.FIELD_REMOVED_$BOOL);
        return j;
    }

    boolean removed();
}
