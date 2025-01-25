package vax.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.nio.Buffer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

///  raw sql values
public interface Raw<T> extends Expr<T> {
    Value.bool TRUE = new Raw.bool("TRUE");

    Value.bool FALSE = new Raw.bool("FALSE");
    Value.temporal.timestamp NOW = new Raw.timestamp("CURRENT_TIMESTAMP");
    Value.temporal.time TIME = new Raw.time("CURRENT_TIME");
    Value.temporal.date DATE = new Raw.date("CURRENT_DATE");
    Value.temporal.datetime DATETIME = new Raw.datetime("NOW");

    String raw();

    sealed interface numeric<T extends Number> extends vax.query.Raw<T>, Value.numeric<T> {}

    record int8(String raw) implements numeric<Byte>, Value.numeric.int8 {}

    record int16(String raw) implements numeric<Short>, Value.numeric.int16 {}

    record int32(String raw) implements numeric<Integer>, Value.numeric.int32 {}

    record int64(String raw) implements numeric<Long>, Value.numeric.int64 {}

    record float32(String raw) implements numeric<Float>, Value.numeric.float32 {}

    record float64(String raw) implements numeric<Double>, Value.numeric.float64 {}

    record decimal(String raw) implements numeric<BigDecimal>, Value.numeric.decimal {}

    record binary(String raw) implements vax.query.Raw<byte[]>, Value.binary {}

    record blob(String raw) implements vax.query.Raw<Buffer>, Value.blob {}

    sealed interface temporal<T extends Temporal> extends vax.query.Raw<T>, Value.temporal<T> {}

    record timestamp(String raw) implements temporal<Instant>, Value.temporal.timestamp {}

    record datetime(String raw) implements temporal<LocalDateTime>, Value.temporal.datetime {}

    record time(String raw) implements temporal<LocalTime>, Value.temporal.time {}

    record date(String raw) implements temporal<LocalDate>, Value.temporal.date {}

    record bool(String raw) implements vax.query.Raw<Boolean>, Value.bool {}

    sealed interface json<T> extends vax.query.Raw<T>, Value.json<T> {}

    record jsonObject(String raw) implements json<JsonObject>, Value.json.jsonObject {}

    record jsonArray(String raw) implements json<JsonArray>, Value.json.jsonArray {}
}
