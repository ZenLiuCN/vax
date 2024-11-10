package cn.zenliu.vax.common.units;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.buffer.impl.BufferImpl;
import io.vertx.core.buffer.impl.VertxByteBufAllocator;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
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
public interface BinaryX {
    ByteBuf raw();

    default int writeable() {
        return raw().capacity() - raw().writerIndex();
    }

    default int capacity() {
        return raw().capacity();
    }

    default int readable() {
        return length();
    }

    default int length() {
        return raw().readableBytes();
    }

    default BinaryX slice(int from, int to) {
        return create(raw().slice(from, to));
    }

    default Buffer toBuffer() {
        return BufferImpl.buffer(raw());
    }

    default boolean b() {
        var v = raw().readByte();
        return v > 0;
    }

    default BinaryX b(boolean b) {
        raw().writeByte(b ? 1 : -1);
        return this;
    }

    default byte i1() {
        return raw().readByte();
    }

    default BinaryX i1(byte b) {
        raw().writeByte(b);
        return this;
    }

    default short i2() {
        return raw().readShort();
    }

    default BinaryX i2(short b) {
        raw().writeShort(b);
        return this;
    }

    default int i4() {
        return raw().readInt();
    }

    default BinaryX i4(int b) {
        raw().writeInt(b);
        return this;
    }

    default long i8() {
        return raw().readLong();
    }

    default BinaryX i8(long b) {
        raw().writeLong(b);
        return this;
    }


    default float f4() {
        return raw().readFloat();
    }

    default BinaryX f4(float b) {
        raw().writeFloat(b);
        return this;
    }

    default double f8() {
        return raw().readDouble();
    }

    default BinaryX f8(double b) {
        raw().writeDouble(b);
        return this;
    }


    default int v4() {
        var b = raw();
        var result = 0;
        var shift = 0;
        var by = b.readByte();
        while (true) {
            result |= (by & 0x7f) << shift;
            if ((by & 0x80) != 0x80) break;
            by = b.readByte();
            shift += 7;
        }
        return result;
    }

    default BinaryX v4(int n) {
        var b = raw();
        while ((n & ~0x7F) != 0) {
            b.writeByte(((byte) ((n & 0x7F) | 0x80)));
            n >>>= 7;
        }
        b.writeByte((byte) n); //!Last
        return this;
    }


    default long v8() {
        var b = raw();
        var result = 0L;
        var shift = 0L;
        var by = b.readByte();
        while (true) {
            result |= (by & 0x7fL) << shift;
            if ((by & 0x80) != 0x80) break;
            by = b.readByte();
            shift += 7L;
        }
        return result;
    }

    default BinaryX v8(long n) {
        var b = raw();
        while ((n & ~0x7FL) != 0) {
            b.writeByte(((byte) ((n & 0x7FL) | 0x80L)));
            n >>>= 7;
        }
        b.writeByte((byte) n); //!Last
        return this;
    }

    default int z4() {
        var v = v4();
        return (v >> 1) ^ -(v & 1);
    }

    default BinaryX z4(int v) {
        v = (v << 1) ^ (v >> 31);
        return v4(v);
    }

    default long z8() {
        var v = v8();
        return (v >> 1L) ^ -(v & 1L);
    }

    default BinaryX z8(long v) {
        v = (v << 1L) ^ (v >> 63);
        return v8(v);
    }

    default byte @Nullable [] $bs() {
        var n = v4();
        if (n < 0) return null;
        var b = new byte[n];
        if (n == 0) return b;
        raw().readBytes(b);
        return b;
    }

    default BinaryX $bs(byte @Nullable [] n) {
        if (n == null) {
            return v4(-1);
        }
        if (n.length == 0) {
            return v4(0);
        }
        v4(n.length).raw().writeBytes(n);
        return this;
    }

    /**
     * @return peek a byte
     */
    default byte $p1() {
        var b = raw();
        return b.getByte(b.readerIndex());
    }

    /**
     * @return peek 4 byte
     */
    default int $p4() {
        var b = raw();
        return b.getInt(b.readerIndex());
    }

    /**
     * @return peek 8 byte
     */
    default long $p8() {
        var b = raw();
        return b.getLong(b.readerIndex());
    }

    default String str() {
        var n = z4();
        if (n == -1) return null;
        if (n == 0) return "";
        var b = new byte[n];
        raw().readBytes(b);
        return new String(b, StandardCharsets.UTF_8);
    }

    default BinaryX str(String v) {
        if (v == null) {
            return z4(-1);
        }
        if (v.isEmpty()) {
            return z4(0);
        }
        var a = v.getBytes(StandardCharsets.UTF_8);
        z4(a.length).raw().writeBytes(a);
        return this;
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

    default <R> R sizedMaybe(BiFunction<BinaryX, Integer, R> act) {
        return act.apply(this, z4());
    }


    /**
     * @param write the write action
     * @return range of byte index that write
     */
    default Range range(Consumer<BinaryX> write) {
        var v = raw().writerIndex();
        write.accept(this);
        return new Range(v, raw().writerIndex());
    }

    default Range meta(long id, Consumer<BinaryX> act) {
        return range(a -> act.accept(a.i8(id)));
    }

    default <R> R meta(BiFunction<BinaryX, Long, R> act) {
        return  act.apply(this, i8());
    }

    record Range(int from, int to) {
    }

    static BinaryX create(Buffer buffer) {
        return new P(buffer instanceof BufferImpl f ? f.byteBuf() : P.alloc.buffer().writeBytes(buffer.getBytes()));
    }

    static BinaryX create(ByteBuf buf) {
        return new P(buf);
    }

    static BinaryX create(int capacity) {
        return new P(P.alloc.buffer(capacity));
    }

    record P(ByteBuf raw) implements BinaryX {
        public static final ByteBufAllocator alloc = VertxByteBufAllocator.DEFAULT;
    }
}
