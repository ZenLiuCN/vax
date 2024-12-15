package vax.common.units;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.ClusterSerializable;

/**
 * @author Zen.Liu
 * @since 2024-12-08
 */
public interface DataTypeX {
    interface JsonType {
        JsonObject $raw();

        JsonObject $copy();

        /**
         * export javascript compact json
         */
        JsonObject $export();
    }



    interface PojoType<T> {
        /**
         * deep copy
         */
        T $copyPojo();
    }

    interface BinaryType extends ClusterSerializable {
        Buffer $rawBuffer();

        default Buffer $copyBuffer() {
            return $rawBuffer().copy();
        }

        @Override
        void writeToBuffer(Buffer buffer);

        @Override
        int readFromBuffer(int pos, Buffer buffer);
    }
}
