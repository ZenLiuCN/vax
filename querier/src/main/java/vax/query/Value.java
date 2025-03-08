package vax.query;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.intellij.lang.annotations.MagicConstant;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zen.Liu
 * @since 2025-03-07
 */
public interface Value<T> {
    record param<T>(T value) implements Value<T> {}
    record hold<T>(Class<T> value,String key) implements Value<T> {}

    default Bool eq(T v) {
        return eq(new param<>(v));
    }

    default Bool neq(T v) {
        return neq(new param<>(v));
    }

    default Bool eq(Value<T> v) {
        return new binary<>(this, v, EQ);
    }

    default Bool neq(Value<T> v) {
        return new binary<>(this, v, NEQ);
    }

    default Bool exists() {
        return new unary<>(this, NON_NULL);
    }

    default Bool missing() {
        return new unary<>(this, NULL);
    }

    default Model.Field<T> as(String name) {
        return new Model.Field.Virtual<>(this, name);
    }


    interface Operation {
        int op();
    }


    int NONE = 0;

    int EQ = 1;
    int NEQ = 2;
    int BTW = 3;
    int NULL = 4;
    int NON_NULL = 5;
    int GT = 6;
    int GTE = 7;
    int LT = 8;
    int LTE = 9;
    int NOT = 10;
    int AND = 11;
    int OR = 12;
    int PLUS = 13;
    int MINUS = 14;
    int TIMES = 15;
    int DIV = 16;
    int MOD = 17;
    int BIT_SHR = 18;
    int BIT_SHL = 19;
    int BIT_NOT = 20;
    int BIT_OR = 21;
    int BIT_AND = 22;
    int BIT_XOR = 23;
    int START = 24;
    int END = 25;
    int INCLUDE = 26;
    int CONCAT = 27;
    int JSON_MERGE = 28;
    int JSON_GET = 29;
    int JSON_SET = 30;
    int JSON_DELETE = 31;
    int TIME_ADD = 32;
    int TIME_FIELD = 33;


    record binary<T>(Value<T> v0, Value<T> v1, @MagicConstant(valuesFromClass = Expr.class) int op) implements Bool,
                                                                                                               Operation {
    }

    record unary<T>(Value<T> v0, @MagicConstant(valuesFromClass = Expr.class) int op) implements Bool, Operation {}

    record triple<T>(Value<T> v0, Value<T> v1, Value<T> v2,
                     @MagicConstant(valuesFromClass = Expr.class) int op) implements
                                                                          Bool,
                                                                          Operation {}


    Bool TRUE = new Bool() {};
    Bool FALSE = new Bool() {};

    interface Bool extends Value<Boolean> {
        default Bool not() {
            return new unary<>(this, NOT);
        }

        default Bool and(Value<Boolean> v) {
            return new binary<>(this, v, AND);
        }

        default Bool or(Value<Boolean> v) {
            return new binary<>(this, v, OR);
        }
    }

    record concat(List<Value<CharSequence>> v) implements Text, Operation {
        @Override
        public int op() {
            return CONCAT;
        }
    }

    interface Text extends Value<CharSequence> {
        @SuppressWarnings("unchecked")
        default Text concat(Value<CharSequence> v0, Value<CharSequence>... v) {
            var x = Arrays.asList(v);
            x.add(0, v0);
            x.add(0, this);
            return new concat(x);
        }

        default Bool startWith(Value<CharSequence> v) {
            return new binary<>(this, v, START);
        }

        default Bool endWith(Value<CharSequence> v) {
            return new binary<>(this, v, END);
        }

        default Bool contains(Value<CharSequence> v) {
            return new binary<>(this, v, INCLUDE);
        }
    }

    record bNum<T extends Number>(Value<T> left, Value<T> right, int op) implements Numeric<T>, Operation {}

    record bInt<T extends Number>(Value<T> left, Value<T> right, int op) implements Int<T>, Operation {}

    record uNum<T extends Number>(Value<T> left, int op) implements Numeric<T>, Operation {}

    record uInt<T extends Number>(Value<T> left, int op) implements Int<T>, Operation {}

    record tNum<T extends Number>(Value<T> left, Value<T> right, Value<T> right2, int op)
            implements Numeric<T>, Operation {}

    interface Numeric<T extends Number> extends Value<T> {

        default Bool gt(Value<T> v) {return new binary<>(this, v, GT);}

        default Bool lt(Value<T> v) {return new binary<>(this, v, LT);}

        default Bool gte(Value<T> v) {return new binary<>(this, v, GTE);}

        default Bool lte(Value<T> v) {return new binary<>(this, v, LTE);}

        default Bool between(Value<T> v, Value<T> v1) {return new triple<>(this, v, v1, BTW);}

        default Numeric<T> plus(Value<T> v) {return new bNum<>(this, v, PLUS);}

        default Numeric<T> minus(Value<T> v) {return new bNum<>(this, v, PLUS);}

        default Numeric<T> div(Value<T> v) {return new bNum<>(this, v, DIV);}

        default Numeric<T> times(Value<T> v) {return new bNum<>(this, v, TIMES);}

        default Numeric<T> mode(Value<T> v) {return new bNum<>(this, v, MOD);}


    }

    interface Int<T extends Number> extends Numeric<T> {
        default Int<T> shr(Int<T> v) {return new bInt<>(this, v, BIT_SHR);}

        default Int<T> shl(Int<T> v) {return new bInt<>(this, v, BIT_SHL);}

        default Int<T> bOr(Int<T> v) {return new bInt<>(this, v, BIT_OR);}

        default Int<T> bAnd(Int<T> v) {return new bInt<>(this, v, BIT_AND);}

        default Int<T> bXor(Int<T> v) {return new bInt<>(this, v, BIT_XOR);}

        default Int<T> bNot() {return new uInt<>(this, BIT_NOT);}
    }

    record uTime<T extends java.time.temporal.Temporal>(Temporal<T> v, int amount, ChronoUnit unit)
            implements
            Temporal<T>,
            Operation {

        @Override
        public int op() {
            return TIME_ADD;
        }
    }

    record fTime<T extends java.time.temporal.Temporal>(Temporal<T> v, ChronoField field) implements Int<Integer>,
                                                                                                     Operation {

        @Override
        public int op() {
            return TIME_FIELD;
        }
    }

    Temporal<Instant> NOW = new Temporal<>() {};

    interface Temporal<T extends java.time.temporal.Temporal> extends Value<T> {
        default Temporal<T> add(int v, ChronoUnit unit) {
            return new uTime<>(this, v, unit);
        }

        default Bool after(Temporal<T> v) {
            return new binary<>(this, v, GT);
        }

        default Bool before(Temporal<T> v) {
            return new binary<>(this, v, LT);
        }

        default Bool between(Temporal<T> v, Temporal<T> v1) {
            return new triple<>(this, v, v1, BTW);
        }

        default Int<Integer> field(ChronoField field) {
            return new fTime<>(this, field);
        }
    }

    interface Binary<T> extends Value<T> {}


    sealed interface Json<T> extends Value<T> {

    }

    record jText<T>(Value<T> v, int n, String key) implements Text, Operation {
        @Override
        public int op() {
            return JSON_GET;
        }
    }

    record jBool<T>(Value<T> v, int n, String key) implements Bool, Operation {
        @Override
        public int op() {
            return JSON_GET;
        }
    }

    record jNumeric<T, R extends Number>(Value<T> v, int n, String key) implements Numeric<R>, Operation {
        @Override
        public int op() {
            return JSON_GET;
        }
    }

    record jArray<T>(Value<T> v, int n, String key) implements JArray, Operation {
        @Override
        public int op() {
            return JSON_GET;
        }
    }

    record jObject<T>(Value<T> v, int n, String key) implements JObject, Operation {
        @Override
        public int op() {
            return JSON_GET;
        }
    }

    record mArray(Value<JsonArray> v0, Value<JsonArray> v1) implements JArray, Operation {
        @Override
        public int op() {
            return JSON_MERGE;
        }
    }

    record bArray<T>(Value<JsonArray> v0, List<Object> path, Value<T> v1, int op) implements JArray, Operation {

    }

    record mObject(Value<JsonObject> v0, Value<JsonObject> v1) implements JObject, Operation {
        @Override
        public int op() {
            return JSON_MERGE;
        }
    }

    record bObject<T>(Value<JsonObject> v0, List<Object> path, Value<T> v1, int op) implements JObject, Operation {}

    non-sealed interface JArray extends Json<JsonArray> {
        default Text asText(int n) {
            return new jText<>(this, n, null);
        }

        default Bool asBool(int n) {
            return new jBool<>(this, n, null);
        }

        default <T extends Number> Numeric<T> asNumber(int n) {
            return new jNumeric<>(this, n, null);
        }

        default JObject asObject(int n) {
            return new jObject<>(this, n, null);
        }

        default JArray asArray(int n) {
            return new jArray<>(this, n, null);
        }

        default JArray merge(Value<JsonArray> v) {
            return new mArray(this, v);
        }

        default JArray set(Value<?> v, int k, Object... path) {
            if (path.length == 0)
                return new bArray<>(this, List.of(k), v, JSON_SET);
            var x = Arrays.stream(path).map(i -> {
                if (i instanceof Integer || i instanceof CharSequence) return i;
                throw new IllegalArgumentException("path can only contains text or integer");
            }).collect(Collectors.toList());
            x.add(0, k);
            return new bArray<>(this, x, v, JSON_SET);
        }
    }

    non-sealed interface JObject extends Json<JsonObject> {
        default Text asText(String k) {
            return new jText<>(this, -1, k);
        }

        default Bool asBool(String k) {
            return new jBool<>(this, -1, k);
        }

        default <T extends Number> Numeric<T> asNumber(String k) {
            return new jNumeric<>(this, -1, k);
        }

        default JObject asObject(String k) {
            return new jObject<>(this, -1, k);
        }

        default JArray asArray(String k) {
            return new jArray<>(this, -1, k);
        }

        default JObject merge(Value<JsonObject> v) {
            return new mObject(this, v);
        }

        default JObject set(Value<?> v, String k, Object... path) {
            if (path.length == 0)
                return new bObject<>(this, List.of(k), v, JSON_SET);
            var x = Arrays.stream(path).map(i -> {
                if (i instanceof Integer || i instanceof CharSequence) return i;
                throw new IllegalArgumentException("path can only contains text or integer");
            }).collect(Collectors.toList());
            x.add(0, k);
            return new bObject<>(this, x, v, JSON_SET);
        }


    }


}
