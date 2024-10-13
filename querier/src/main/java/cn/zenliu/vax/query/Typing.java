package cn.zenliu.vax.query;
/*
import cn.zenliu.vertx.query.u.IndexedMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.data.Numeric;

import java.math.BigDecimal;
import java.time.*;
import java.util.Objects;
import java.util.function.Function;


public interface Typing {
    Class<Byte> I8 = Byte.class;
    Class<Short> I16 = Short.class;
    Class<Integer> I32 = Integer.class;
    Class<Long> I64 = Long.class;
    Class<Double> F64 = Double.class;
    Class<Float> F32 = Float.class;
    Class<Numeric> NUM = Numeric.class;
    Class<BigDecimal> DEC = BigDecimal.class;
    Class<String> TEXT = String.class;
    Class<Boolean> BOOL = Boolean.class;

    Class<Instant> TIMESTAMP = Instant.class;
    Class<LocalDateTime> DATE_TIME = LocalDateTime.class;
    Class<LocalTime> TIME = LocalTime.class;
    Class<LocalDate> DATE = LocalDate.class;

    Class<JsonObject> JSON_OBJECT = JsonObject.class;
    Class<JsonArray> JSON_ARRAY = JsonArray.class;
    Class<byte[]> BINARY = byte[].class;
    Class<Buffer> BLOB = Buffer.class;

    static <T, R> Function<T, R> nullable(Function<T, R> m) {
        return v -> v == null ? null : m.apply(v);
    }

    interface Mapping {
        IndexedMapper<Byte> I8 = new index<>(((IndexMapper<Numeric>) Row::getNumeric).andThen(nullable(Numeric::byteValue)), -1, "");
        IndexedMapper<Short> I16 = new index<>(Row::getShort, -1, "");
        IndexedMapper<Integer> I32 = new index<>(Row::getInteger, -1, "");
        IndexedMapper<Long> I64 = new index<>(Row::getLong, -1, "");
        IndexedMapper<Float> F32 = new index<>(Row::getFloat, -1, "");
        IndexedMapper<Double> F64 = new index<>(Row::getDouble, -1, "");
        IndexedMapper<Numeric> NUM = new index<>(Row::getNumeric, -1, "");
        IndexedMapper<BigDecimal> DEC = new index<>(Row::getBigDecimal, -1, "");
        IndexedMapper<String> TEXT = new index<>(Row::getString, -1, "");
        IndexedMapper<Boolean> BOOL = new index<>(Row::getBoolean, -1, "");
        IndexedMapper<LocalTime> TIME = new index<>(Row::getLocalTime, -1, "");
        IndexedMapper<LocalDate> DATE = new index<>(Row::getLocalDate, -1, "");
        IndexedMapper<Instant> TIMESTAMP = new index<>(((IndexMapper<OffsetDateTime>) Row::getOffsetDateTime).andThen(nullable(Instant::from)), -1, "");
        IndexedMapper<LocalDateTime> DATE_TIME = new index<>(Row::getLocalDateTime, -1, "");
        IndexedMapper<JsonObject> JSON_OBJECT = new index<>(Row::getJsonObject, -1, "");
        IndexedMapper<JsonArray> JSON_ARRAY = new index<>(Row::getJsonArray, -1, "");
        IndexedMapper<Buffer> BLOB = new index<>(Row::getBuffer, -1, "");
        IndexedMapper<byte[]> BINARY = new index<>(((IndexMapper<Buffer>) Row::getBuffer).andThen(nullable(Buffer::getBytes)), -1, "");

        interface IndexMapper<T> {
            T apply(Row row, int pos);

            default <R> IndexMapper<R> andThen(Function<T, R> m) {
                Objects.requireNonNull(m);
                return (r, i) -> m.apply(apply(r, i));
            }
        }

        record index<T>(IndexMapper<T> act, int pos, String name) implements IndexedMapper<T> {

            @Override
            public IndexedMapper<T> with(int pos) {
                return new index<>(act, pos, name);
            }

            @Override
            public IndexedMapper<T> with(String name) {
                return new index<>(act, pos, name);
            }

            @Override
            public T apply(Row row) {
                return act.apply(row, pos);
            }

            @Override
            public JsonObject apply(JsonObject jo, Row row) {
                return jo.put(name, act.apply(row, pos));
            }
        }
    }

    Function<String, Context.field<Byte, i8>> I8_FUNC = i8::new;
    Function<String, Context.field<Short, i16>> I16_FUNC = i16::new;
    Function<String, Context.field<Integer, i32>> I32_FUNC = i32::new;
    Function<String, Context.field<Long, i64>> I64_FUNC = i64::new;
    Function<String, Context.field<Double, f64>> F64_FUNC = f64::new;
    Function<String, Context.field<Float, f32>> F32_FUNC = f32::new;
    Function<String, Context.field<Numeric, num>> NUM_FUNC = num::new;
    Function<String, Context.field<BigDecimal, dec>> DEC_FUNC = dec::new;
    Function<String, Context.field<String, text>> TEXT_FUNC = text::new;
    Function<String, Context.field<Boolean, bool>> BOOL_FUNC = bool::new;
    Function<String, Context.field<Instant, timestamp>> TIMESTAMP_FUNC = timestamp::new;
    Function<String, Context.field<LocalDateTime, dateTime>> DATE_TIME_FUNC = dateTime::new;
    Function<String, Context.field<LocalTime, time>> TIME_FUNC = time::new;
    Function<String, Context.field<LocalDate, date>> DATE_FUNC = date::new;
    Function<String, Context.field<JsonObject, jsonObject>> JSON_OBJECT_FUNC = jsonObject::new;
    Function<String, Context.field<JsonArray, jsonArray>> JSON_ARRAY_FUNC = jsonArray::new;
    Function<String, Context.field<byte[], binary>> BINARY_FUNC = binary::new;
    Function<String, Context.field<Buffer, blob>> BLOB_FUNC = blob::new;


    final class i8 extends Context.field<java.lang.Byte, i8> implements Context.Field<java.lang.Byte>, Context.Value.I8 {
        public i8(String name) {
            super(I8, name, null, null, Mapping.I8);
        }

        @Override
        i8 self() {
            return new i8(name);
        }
    }

    final class i16 extends Context.field<java.lang.Short, i16> implements Context.Field<java.lang.Short>, Context.Value.I16 {
        public i16(String name) {
            super(I16, name, null, null, Mapping.I16);
        }

        @Override
        i16 self() {
            return new i16(name);
        }
    }

    final class i32 extends Context.field<java.lang.Integer, i32> implements Context.Field<java.lang.Integer>, Context.Value.I32 {
        public i32(String name) {
            super(I32, name, null, null, Mapping.I32);
        }

        @Override
        i32 self() {
            return new i32(name);
        }
    }

    final class i64 extends Context.field<java.lang.Long, i64> implements Context.Field<java.lang.Long>, Context.Value.I64 {
        public i64(String name) {
            super(I64, name, null, null, Mapping.I64);
        }

        @Override
        i64 self() {
            return new i64(name);
        }
    }

    final class f64 extends Context.field<java.lang.Double, f64> implements Context.Field<java.lang.Double>, Context.Value.F64 {
        public f64(String name) {
            super(F64, name, null, null, Mapping.F64);
        }

        @Override
        f64 self() {
            return new f64(name);
        }
    }

    final class f32 extends Context.field<java.lang.Float, f32> implements Context.Field<java.lang.Float>, Context.Value.F32 {
        public f32(String name) {
            super(F32, name, null, null, Mapping.F32);
        }

        @Override
        f32 self() {
            return new f32(name);
        }
    }

    final class num extends Context.field<io.vertx.sqlclient.data.Numeric, num> implements Context.Field<io.vertx.sqlclient.data.Numeric>, Context.Value.Num {
        public num(String name) {
            super(NUM, name, null, null, Mapping.NUM);
        }

        @Override
        num self() {
            return new num(name);
        }
    }

    final class dec extends Context.field<java.math.BigDecimal, dec> implements Context.Field<java.math.BigDecimal>, Context.Value.Dec {
        public dec(String name) {
            super(DEC, name, null, null, Mapping.DEC);
        }

        @Override
        dec self() {
            return new dec(name);
        }
    }

    final class text extends Context.field<java.lang.String, text> implements Context.Field<java.lang.String>, Context.Value.Text {
        public text(String name) {
            super(TEXT, name, null, null, Mapping.TEXT);
        }

        @Override
        text self() {
            return new text(name);
        }
    }

    final class bool extends Context.field<java.lang.Boolean, bool> implements Context.Field<java.lang.Boolean>, Context.Value.Bool {
        public bool(String name) {
            super(BOOL, name, null, null, Mapping.BOOL);
        }

        @Override
        bool self() {
            return new bool(name);
        }
    }

    final class timestamp extends Context.field<java.time.Instant, timestamp> implements Context.Field<java.time.Instant>, Context.Value.Timestamp {
        public timestamp(String name) {
            super(TIMESTAMP, name, null, null, Mapping.TIMESTAMP);
        }

        @Override
        timestamp self() {
            return new timestamp(name);
        }
    }

    final class dateTime extends Context.field<java.time.LocalDateTime, dateTime> implements Context.Field<java.time.LocalDateTime>, Context.Value.DateTime {
        public dateTime(String name) {
            super(DATE_TIME, name, null, null, Mapping.DATE_TIME);
        }

        @Override
        dateTime self() {
            return new dateTime(name);
        }
    }

    final class time extends Context.field<java.time.LocalTime, time> implements Context.Field<java.time.LocalTime>, Context.Value.Time {
        public time(String name) {
            super(TIME, name, null, null, Mapping.TIME);
        }

        @Override
        time self() {
            return new time(name);
        }
    }

    final class date extends Context.field<java.time.LocalDate, date> implements Context.Field<java.time.LocalDate>, Context.Value.Date {
        public date(String name) {
            super(DATE, name, null, null, Mapping.DATE);
        }

        @Override
        date self() {
            return new date(name);
        }
    }

    final class jsonObject extends Context.field<io.vertx.core.json.JsonObject, jsonObject> implements Context.Field<io.vertx.core.json.JsonObject>, Context.Value.JObject {
        public jsonObject(String name) {
            super(JSON_OBJECT, name, null, null, Mapping.JSON_OBJECT);
        }

        @Override
        jsonObject self() {
            return new jsonObject(name);
        }
    }

    final class jsonArray extends Context.field<io.vertx.core.json.JsonArray, jsonArray> implements Context.Field<io.vertx.core.json.JsonArray>, Context.Value.JArray {
        public jsonArray(String name) {
            super(JSON_ARRAY, name, null, null, Mapping.JSON_ARRAY);
        }

        @Override
        jsonArray self() {
            return new jsonArray(name);
        }
    }

    final class binary extends Context.field<byte[], binary> implements Context.Field<byte[]>, Context.Value.Binary {
        public binary(String name) {
            super(BINARY, name, null, null, Mapping.BINARY);
        }

        @Override
        binary self() {
            return new binary(name);
        }
    }

    final class blob extends Context.field<io.vertx.core.buffer.Buffer, blob> implements Context.Field<io.vertx.core.buffer.Buffer>, Context.Value.Blob {
        public blob(String name) {
            super(BLOB, name, null, null, Mapping.BLOB);
        }

        @Override
        blob self() {
            return new blob(name);
        }
    }

//    public static void main(String[] args) {
//        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(Arrays.stream(Typing.class.getFields())
//                .map(f -> {
//                    var t = ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
//                    var n = f.getName();
//                    return """
//                            final class %1$s extends Context.field<%3$s, %1$s> implements Context.Field<%3$s>, Context.Value.%4$s {
//                                   public %1$s(String name) {
//                                       super(%2$s, name, null, null, Mapping.%2$s);
//                                   }
//                                   @Override
//                                   %1$s self() {
//                                       return new  %1$s(name);
//                                   }
//                               }
//                            """
//                            .formatted(
//                                    Cases.usn2cam.apply(n),
//                                    n,
//                                    t.getTypeName(),
//                                    n.startWith("JSON_") ? "J" + Cases.usn2pas.apply(n.substring(n.indexOf('_') + 1)) : Cases.usn2pas.apply(n)
//                            );
//                })
//                .filter(Predicate.not(String::isBlank))
//                .collect(Collectors.joining("\n"))), null);
//    }

}
*/