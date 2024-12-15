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
public sealed interface BinaryX extends Buffer permits BinaryX.Impl {
    static BinaryX of(Buffer buffer) {
        return new Impl(buffer);
    }

    static BinaryX of(byte[] buffer) {
        return new Impl(Buffer.buffer(buffer));
    }

    static BinaryX of(int capacity) {
        return new Impl(Buffer.buffer(capacity));
    }

    static BinaryX of() {
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

    record Impl(Buffer $raw, AtomicInteger $p) implements BinaryX {
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
        public BinaryX getBytes(byte[] dst) {
            $raw.getBytes(dst);
            return this;
        }

        @Override
        public BinaryX getBytes(byte[] dst, int dstIndex) {
            $raw.getBytes(dst, dstIndex);
            return this;
        }

        @Override
        public BinaryX getBytes(int start, int end, byte[] dst) {
            $raw.getBytes(start, end, dst);
            return this;
        }

        @Override
        public BinaryX getBytes(int start, int end, byte[] dst, int dstIndex) {
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
        public BinaryX appendBuffer(Buffer buff) {
            $raw.appendBuffer(buff);
            return this;
        }

        @Override
        public BinaryX appendBuffer(Buffer buff, int offset, int len) {
            $raw.appendBuffer(buff, offset, len);
            return this;
        }

        @Override
        public BinaryX appendBytes(byte[] bytes) {
            $raw.appendBytes(bytes);
            return this;
        }

        @Override
        public BinaryX appendBytes(byte[] bytes, int offset, int len) {
            $raw.appendBytes(bytes, offset, len);
            return this;
        }

        @Override
        public BinaryX appendByte(byte b) {
            $raw.appendByte(b);
            return this;
        }

        @Override
        public BinaryX appendUnsignedByte(short b) {
            $raw.appendUnsignedByte(b);
            return this;
        }

        @Override
        public BinaryX appendInt(int i) {
            $raw.appendInt(i);
            return this;
        }

        @Override
        public BinaryX appendIntLE(int i) {
            $raw.appendIntLE(i);
            return this;
        }

        @Override
        public BinaryX appendUnsignedInt(long i) {
            $raw.appendUnsignedInt(i);
            return this;
        }

        @Override
        public BinaryX appendUnsignedIntLE(long i) {
            $raw.appendUnsignedIntLE(i);
            return this;
        }

        @Override
        public BinaryX appendMedium(int i) {
            $raw.appendMedium(i);
            return this;
        }

        @Override
        public BinaryX appendMediumLE(int i) {
            $raw.appendMediumLE(i);
            return this;
        }

        @Override
        public BinaryX appendLong(long l) {
            $raw.appendLong(l);
            return this;
        }

        @Override
        public BinaryX appendLongLE(long l) {
            $raw.appendLongLE(l);
            return this;
        }

        @Override
        public BinaryX appendShort(short s) {
            $raw.appendShort(s);
            return this;
        }

        @Override
        public BinaryX appendShortLE(short s) {
            $raw.appendShortLE(s);
            return this;
        }

        @Override
        public BinaryX appendUnsignedShort(int s) {
            $raw.appendUnsignedShort(s);
            return this;
        }

        @Override
        public BinaryX appendUnsignedShortLE(int s) {
            $raw.appendUnsignedShortLE(s);
            return this;
        }

        @Override
        public BinaryX appendFloat(float f) {
            $raw.appendFloat(f);
            return this;
        }

        @Override
        public BinaryX appendDouble(double d) {
            $raw.appendDouble(d);
            return this;
        }

        @Override
        public BinaryX appendString(String str, String enc) {
            $raw.appendString(str, enc);
            return this;
        }

        @Override
        public BinaryX appendString(String str) {
            $raw.appendString(str);
            return this;
        }

        @Override
        public BinaryX setByte(int pos, byte b) {
            $raw.setByte(pos, b);
            return this;
        }

        @Override
        public BinaryX setUnsignedByte(int pos, short b) {
            $raw.setUnsignedByte(pos, b);
            return this;
        }

        @Override
        public BinaryX setInt(int pos, int i) {
            $raw.setInt(pos, i);
            return this;
        }

        @Override
        public BinaryX setIntLE(int pos, int i) {
            $raw.setIntLE(pos, i);
            return this;
        }

        @Override
        public BinaryX setUnsignedInt(int pos, long i) {
            $raw.setUnsignedInt(pos, i);
            return this;
        }

        @Override
        public BinaryX setUnsignedIntLE(int pos, long i) {
            $raw.setUnsignedIntLE(pos, i);
            return this;
        }

        @Override
        public BinaryX setMedium(int pos, int i) {
            $raw.setMedium(pos, i);
            return this;
        }

        @Override
        public BinaryX setMediumLE(int pos, int i) {
            $raw.setMediumLE(pos, i);
            return this;
        }

        @Override
        public BinaryX setLong(int pos, long l) {
            $raw.setLong(pos, l);
            return this;
        }

        @Override
        public BinaryX setLongLE(int pos, long l) {
            $raw.setLongLE(pos, l);
            return this;
        }

        @Override
        public BinaryX setDouble(int pos, double d) {
            $raw.setDouble(pos, d);
            return this;
        }

        @Override
        public BinaryX setFloat(int pos, float f) {
            $raw.setFloat(pos, f);
            return this;
        }

        @Override
        public BinaryX setShort(int pos, short s) {
            $raw.setShort(pos, s);
            return this;
        }

        @Override
        public BinaryX setShortLE(int pos, short s) {
            $raw.setShortLE(pos, s);
            return this;
        }

        @Override
        public BinaryX setUnsignedShort(int pos, int s) {
            $raw.setUnsignedShort(pos, s);
            return this;
        }

        @Override
        public BinaryX setUnsignedShortLE(int pos, int s) {
            $raw.setUnsignedShortLE(pos, s);
            return this;
        }

        @Override
        public BinaryX setBuffer(int pos, Buffer b) {
            $raw.setBuffer(pos, b);
            return this;
        }

        @Override
        public BinaryX setBuffer(int pos, Buffer b, int offset, int len) {
            $raw.setBuffer(pos, b, offset, len);
            return this;
        }

        @Override
        public BinaryX setBytes(int pos, ByteBuffer b) {
            $raw.setBytes(pos, b);
            return this;
        }

        @Override
        public BinaryX setBytes(int pos, byte[] b) {
            $raw.setBytes(pos, b);
            return this;
        }

        @Override
        public BinaryX setBytes(int pos, byte[] b, int offset, int len) {
            $raw.setBytes(pos, b, offset, len);
            return this;
        }

        @Override
        public BinaryX setString(int pos, String str) {
            $raw.setString(pos, str);
            return this;
        }

        @Override
        public BinaryX setString(int pos, String str, String enc) {
            $raw.setString(pos, str, enc);
            return this;
        }

        @Override
        public int length() {
            return $raw.length();
        }

        @Override
        public BinaryX copy() {
            return new Impl($raw.copy());
        }

        @Override
        public BinaryX slice() {
            return new Impl($raw.slice());
        }

        @Override
        public BinaryX slice(int start, int end) {
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
    BinaryX getBytes(byte[] dst);

    @Override
    BinaryX getBytes(byte[] dst, int dstIndex);

    @Override
    BinaryX getBytes(int start, int end, byte[] dst);

    @Override
    BinaryX getBytes(int start, int end, byte[] dst, int dstIndex);


    @Override
    BinaryX appendBuffer(Buffer buff);

    @Override
    BinaryX appendBuffer(Buffer buff, int offset, int len);

    @Override
    BinaryX appendBytes(byte[] bytes);

    @Override
    BinaryX appendBytes(byte[] bytes, int offset, int len);

    @Override
    BinaryX appendByte(byte b);

    @Override
    BinaryX appendUnsignedByte(short b);

    @Override
    BinaryX appendInt(int i);

    @Override
    BinaryX appendIntLE(int i);

    @Override
    BinaryX appendUnsignedInt(long i);

    @Override
    BinaryX appendUnsignedIntLE(long i);

    @Override
    BinaryX appendMedium(int i);

    @Override
    BinaryX appendMediumLE(int i);

    @Override
    BinaryX appendLong(long l);

    @Override
    BinaryX appendLongLE(long l);

    @Override
    BinaryX appendShort(short s);

    @Override
    BinaryX appendShortLE(short s);

    @Override
    BinaryX appendUnsignedShort(int s);

    @Override
    BinaryX appendUnsignedShortLE(int s);

    @Override
    BinaryX appendFloat(float f);

    @Override
    BinaryX appendDouble(double d);

    @Override
    BinaryX appendString(String str, String enc);

    @Override
    BinaryX appendString(String str);

    @Override
    BinaryX setByte(int pos, byte b);

    @Override
    BinaryX setUnsignedByte(int pos, short b);

    @Override
    BinaryX setInt(int pos, int i);

    @Override
    BinaryX setIntLE(int pos, int i);

    @Override
    BinaryX setUnsignedInt(int pos, long i);

    @Override
    BinaryX setUnsignedIntLE(int pos, long i);

    @Override
    BinaryX setMedium(int pos, int i);

    @Override
    BinaryX setMediumLE(int pos, int i);

    @Override
    BinaryX setLong(int pos, long l);

    @Override
    BinaryX setLongLE(int pos, long l);

    @Override
    BinaryX setDouble(int pos, double d);

    @Override
    BinaryX setFloat(int pos, float f);

    @Override
    BinaryX setShort(int pos, short s);

    @Override
    BinaryX setShortLE(int pos, short s);

    @Override
    BinaryX setUnsignedShort(int pos, int s);

    @Override
    BinaryX setUnsignedShortLE(int pos, int s);

    @Override
    BinaryX setBuffer(int pos, Buffer b);

    @Override
    BinaryX setBuffer(int pos, Buffer b, int offset, int len);

    @Override
    BinaryX setBytes(int pos, ByteBuffer b);

    @Override
    BinaryX setBytes(int pos, byte[] b);

    @Override
    BinaryX setBytes(int pos, byte[] b, int offset, int len);

    @Override
    BinaryX setString(int pos, String str);

    @Override
    BinaryX setString(int pos, String str, String enc);

    @Override
    BinaryX copy();

    @Override
    BinaryX slice();

    @Override
    BinaryX slice(int start, int end);
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
    default BinaryX b(boolean b) {
        return appendByte((byte) (b ? 1 : -1));
    }

    /**
     * read one byte
     */
    default byte i1() {
        return getByte($pos());
    }

    default BinaryX i1(byte b) {
        return appendByte(b);
    }

    /**
     * read two byte as short
     */
    default short i2() {
        return getShort($pos(2));
    }

    default BinaryX i2(short b) {
        return appendShort(b);
    }

    /**
     * read four byte as int
     */
    default int i4() {
        return getInt($pos(4));
    }

    default BinaryX i4(int b) {
        return appendInt(b);
    }

    /**
     * read eight byte as long int
     */
    default long i8() {
        return getLong($pos(8));
    }

    default BinaryX i8(long b) {
        return appendLong(b);
    }

    /**
     * read four byte as float
     */
    default float f4() {
        return getFloat($pos(4));
    }

    default BinaryX f4(float b) {
        return appendFloat(b);
    }

    /**
     * read eight byte as double float
     */
    default double f8() {
        return getDouble($pos(8));
    }

    default BinaryX f8(double b) {
        return appendDouble(b);
    }

    /**
     * read var int
     */
    default int v4() {
        var result = 0;
        var shift = 0;
        var by = i1();
        while (true) {
            result |= (by & 0x7f) << shift;
            if ((by & 0x80) != 0x80) break;
            by = i1();
            shift += 7;
        }
        return result;
    }

    default BinaryX v4(int n) {
        while ((n & ~0x7F) != 0) {
            i1(((byte) ((n & 0x7F) | 0x80)));
            n >>>= 7;
        }
        return i1((byte) n); //!Last
    }


    /**
     * read var long int
     */
    default long v8() {
        var result = 0L;
        var shift = 0L;
        var by = i1();
        while (true) {
            result |= (by & 0x7fL) << shift;
            if ((by & 0x80) != 0x80) break;
            by = i1();
            shift += 7L;
        }
        return result;
    }

    default BinaryX v8(long n) {
        while ((n & ~0x7FL) != 0) {
            i1(((byte) ((n & 0x7FL) | 0x80L)));
            n >>>= 7;
        }
        return i1((byte) n); //!Last
    }

    /**
     * read zig-zag var int
     */
    default int z4() {
        var v = v4();
        return (v >> 1) ^ -(v & 1);
    }

    default BinaryX z4(int v) {
        v = (v << 1) ^ (v >> 31);
        return v4(v);
    }

    /**
     * read zig-zag var long int
     */
    default long z8() {
        var v = v8();
        return (v >> 1L) ^ -(v & 1L);
    }

    default BinaryX z8(long v) {
        v = (v << 1L) ^ (v >> 63);
        return v8(v);
    }

    /**
     * read size bytes,allow null.
     */
    default byte @Nullable [] bin() {
        var n = z4();
        if (n < 0) return null;
        var b = new byte[n];
        if (n == 0) return b;
        var p = $pos(n);
        getBytes(p, pos(), b);
        return b;
    }

    default BinaryX bin(byte @Nullable [] n) {
        if (n == null) {
            return z4(-1);
        }
        if (n.length == 0) {
            return z4(0);
        }
        return z4(n.length).appendBytes(n);
    }

    default byte[] binary() {
        var n = v4();
        if (n < 0) throw new IllegalStateException("should not be null");
        var b = new byte[n];
        if (n == 0) return b;
        var p = $pos(n);
        getBytes(p, pos(), b);
        return b;
    }

    default BinaryX binary(byte[] n) {
        if (n.length == 0) {
            return v4(0);
        }
        return v4(n.length).appendBytes(n);
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

    default BinaryX str(String v) {
        if (v == null) return z4(-1);
        if (v.isBlank()) return z4(0);
        return bin(v.getBytes(StandardCharsets.UTF_8));
    }

    default BinaryX accept(Consumer<BinaryX> act) {
        act.accept(this);
        return this;
    }

    default <R> R apply(Function<BinaryX, R> act) {
        return act.apply(this);
    }

    default <R> R sized(BiFunction<BinaryX, Integer, R> act) {
        return act.apply(this, v4());
    }

    default <R> Optional<R> sizedOpt(BiFunction<BinaryX, Integer, R> act) {
        var z = z4();
        if (z < 0) return Optional.empty();
        return Optional.of(act.apply(this, z));
    }


}
