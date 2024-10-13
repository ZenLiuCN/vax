package cn.zenliu.vax.query;

import io.netty.util.collection.IntObjectMap;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Zen.Liu
 * @since 2024-10-07
 */
public interface u {
    record Hold(Class<?> type, Function<Object, Object> conv) {
    }

    record Argument(Object value, Function<Object, Object> conv) {
    }

    interface Renderer {
        IntObjectMap<Hold> holders();

        Map<String, Argument> arguments();

        default void render(E<?> e) {
            if (e == null) throw new IllegalStateException("null expr can't rendered");
            if (e instanceof E.Param<?> p) renderParam(p);
            else if (e instanceof E.Hold<?> p) renderHold(p);
            else if (e instanceof E.Json<?> p) renderJson(p);
            else if (e instanceof E.Const<?> p) renderConst(p);
            else if (e instanceof E.Bit<?> p) renderBits(p);
            else if (e instanceof E.Comparison p) renderComparison(p);
            else if (e instanceof E.Logic p) renderLogic(p);
            else if (e instanceof E.Math<?> p) renderMath(p);
            else renderOther(e);
        }

        void renderJson(E.Json<?> p);

        void renderConst(E.Const<?> p);

        void renderHold(E.Hold<?> p);

        void renderParam(E.Param<?> p);

        void renderBits(E.Bit<?> p);

        void renderLogic(E.Logic p);

        void renderComparison(E.Comparison p);

        void renderMath(E.Math<?> p);

        void renderOther(E<?> e);

        Tuple2<String, Map<String, Object>> build();

        Tuple3<String, Map<String, Object>, IntObjectMap<Hold>> computes();
    }

    interface R {
        void $render(Renderer renderer);
    }

    interface T<T> {
        Class<T> $type();
    }

    interface Mapper<T> {
        T apply(Row row);
    }

    interface IndexedMapper<T> extends Mapper<T> {
        T apply(Row row);

        IndexedMapper<T> with(String name);

        IndexedMapper<T> with(int pos);

        JsonObject apply(JsonObject jo, Row row);
    }
}
