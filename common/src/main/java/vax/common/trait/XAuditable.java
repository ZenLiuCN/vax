package vax.common.trait;

import io.vertx.core.json.JsonObject;

import java.time.Instant;

/**
 * Something with a version
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface XAuditable {
    String FIELD_CREATED_BY_$I64 = "createdBy";
    String FIELD_CREATED_AT_$_1Instant = "createdAt";
    String FIELD_MODIFIED_BY_$I64  = "modifiedBy";
    String FIELD_MODIFIED_AT_$_1Instant = "modifiedAt";
   static JsonObject purify(JsonObject j){
       j.remove(XAuditable.FIELD_CREATED_BY_$I64);
       j.remove(XAuditable.FIELD_CREATED_AT_$_1Instant);
       j.remove(XAuditable.FIELD_MODIFIED_BY_$I64);
       j.remove(XAuditable.FIELD_MODIFIED_AT_$_1Instant);
       return j;
    }
    long createdBy();

    /**
     * timestamp value
     */
    Instant createdAt();

    long modifiedBy();

    /**
     * timestamp value
     */
    Instant modifiedAt();
}
