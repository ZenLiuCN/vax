package vax.common.units;

import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.Nullable;
import vax.common.trait.Data;

import java.time.*;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongFunction;

/**
 * integer encode Date Time
 *
 * @author Zen.Liu
 * @since 2024-11-02
 */
public interface Times {
    Temporal toTime();


    AtomicReference<ZoneOffset> DEFAULT_ZONE = new AtomicReference<>(ZoneOffset.ofHours(8));

    static int date(@Nullable LocalDate d) {
        d = d == null ? LocalDate.now() : d;
        return x2d(d.getYear(), d.getMonth().getValue(), d.getDayOfMonth());
    }

    static int time(@Nullable LocalTime d) {
        d = d == null ? LocalTime.now() : d;
        return x2d(d.getHour(), d.getMinute(), d.getSecond());
    }

    static long datetime(@Nullable LocalDateTime d) {
        var p = d == null ? 1 : d.getYear() < 0 ? -1 : 1;
        return date(d == null ? null : d.toLocalDate()) * 1000000L + p * time(d == null ? null : d.toLocalTime());
    }

    static int date(@Nullable LocalDate d, @Nullable ZoneId zone) {
        d = d == null ? LocalDate.now(zone == null ? DEFAULT_ZONE.get() : zone) : d;
        return x2d(d.getYear(), d.getMonth().getValue(), d.getDayOfMonth());
    }

    static int time(@Nullable LocalTime d, @Nullable ZoneId zone) {
        d = d == null ? LocalTime.now(zone == null ? DEFAULT_ZONE.get() : zone) : d;
        return x2d(d.getHour(), d.getMinute(), d.getSecond());
    }

    static LocalDate date(int d) {
        var x = Math.abs(d);
        return LocalDate.of(d / 10000, x % 10000 / 100, x % 100);
    }

    static LocalTime time(int d) {
        var x = Math.abs(d);
        return LocalTime.of(d / 10000, x % 10000 / 100, x % 100);
    }

    static LocalDateTime datetime(long d) {
        return LocalDateTime.of(date((int) (d / 1000000)), time((int) (Math.abs(d) % 1000000)));
    }

    static int x2d(int... v) {
        var p = v[0] >= 0;
        var x = 0;
        for (int i : v) {
            i = Math.abs(i);
            x = x * 100 + i;
        }
        return x * (p ? 1 : -1);
    }

    record Time(int value) implements Times, Data.Compatible {
        public Time(JsonObject v) {
            this(Json.i32(v, "value"));
        }

        public Time(JsonObject v, Void unused) {
            this(Json.i32(v, "value"));
        }

        @Override
        public JsonObject $toJson() {
            return JsonObject.of("value", value);
        }

        @Override
        public JsonObject $jsJson() {
            return $toJson();
        }

        public Time(LocalTime d) {
            this(time(d));
        }

        public Time() {
            this(time(null));
        }

        public LocalTime toTime() {
            return time(value);
        }
    }

    record Date(int value) implements Times, Data.Compatible {
        public Date(JsonObject v) {
            this(Json.i32(v, "value"));
        }

        public Date(JsonObject v, Void unused) {
            this(Json.i32(v, "value"));
        }

        @Override
        public JsonObject $toJson() {
            return JsonObject.of("value", value);
        }

        @Override
        public JsonObject $jsJson() {
            return $toJson();
        }

        public Date(LocalDate d) {
            this(date(d));
        }

        public Date() {
            this(date(null));
        }

        public LocalDate toTime() {
            return date(value);
        }
    }

    record Datetime(long value) implements Times, Data.Compatible {
        public Datetime(JsonObject v) {
            this(Json.i64(v, "value"));
        }

        public Datetime(JsonObject v, Void unused) {
            this(Json.i64j(v, "value"));
        }

        @Override
        public JsonObject $toJson() {
            return JsonObject.of("value", value);
        }

        @Override
        public JsonObject $jsJson() {
            return JsonObject.of("value", String.valueOf(value));
        }

        public Datetime(LocalDateTime d) {
            this(datetime(d));
        }

        public Datetime() {
            this(datetime(null));
        }

        public LocalDateTime toTime() {
            return datetime(value);
        }

        public Instant toInstant() {
            return toTime().toInstant(DEFAULT_ZONE.get());
        }


    }


    /**
     * Ticker is a primitive long store seconds precision datetime with weekday.
     */
    interface Tickers {
        byte weekday();

        short year();

        byte month();

        byte day();

        byte hour();

        byte minute();

        byte second();

        static long now() {

            return ToTicker.apply(LocalDateTime.now(DEFAULT_ZONE.get()));
        }

        /// TICK = YEAR[2B]|MONTH|DAY|HOUR|MINUTE|SECONDS|WEEKDAY (byte for each value)
        getTickFunc WEEKDAY = t -> (byte) (t & 0xFF);
        getTickFunc SECOND = t -> (byte) ((t >> 8) & 0xFF);
        getTickFunc MINUTE = t -> (byte) ((t >> 16) & 0xFF);
        getTickFunc HOUR = t -> (byte) ((t >> 24) & 0xFF);
        getTickFunc DAY = t -> (byte) ((t >> 32) & 0xFF);
        getTickFunc MONTH = t -> (byte) ((t >> 40) & 0xFF);
        getTickYearFunc YEAR = t -> (short) ((t >> 48));

        setTickFunc SetWEEKDAY = (i, t) -> i | t;
        setTickFunc SetSECOND = (i, t) -> i | ((long) (t) << 8);
        setTickFunc SetMINUTE = (i, t) -> i | ((long) (t) << 16);
        setTickFunc SetHOUR = (i, t) -> i | ((long) (t) << 24);
        setTickFunc SetDAY = (i, t) -> i | ((long) (t) << 32);
        setTickFunc SetMONTH = (i, t) -> i | ((long) (t) << 40);
        setTickFunc SetYEAR = (i, t) -> i | ((long) (t) << 48);
        LongFunction<String> FORMATTER = i -> "%02d-%02d-%02dT%02d:%02d:%02d,%d".formatted(
                YEAR.apply(i),
                MONTH.apply(i),
                DAY.apply(i),
                HOUR.apply(i),
                MINUTE.apply(i),
                SECOND.apply(i),
                WEEKDAY.apply(i)
        );
        FromTickFunc<LocalDateTime> FromTicker = i ->
                LocalDateTime.of(
                        YEAR.apply(i),
                        MONTH.apply(i),
                        DAY.apply(i),
                        HOUR.apply(i),
                        MINUTE.apply(i),
                        SECOND.apply(i)
                );
        ToTickFunc<LocalDateTime> ToTicker = d ->
                SetWEEKDAY.apply(
                        SetSECOND.apply(
                                SetMINUTE.apply(
                                        SetHOUR.apply(
                                                SetDAY.apply(
                                                        SetMONTH.apply(
                                                                SetYEAR.apply(0, d.getYear())
                                                                , d.getMonthValue())
                                                        , d.getDayOfMonth())
                                                , d.getHour())
                                        , d.getMinute())
                                , d.getSecond())
                        , d.getDayOfWeek().getValue());

        interface setTickFunc {
            long apply(long v, int s);

        }

        interface FromTickFunc<T> {
            T apply(long tick);
        }

        interface ToTickFunc<T> {
            long apply(T t);
        }

        interface getTickFunc {
            byte apply(long v);
        }

        interface getTickYearFunc {
            short apply(long v);
        }

    }

    record Ticker(long value) implements Tickers, Times {
        public Ticker(LocalDateTime d) {
            this(Tickers.ToTicker.apply(d));
        }

        public Ticker() {
            this(Tickers.now());
        }

        public LocalDateTime toTime() {
            return Tickers.FromTicker.apply(value);
        }

        @Override
        public byte weekday() {
            return Tickers.WEEKDAY.apply(value);
        }

        @Override
        public short year() {
            return Tickers.YEAR.apply(value);
        }

        @Override
        public byte month() {
            return Tickers.MONTH.apply(value);
        }

        @Override
        public byte day() {
            return Tickers.DAY.apply(value);
        }

        @Override
        public byte hour() {
            return Tickers.HOUR.apply(value);
        }

        @Override
        public byte minute() {
            return Tickers.MINUTE.apply(value);
        }

        @Override
        public byte second() {
            return Tickers.SECOND.apply(value);
        }
    }

    /**
     * Tick is a C# compatible tick for nanos precision timestamp
     */
    interface Ticks {

        long KindUtc = 0x4000000000000000L;//1
        long KindLocal = 0x8000000000000000L;//2
        long TicksMask = 0x3FFFFFFFFFFFFFFFL;
        int KindShift = 62;
        int tickPerSec = 10_000_000;
        long base = -62135596800L;

        /**
         * convert ticks to Instant
         *
         * @param ticks long from C# DateTime.toBinary()
         * @return (timestamp, isLocalTime)
         */
        static Map.Entry<Instant, Boolean> from(long ticks) {
            final long flag = ticks >>> KindShift;
            final long tick = ticks & TicksMask;
            final long sec = tick / tickPerSec;
            final long nano = (tick % tickPerSec) * 100;
            return Map.entry(Instant.ofEpochSecond(sec + base, nano), flag == 2L);
        }

        /**
         * create ticks from instant
         *
         * @param instant source instant
         * @param isLocal dose use local flag
         * @return ticks
         */
        static long from(Instant instant, boolean isLocal) {
            long nano = instant.getNano() / 100;
            long sec = (instant.getEpochSecond() - base) * tickPerSec;
            long tick = sec + nano;
            return tick | (isLocal ? KindLocal : KindUtc);
        }

        static long from(Instant instant) {
            long nano = instant.getNano() / 100;
            long sec = (instant.getEpochSecond() - base) * tickPerSec;
            long tick = sec + nano;
            return tick | KindUtc;
        }

        static long now() {
            return from(Instant.now(), false);
        }

        static Duration between(long first, long second) {
            final Instant f = from(first).getKey();
            final Instant s = from(second).getKey();
            return Duration.between(f, s);
        }

        static Duration betweenNow(long first) {
            final Instant f = from(first).getKey();
            return Duration.between(f, Instant.now());
        }
    }

    record Tick(long value) implements Ticks, Times {
        public Tick(Instant d) {
            this(Ticks.from(d));
        }

        public Tick() {
            this(Ticks.now());
        }

        public Duration between(Tick t) {
            return Ticks.between(value, t.value);
        }

        public Instant toTime() {
            return Ticks.from(value).getKey();
        }
    }
}
