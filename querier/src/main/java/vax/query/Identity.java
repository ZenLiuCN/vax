package vax.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.nio.Buffer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

///  identified values (property of tables)
sealed public interface Identity<T> extends Value<T> {
    @Nullable CharSequence owner();

    CharSequence name();

    @Nullable CharSequence alias();

    @Nullable CharSequence schema();

    ModifyExpr<T> set(Expr<T> v);

    ModifyExpr<T> set(T v);

    AssignExpr<T> assign(Expr<T> v);

    AssignExpr<T> assign(T v);

    non-sealed interface bool extends vax.query.Identity<Boolean>, Value.bool {}

    non-sealed interface text extends vax.query.Identity<CharSequence>, Value.text {}

    sealed interface temporal<T extends Temporal> extends vax.query.Identity<T>, Value.temporal<T> {}

    non-sealed interface dateTime extends temporal<LocalDateTime> {}

    non-sealed interface time extends temporal<LocalTime> {}

    non-sealed interface date extends temporal<LocalDate> {}

    non-sealed interface timestamp extends temporal<Instant> {}

    non-sealed interface binary extends vax.query.Identity<byte[]>, Value.binary {}

    non-sealed interface blob extends vax.query.Identity<Buffer>, Value.blob {}

    sealed interface numeric<T extends Number> extends vax.query.Identity<T>, Value.numeric<T> {}

    non-sealed interface int8 extends numeric<Byte>, Value.numeric.int8 {}

    non-sealed interface int16 extends numeric<Short>, Value.numeric.int16 {}

    non-sealed interface int32 extends numeric<Integer>, Value.numeric.int32 {}

    non-sealed interface int64 extends numeric<Long>, Value.numeric.int64 {}

    non-sealed interface float32 extends numeric<Float>, Value.numeric.float32 {}

    non-sealed interface float64 extends numeric<Double>, Value.numeric.float64 {}

    non-sealed interface decimal extends numeric<BigDecimal>, Value.numeric.decimal {}

    sealed interface json<T> extends vax.query.Identity<T>, Value.json<T> {}

    non-sealed interface jsonObject extends json<JsonObject>, Value.json.jsonObject {}

    non-sealed interface jsonArray extends json<JsonArray>, Value.json.jsonArray {}
}
