package vax.common.units;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

/**
 * integer encode Date Time
 *
 * @author Zen.Liu
 * @since 2024-11-02
 */
public interface TimeX {
    Temporal toTime();

    record Time(int value) implements TimeX {
        public Time() {
            this(time(null));
        }

        public LocalTime toTime() {
            return time(value);
        }
    }

    record Date(int value) implements TimeX {
        public Date() {
            this(date(null));
        }
        public LocalDate toTime() {
            return date(value);
        }
    }

    record Datetime(long value) implements TimeX {
        public Datetime() {
            this(datetime(null));
        }
        public LocalDateTime toTime() {
            return datetime(value);
        }
    }

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

}
