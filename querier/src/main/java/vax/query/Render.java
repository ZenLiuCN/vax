package vax.query;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.data.Numeric;

import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;
import java.util.function.Function;

/**
 * @author Zen.Liu
 * @since 2025-03-07
 */
public interface Render<T> {
    T get(Row r, int n);

    default <R> Render<R> map(Function<T, R> m) {
        return (r, i) -> m.apply(get(r, i));
    }

    Render<String> STRING = Row::getString;
    Render<Boolean> BOOLEAN = Row::getBoolean;
    Render<Short> SHORT = Row::getShort;
    Render<Integer> INTEGER = Row::getInteger;
    Render<Long> LONG = Row::getLong;
    Render<Numeric> NUMERIC = Row::getNumeric;
    Render<Buffer> BUFFER = Row::getBuffer;
    Render<UUID> UUID = Row::getUUID;
    Render<JsonObject> JSON_OBJECT = Row::getJsonObject;
    Render<JsonArray> JSON_ARRAY = Row::getJsonArray;

    Render<BigDecimal> DECIMAL = Row::getBigDecimal;
    Render<OffsetDateTime> OFFSET_DATE_TIME = Row::getOffsetDateTime;
    Render<OffsetTime> OFFSET_TIME = Row::getOffsetTime;
    Render<LocalDate> LOCAL_DATE = Row::getLocalDate;
    Render<LocalTime> LOCAL_TIME = Row::getLocalTime;
    Render<LocalDateTime> LOCAL_DATE_TIME = Row::getLocalDateTime;

    Render<byte[]> BYTES = BUFFER.map(Buffer::getBytes);

    Render<Instant> INSTANT = OFFSET_DATE_TIME.map(OffsetDateTime::toInstant);

}
