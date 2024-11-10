package cn.zenliu.vax.common.units;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jooq.lambda.function.Consumer3;

import java.util.function.BiFunction;

/**
 * @author Zen.Liu
 * @since 2024-11-10
 */
public interface FunctorX {
    interface JsonArrayReader<T> extends BiFunction<JsonArray, Integer, T>, FunctorX {
        T apply(JsonArray o, int i);

        @Override
        default T apply(JsonArray objects, Integer integer) {
            return apply(objects, (int) integer);
        }
    }

    interface JsonObjectReader<T> extends BiFunction<JsonObject, String, T>, FunctorX {
        T apply(JsonObject o, String k);

    }

    interface JsonArrayWriter<T> extends Consumer3<JsonArray, Integer, T>, FunctorX {
        void accept(JsonArray o, int i, T v);

        @Override
        default void accept(JsonArray v1, Integer v2, T v3) {
            accept(v1, (int) v2, v3);
        }
    }

    interface JsonObjectWriter<T> extends Consumer3<JsonObject, String, T>, FunctorX {
        void accept(JsonObject o, String k, T v);
    }

    interface JsonObjectAccessor<T> extends JsonObjectWriter<T>, JsonObjectReader<T> {

    }


}
