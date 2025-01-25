package vax.common.units;

import io.netty.buffer.ByteBuf;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Binary proto for transmit.
 * <ul>
 *     <li>v#: var int encoded</li>
 *     <li>z#: zigzag and var int encoded</li>
 *     <li>?Maybe: z4 of size, -1 for null</li>
 *     <li>bs: z4 of size, -1 for null</li>
 *     <li>str: z4 of size, -1 for null</li>
 * </ul>
 *
 * @author Zen.Liu
 * @since 2024-10-13
 */
public sealed interface Binary extends Buffer permits Binary.Impl {
    static Binary of(Buffer buffer) {
        return new Impl(buffer);
    }

    static Binary of(byte[] buffer) {
        return new Impl(Buffer.buffer(buffer));
    }

    static Binary of(int capacity) {
        return new Impl(Buffer.buffer(capacity));
    }

    static Binary of() {
        return new Impl(Buffer.buffer());
    }

    Buffer $raw();

    /**
     * @return internal read position
     */
    AtomicInteger $p();

    /**
     * @param n amount of bytes will be read.
     * @return start position to read
     */
    int $pos(int n);

    /**
     * fetch current read position then update for read one byte.
     */
    default int $pos() {
        return $pos(1);
    }

    int pos();

    record Impl(Buffer $raw, AtomicInteger $p) implements Binary {
        Impl(Buffer $raw) {
            this($raw, new AtomicInteger());
        }

        @Override
        public int pos() {
            return $p.get();
        }

        @Override
        public int $pos(int n) {
            return $p.getAndAdd(n);
        }

        @Override
        public String toString() {
            return $raw.toString();
        }

        @Override
        public String toString(String enc) {
            return $raw.toString(enc);
        }

        @Override
        public String toString(Charset enc) {
            return $raw.toString(enc);
        }

        @Override
        public JsonObject toJsonObject() {
            return $raw.toJsonObject();
        }

        @Override
        public JsonArray toJsonArray() {
            return $raw.toJsonArray();
        }

        @Override
        public Object toJsonValue() {
            return $raw.toJsonValue();
        }

        @Override
        @Deprecated
        public Object toJson() {
            return $raw.toJson();
        }

        @Override
        public byte getByte(int pos) {
            return $raw.getByte(pos);
        }

        @Override
        public short getUnsignedByte(int pos) {
            return $raw.getUnsignedByte(pos);
        }

        @Override
        public int getInt(int pos) {
            return $raw.getInt(pos);
        }

        @Override
        public int getIntLE(int pos) {
            return $raw.getIntLE(pos);
        }

        @Override
        public long getUnsignedInt(int pos) {
            return $raw.getUnsignedInt(pos);
        }

        @Override
        public long getUnsignedIntLE(int pos) {
            return $raw.getUnsignedIntLE(pos);
        }

        @Override
        public long getLong(int pos) {
            return $raw.getLong(pos);
        }

        @Override
        public long getLongLE(int pos) {
            return $raw.getLongLE(pos);
        }

        @Override
        public double getDouble(int pos) {
            return $raw.getDouble(pos);
        }

        @Override
        public float getFloat(int pos) {
            return $raw.getFloat(pos);
        }

        @Override
        public short getShort(int pos) {
            return $raw.getShort(pos);
        }

        @Override
        public short getShortLE(int pos) {
            return $raw.getShortLE(pos);
        }

        @Override
        public int getUnsignedShort(int pos) {
            return $raw.getUnsignedShort(pos);
        }

        @Override
        public int getUnsignedShortLE(int pos) {
            return $raw.getUnsignedShortLE(pos);
        }

        @Override
        public int getMedium(int pos) {
            return $raw.getMedium(pos);
        }

        @Override
        public int getMediumLE(int pos) {
            return $raw.getMediumLE(pos);
        }

        @Override
        public int getUnsignedMedium(int pos) {
            return $raw.getUnsignedMedium(pos);
        }

        @Override
        public int getUnsignedMediumLE(int pos) {
            return $raw.getUnsignedMediumLE(pos);
        }

        @Override
        public byte[] getBytes() {
            return $raw.getBytes();
        }

        @Override
        public byte[] getBytes(int start, int end) {
            return $raw.getBytes(start, end);
        }

        @Override
        public Binary getBytes(byte[] dst) {
            $raw.getBytes(dst);
            return this;
        }

        @Override
        public Binary getBytes(byte[] dst, int dstIndex) {
            $raw.getBytes(dst, dstIndex);
            return this;
        }

        @Override
        public Binary getBytes(int start, int end, byte[] dst) {
            $raw.getBytes(start, end, dst);
            return this;
        }

        @Override
        public Binary getBytes(int start, int end, byte[] dst, int dstIndex) {
            $raw.getBytes(start, end, dst, dstIndex);
            return this;
        }

        @Override
        public Buffer getBuffer(int start, int end) {
            return $raw.getBuffer(start, end);
        }

        @Override
        public String getString(int start, int end, String enc) {
            return $raw.getString(start, end, enc);
        }

        @Override
        public String getString(int start, int end) {
            return $raw.getString(start, end);
        }

        @Override
        public Binary appendBuffer(Buffer buff) {
            $raw.appendBuffer(buff);
            return this;
        }

        @Override
        public Binary appendBuffer(Buffer buff, int offset, int len) {
            $raw.appendBuffer(buff, offset, len);
            return this;
        }

        @Override
        public Binary appendBytes(byte[] bytes) {
            $raw.appendBytes(bytes);
            return this;
        }

        @Override
        public Binary appendBytes(byte[] bytes, int offset, int len) {
            $raw.appendBytes(bytes, offset, len);
            return this;
        }

        @Override
        public Binary appendByte(byte b) {
            $raw.appendByte(b);
            return this;
        }

        @Override
        public Binary appendUnsignedByte(short b) {
            $raw.appendUnsignedByte(b);
            return this;
        }

        @Override
        public Binary appendInt(int i) {
            $raw.appendInt(i);
            return this;
        }

        @Override
        public Binary appendIntLE(int i) {
            $raw.appendIntLE(i);
            return this;
        }

        @Override
        public Binary appendUnsignedInt(long i) {
            $raw.appendUnsignedInt(i);
            return this;
        }

        @Override
        public Binary appendUnsignedIntLE(long i) {
            $raw.appendUnsignedIntLE(i);
            return this;
        }

        @Override
        public Binary appendMedium(int i) {
            $raw.appendMedium(i);
            return this;
        }

        @Override
        public Binary appendMediumLE(int i) {
            $raw.appendMediumLE(i);
            return this;
        }

        @Override
        public Binary appendLong(long l) {
            $raw.appendLong(l);
            return this;
        }

        @Override
        public Binary appendLongLE(long l) {
            $raw.appendLongLE(l);
            return this;
        }

        @Override
        public Binary appendShort(short s) {
            $raw.appendShort(s);
            return this;
        }

        @Override
        public Binary appendShortLE(short s) {
            $raw.appendShortLE(s);
            return this;
        }

        @Override
        public Binary appendUnsignedShort(int s) {
            $raw.appendUnsignedShort(s);
            return this;
        }

        @Override
        public Binary appendUnsignedShortLE(int s) {
            $raw.appendUnsignedShortLE(s);
            return this;
        }

        @Override
        public Binary appendFloat(float f) {
            $raw.appendFloat(f);
            return this;
        }

        @Override
        public Binary appendDouble(double d) {
            $raw.appendDouble(d);
            return this;
        }

        @Override
        public Binary appendString(String str, String enc) {
            $raw.appendString(str, enc);
            return this;
        }

        @Override
        public Binary appendString(String str) {
            $raw.appendString(str);
            return this;
        }

        @Override
        public Binary setByte(int pos, byte b) {
            $raw.setByte(pos, b);
            return this;
        }

        @Override
        public Binary setUnsignedByte(int pos, short b) {
            $raw.setUnsignedByte(pos, b);
            return this;
        }

        @Override
        public Binary setInt(int pos, int i) {
            $raw.setInt(pos, i);
            return this;
        }

        @Override
        public Binary setIntLE(int pos, int i) {
            $raw.setIntLE(pos, i);
            return this;
        }

        @Override
        public Binary setUnsignedInt(int pos, long i) {
            $raw.setUnsignedInt(pos, i);
            return this;
        }

        @Override
        public Binary setUnsignedIntLE(int pos, long i) {
            $raw.setUnsignedIntLE(pos, i);
            return this;
        }

        @Override
        public Binary setMedium(int pos, int i) {
            $raw.setMedium(pos, i);
            return this;
        }

        @Override
        public Binary setMediumLE(int pos, int i) {
            $raw.setMediumLE(pos, i);
            return this;
        }

        @Override
        public Binary setLong(int pos, long l) {
            $raw.setLong(pos, l);
            return this;
        }

        @Override
        public Binary setLongLE(int pos, long l) {
            $raw.setLongLE(pos, l);
            return this;
        }

        @Override
        public Binary setDouble(int pos, double d) {
            $raw.setDouble(pos, d);
            return this;
        }

        @Override
        public Binary setFloat(int pos, float f) {
            $raw.setFloat(pos, f);
            return this;
        }

        @Override
        public Binary setShort(int pos, short s) {
            $raw.setShort(pos, s);
            return this;
        }

        @Override
        public Binary setShortLE(int pos, short s) {
            $raw.setShortLE(pos, s);
            return this;
        }

        @Override
        public Binary setUnsignedShort(int pos, int s) {
            $raw.setUnsignedShort(pos, s);
            return this;
        }

        @Override
        public Binary setUnsignedShortLE(int pos, int s) {
            $raw.setUnsignedShortLE(pos, s);
            return this;
        }

        @Override
        public Binary setBuffer(int pos, Buffer b) {
            $raw.setBuffer(pos, b);
            return this;
        }

        @Override
        public Binary setBuffer(int pos, Buffer b, int offset, int len) {
            $raw.setBuffer(pos, b, offset, len);
            return this;
        }

        @Override
        public Binary setBytes(int pos, ByteBuffer b) {
            $raw.setBytes(pos, b);
            return this;
        }

        @Override
        public Binary setBytes(int pos, byte[] b) {
            $raw.setBytes(pos, b);
            return this;
        }

        @Override
        public Binary setBytes(int pos, byte[] b, int offset, int len) {
            $raw.setBytes(pos, b, offset, len);
            return this;
        }

        @Override
        public Binary setString(int pos, String str) {
            $raw.setString(pos, str);
            return this;
        }

        @Override
        public Binary setString(int pos, String str, String enc) {
            $raw.setString(pos, str, enc);
            return this;
        }

        @Override
        public int length() {
            return $raw.length();
        }

        @Override
        public Binary copy() {
            return new Impl($raw.copy());
        }

        @Override
        public Binary slice() {
            return new Impl($raw.slice());
        }

        @Override
        public Binary slice(int start, int end) {
            return new Impl($raw.slice(start, end));
        }

        @Override
        @Deprecated
        public ByteBuf getByteBuf() {
            return $raw.getByteBuf();
        }

        @Override
        public void writeToBuffer(Buffer buffer) {
            $raw.writeToBuffer(buffer);
        }

        @Override
        public int readFromBuffer(int pos, Buffer buffer) {
            return $raw.readFromBuffer(pos, buffer);
        }
    }


    //region Fluent
    @Override
    Binary getBytes(byte[] dst);

    @Override
    Binary getBytes(byte[] dst, int dstIndex);

    @Override
    Binary getBytes(int start, int end, byte[] dst);

    @Override
    Binary getBytes(int start, int end, byte[] dst, int dstIndex);


    @Override
    Binary appendBuffer(Buffer buff);

    @Override
    Binary appendBuffer(Buffer buff, int offset, int len);

    @Override
    Binary appendBytes(byte[] bytes);

    @Override
    Binary appendBytes(byte[] bytes, int offset, int len);

    @Override
    Binary appendByte(byte b);

    @Override
    Binary appendUnsignedByte(short b);

    @Override
    Binary appendInt(int i);

    @Override
    Binary appendIntLE(int i);

    @Override
    Binary appendUnsignedInt(long i);

    @Override
    Binary appendUnsignedIntLE(long i);

    @Override
    Binary appendMedium(int i);

    @Override
    Binary appendMediumLE(int i);

    @Override
    Binary appendLong(long l);

    @Override
    Binary appendLongLE(long l);

    @Override
    Binary appendShort(short s);

    @Override
    Binary appendShortLE(short s);

    @Override
    Binary appendUnsignedShort(int s);

    @Override
    Binary appendUnsignedShortLE(int s);

    @Override
    Binary appendFloat(float f);

    @Override
    Binary appendDouble(double d);

    @Override
    Binary appendString(String str, String enc);

    @Override
    Binary appendString(String str);

    @Override
    Binary setByte(int pos, byte b);

    @Override
    Binary setUnsignedByte(int pos, short b);

    @Override
    Binary setInt(int pos, int i);

    @Override
    Binary setIntLE(int pos, int i);

    @Override
    Binary setUnsignedInt(int pos, long i);

    @Override
    Binary setUnsignedIntLE(int pos, long i);

    @Override
    Binary setMedium(int pos, int i);

    @Override
    Binary setMediumLE(int pos, int i);

    @Override
    Binary setLong(int pos, long l);

    @Override
    Binary setLongLE(int pos, long l);

    @Override
    Binary setDouble(int pos, double d);

    @Override
    Binary setFloat(int pos, float f);

    @Override
    Binary setShort(int pos, short s);

    @Override
    Binary setShortLE(int pos, short s);

    @Override
    Binary setUnsignedShort(int pos, int s);

    @Override
    Binary setUnsignedShortLE(int pos, int s);

    @Override
    Binary setBuffer(int pos, Buffer b);

    @Override
    Binary setBuffer(int pos, Buffer b, int offset, int len);

    @Override
    Binary setBytes(int pos, ByteBuffer b);

    @Override
    Binary setBytes(int pos, byte[] b);

    @Override
    Binary setBytes(int pos, byte[] b, int offset, int len);

    @Override
    Binary setString(int pos, String str);

    @Override
    Binary setString(int pos, String str, String enc);

    @Override
    Binary copy();

    @Override
    Binary slice();

    @Override
    Binary slice(int start, int end);
    //endregion

    /**
     * read one byte as boolean
     */
    default boolean b() {
        var v = getByte($pos(1));
        return v > 0;
    }

    /**
     * write boolean as  one byte
     */
    default Binary b(boolean b) {
        return appendByte((byte) (b ? 1 : -1));
    }

    /**
     * read one byte
     */
    default byte i8() {
        return getByte($pos());
    }

    default Binary i8(byte b) {
        return appendByte(b);
    }

    /**
     * read two byte as short
     */
    default short i16() {
        return getShort($pos(2));
    }

    default Binary i16(short b) {
        return appendShort(b);
    }

    /**
     * read four byte as int
     */
    default int i32() {
        return getInt($pos(4));
    }

    default Binary i32(int b) {
        return appendInt(b);
    }

    /**
     * read eight byte as long int
     */
    default long i64() {
        return getLong($pos(8));
    }

    default Binary i64(long b) {
        return appendLong(b);
    }

    /**
     * read four byte as float
     */
    default float f32() {
        return getFloat($pos(4));
    }

    default Binary f32(float b) {
        return appendFloat(b);
    }

    /**
     * read eight byte as double float
     */
    default double f64() {
        return getDouble($pos(8));
    }

    default Binary f64(double b) {
        return appendDouble(b);
    }

    /**
     * read var int
     */
    default int v32() {
        var result = 0;
        var shift = 0;
        var by = i8();
        while (true) {
            result |= (by & 0x7f) << shift;
            if ((by & 0x80) != 0x80) break;
            by = i8();
            shift += 7;
        }
        return result;
    }

    default Binary v32(int n) {
        while ((n & ~0x7F) != 0) {
            i8(((byte) ((n & 0x7F) | 0x80)));
            n >>>= 7;
        }
        return i8((byte) n); //!Last
    }


    /**
     * read var long int
     */
    default long v64() {
        var result = 0L;
        var shift = 0L;
        var by = i8();
        while (true) {
            result |= (by & 0x7fL) << shift;
            if ((by & 0x80) != 0x80) break;
            by = i8();
            shift += 7L;
        }
        return result;
    }

    default Binary v64(long n) {
        while ((n & ~0x7FL) != 0) {
            i8(((byte) ((n & 0x7FL) | 0x80L)));
            n >>>= 7;
        }
        return i8((byte) n); //!Last
    }

    /**
     * read zig-zag var int
     */
    default int z32() {
        var v = v32();
        return (v >> 1) ^ -(v & 1);
    }

    default Binary z32(int v) {
        v = (v << 1) ^ (v >> 31);
        return v32(v);
    }

    /**
     * read zig-zag var long int
     */
    default long z64() {
        var v = v64();
        return (v >> 1L) ^ -(v & 1L);
    }

    default Binary z64(long v) {
        v = (v << 1L) ^ (v >> 63);
        return v64(v);
    }

    /**
     * read size bytes,allow null.
     */
    default byte @Nullable [] bin() {
        var n = z32();
        if (n < 0) return null;
        var b = new byte[n];
        if (n == 0) return b;
        var p = $pos(n);
        getBytes(p, pos(), b);
        return b;
    }

    default Binary bin(byte @Nullable [] n) {
        if (n == null) {
            return z32(-1);
        }
        if (n.length == 0) {
            return z32(0);
        }
        return z32(n.length).appendBytes(n);
    }

    default byte[] binary() {
        var n = v32();
        if (n < 0) throw new IllegalStateException("should not be null");
        var b = new byte[n];
        if (n == 0) return b;
        var p = $pos(n);
        getBytes(p, pos(), b);
        return b;
    }

    default Binary binary(byte[] n) {
        if (n.length == 0) {
            return v32(0);
        }
        return v32(n.length).appendBytes(n);
    }

    /**
     * @return peek a byte
     */
    default byte peek1() {
        return getByte(pos());
    }

    /**
     * @return peek 4 byte
     */
    default int peek4() {
        return getInt(pos());
    }

    /**
     * @return peek 8 byte
     */
    default long peek8() {
        return getLong(pos());
    }

    default String str() {
        var b = bin();
        if (b == null) return null;
        if (b.length == 0) return "";
        return new String(b, StandardCharsets.UTF_8);
    }

    default Binary str(String v) {
        if (v == null) return z32(-1);
        if (v.isBlank()) return z32(0);
        return bin(v.getBytes(StandardCharsets.UTF_8));
    }

    default Binary accept(Consumer<Binary> act) {
        act.accept(this);
        return this;
    }

    default <R> R apply(Function<Binary, R> act) {
        return act.apply(this);
    }

    default <R> R sized(BiFunction<Binary, Integer, R> act) {
        return act.apply(this, v32());
    }

    default <R> Optional<R> sizedOpt(BiFunction<Binary, Integer, R> act) {
        var z = z32();
        if (z < 0) return Optional.empty();
        return Optional.of(act.apply(this, z));
    }


}
