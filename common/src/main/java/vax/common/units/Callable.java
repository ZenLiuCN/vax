package vax.common.units;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.serviceproxy.HelperUtils;
import io.vertx.serviceproxy.ServiceException;
import org.jooq.lambda.function.*;
import org.jooq.lambda.tuple.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vax.common.trait.Disposable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Zen.Liu
 * @since 2025-01-21
 */
public interface Callable {
    Logger log= LoggerFactory.getLogger(Callable.class);
    interface Callee<I, O> extends Callable {
        I dec(Buffer i);

        Buffer enc(O o);

        Future<O> apply(I v);

        default void handle(Message<Buffer> v) {
            try {
                apply(dec(v.body()))
                        .map(this::enc)
                        .onSuccess(v::reply)
                        .onFailure(ex -> failure(ex, v, this.getClass()));
            } catch (Exception ex) {
                failure(ex, v, this.getClass());
            }
        }

        interface callee<I, O> extends Callee<I, O> {
            Function<Buffer, I> decode();

            Function<O, Buffer> encode();

            @Override
            default I dec(Buffer i) {
                return decode().apply(i);
            }

            @Override
            default Buffer enc(O o) {
                return encode().apply(o);
            }
        }

        record Handler(
                String address,
                boolean[] closed,
                Supplier<Future<Void>> closer
        ) implements Disposable {
            Handler(String address, Supplier<Future<Void>> closer) {
                this(address, new boolean[]{false}, closer);
            }

            @Override
            public Future<Void> dispose() {
                if (closed[0]) return Future.succeededFuture();
                return closer.get();
            }
        }
    }

    interface Caller<I, O> extends Callable {
        String address();

        EventBus bus();

        O dec(Buffer o);

        Buffer enc(I i);

        default Future<O> apply(I v) {
            return bus()
                    .<Buffer>request(address(), enc(v))
                    .map(Message::body)
                    .map(this::dec)
                    ;
        }

        interface caller<I, O> extends Caller<I, O> {
            Function<Buffer, O> decode();

            Function<I, Buffer> encode();

            @Override
            default O dec(Buffer i) {
                return decode().apply(i);
            }

            @Override
            default Buffer enc(I o) {
                return encode().apply(o);
            }
        }

    }



    static void failure(Throwable t, Message<Buffer> msg, Class<?> owner) {
        var d = log.isDebugEnabled();
        if (t instanceof ServiceException se) {
            if (d)
                msg.reply(new ServiceException(se.failureCode(), se.getMessage(), HelperUtils.generateDebugInfo(t)));
            else
                msg.reply(new ServiceException(se.failureCode(), se.getMessage()));
        } else if (t instanceof DomainError de) {
            if (d)
                msg.reply(new ServiceException(de.code, de.$toJson().encode(), HelperUtils.generateDebugInfo(t)));
            else
                msg.reply(new ServiceException(de.code, de.$toJson().encode()));
        } else {
            if (d)
                msg.reply(new ServiceException(
                        500,
                        DomainError.internalServerError()
                                   .sys("owner {}", owner.getSimpleName()
                                           , t)
                                   .$toJson()
                                   .encode()
                        , HelperUtils.generateDebugInfo(t)));
            else {
                msg.reply(new ServiceException(500, DomainError
                        .internalServerError()
                        .sys("owner {}", owner.getSimpleName()
                                , t).$toJson().encode()));
            }

        }
    }


    interface Fn1<I0, O> extends Function1<I0, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function1<Buffer, I0> decode,
                Function1<O, Buffer> encode) {
            var ce = new Callee1<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee1<I0, O>(
                Function<Buffer, I0> decode,
                Function<O, Buffer> encode,
                Fn1<I0, O> fn
        ) implements Callable.Callee.callee<I0, O> {
            @Override
            public Future<O> apply(I0 v) {
                return fn.apply(v);
            }
        }
        static <I0, O> Fn1<I0, O> caller(EventBus bus, String address,
                                         Function<I0, Buffer> encode,
                                         Function<Buffer, O> decode) {
            return new Caller1<>(encode, decode, address, bus);
        }

        record Caller1<I0, O>(
                Function<I0, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<I0, O>, Fn1<I0, O> {
            @Override
            public Future<O> apply(I0 v) {
                return caller.super.apply(v);
            }
        }
    }

    interface Fn2<I0,I1, O> extends Function2<I0,I1, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple2<I0,I1>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee2<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee2<I0,I1, O>(
                Function<Buffer, Tuple2<I0,I1>> decode,
                Function<O, Buffer> encode,
                Fn2<I0,I1, O> fn
        ) implements Callable.Callee.callee<Tuple2<I0,I1>, O> {
            @Override
            public Future<O> apply(Tuple2<I0,I1> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1, O> Fn2<I0,I1, O> caller(EventBus bus, String address,
                                               Function<Tuple2<? extends I0,? extends I1>, Buffer> encode,
                                               Function<Buffer, O> decode) {
            return new Caller2<>(encode, decode, address, bus);
        }

        record Caller2<I0,I1, O>(
                Function<Tuple2<? extends I0,? extends I1>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple2<? extends I0,? extends I1>, O>, Fn2<I0,I1, O> {
            @Override
            public Future<O> apply(Tuple2<? extends I0,? extends I1> v) {
                return caller.super.apply(v);
            }

            @Override
            public Future<O> apply(I0 v1, I1 v2) {
                return apply(Tuple.tuple(v1,v2));
            }
        }
    }

    interface Fn3<I0,I1,I2, O> extends Function3<I0,I1,I2, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple3<I0,I1,I2>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee3<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee3<I0,I1,I2, O>(
                Function<Buffer, Tuple3<I0,I1,I2>> decode,
                Function<O, Buffer> encode,
                Fn3<I0,I1,I2, O> fn
        ) implements Callable.Callee.callee<Tuple3<I0,I1,I2>, O> {
            @Override
            public Future<O> apply(Tuple3<I0,I1,I2> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1,I2, O> Fn3<I0,I1,I2, O> caller(EventBus bus, String address,
                                                     Function<Tuple3<? extends I0,? extends I1,? extends I2>, Buffer> encode,
                                                     Function<Buffer, O> decode) {
            return new Caller3<>(encode, decode, address, bus);
        }

        record Caller3<I0,I1,I2, O>(
                Function<Tuple3<? extends I0,? extends I1,? extends I2>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple3<? extends I0,? extends I1,? extends I2>, O>, Fn3<I0,I1,I2, O> {
            @Override
            public Future<O> apply(Tuple3<? extends I0,? extends I1,? extends I2> v) {
                return caller.super.apply(v);
            }
            @Override
            public Future<O> apply(I0 i0,I1 i1,I2 i2) {
                return apply(Tuple.tuple(i0,i1,i2));
            }
        }
    }

    interface Fn4<I0,I1,I2,I3, O> extends Function4<I0,I1,I2,I3, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple4<I0,I1,I2,I3>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee4<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee4<I0,I1,I2,I3, O>(
                Function<Buffer, Tuple4<I0,I1,I2,I3>> decode,
                Function<O, Buffer> encode,
                Fn4<I0,I1,I2,I3, O> fn
        ) implements Callable.Callee.callee<Tuple4<I0,I1,I2,I3>, O> {
            @Override
            public Future<O> apply(Tuple4<I0,I1,I2,I3> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1,I2,I3, O> Fn4<I0,I1,I2,I3, O> caller(EventBus bus, String address,
                                                           Function<Tuple4<? extends I0,? extends I1,? extends I2,? extends I3>, Buffer> encode,
                                                           Function<Buffer, O> decode) {
            return new Caller4<>(encode, decode, address, bus);
        }

        record Caller4<I0,I1,I2,I3, O>(
                Function<Tuple4<? extends I0,? extends I1,? extends I2,? extends I3>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple4<? extends I0,? extends I1,? extends I2,? extends I3>, O>, Fn4<I0,I1,I2,I3, O> {
            @Override
            public Future<O> apply(Tuple4<? extends I0,? extends I1,? extends I2,? extends I3> v) {
                return caller.super.apply(v);
            }
            @Override
            public Future<O> apply(I0 i0,I1 i1,I2 i2,I3 i3) {
                return apply(Tuple.tuple(i0,i1,i2,i3));
            }
        }
    }

    interface Fn5<I0,I1,I2,I3,I4, O> extends Function5<I0,I1,I2,I3,I4, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple5<I0,I1,I2,I3,I4>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee5<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee5<I0,I1,I2,I3,I4, O>(
                Function<Buffer, Tuple5<I0,I1,I2,I3,I4>> decode,
                Function<O, Buffer> encode,
                Fn5<I0,I1,I2,I3,I4, O> fn
        ) implements Callable.Callee.callee<Tuple5<I0,I1,I2,I3,I4>, O> {
            @Override
            public Future<O> apply(Tuple5<I0,I1,I2,I3,I4> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1,I2,I3,I4, O> Fn5<I0,I1,I2,I3,I4, O> caller(EventBus bus, String address,
                                                                 Function<Tuple5<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4>, Buffer> encode,
                                                                 Function<Buffer, O> decode) {
            return new Caller5<>(encode, decode, address, bus);
        }

        record Caller5<I0,I1,I2,I3,I4, O>(
                Function<Tuple5<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple5<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4>, O>, Fn5<I0,I1,I2,I3,I4, O> {
            @Override
            public Future<O> apply(Tuple5<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4> v) {
                return caller.super.apply(v);
            }
            @Override
            public Future<O> apply(I0 i0,I1 i1,I2 i2,I3 i3,I4 i4) {
                return apply(Tuple.tuple(i0,i1,i2,i3,i4));
            }
        }
    }

    interface Fn6<I0,I1,I2,I3,I4,I5, O> extends Function6<I0,I1,I2,I3,I4,I5, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple6<I0,I1,I2,I3,I4,I5>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee6<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee6<I0,I1,I2,I3,I4,I5, O>(
                Function<Buffer, Tuple6<I0,I1,I2,I3,I4,I5>> decode,
                Function<O, Buffer> encode,
                Fn6<I0,I1,I2,I3,I4,I5, O> fn
        ) implements Callable.Callee.callee<Tuple6<I0,I1,I2,I3,I4,I5>, O> {
            @Override
            public Future<O> apply(Tuple6<I0,I1,I2,I3,I4,I5> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1,I2,I3,I4,I5, O> Fn6<I0,I1,I2,I3,I4,I5, O> caller(EventBus bus, String address,
                                                                       Function<Tuple6<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5>, Buffer> encode,
                                                                       Function<Buffer, O> decode) {
            return new Caller6<>(encode, decode, address, bus);
        }

        record Caller6<I0,I1,I2,I3,I4,I5, O>(
                Function<Tuple6<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple6<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5>, O>, Fn6<I0,I1,I2,I3,I4,I5, O> {
            @Override
            public Future<O> apply(Tuple6<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5> v) {
                return caller.super.apply(v);
            }
            @Override
            public Future<O> apply(I0 i0,I1 i1,I2 i2,I3 i3,I4 i4,I5 i5) {
                return apply(Tuple.tuple(i0,i1,i2,i3,i4,i5));
            }
        }
    }

    interface Fn7<I0,I1,I2,I3,I4,I5,I6, O> extends Function7<I0,I1,I2,I3,I4,I5,I6, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple7<I0,I1,I2,I3,I4,I5,I6>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee7<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee7<I0,I1,I2,I3,I4,I5,I6, O>(
                Function<Buffer, Tuple7<I0,I1,I2,I3,I4,I5,I6>> decode,
                Function<O, Buffer> encode,
                Fn7<I0,I1,I2,I3,I4,I5,I6, O> fn
        ) implements Callable.Callee.callee<Tuple7<I0,I1,I2,I3,I4,I5,I6>, O> {
            @Override
            public Future<O> apply(Tuple7<I0,I1,I2,I3,I4,I5,I6> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1,I2,I3,I4,I5,I6, O> Fn7<I0,I1,I2,I3,I4,I5,I6, O> caller(EventBus bus, String address,
                                                                             Function<Tuple7<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6>, Buffer> encode,
                                                                             Function<Buffer, O> decode) {
            return new Caller7<>(encode, decode, address, bus);
        }

        record Caller7<I0,I1,I2,I3,I4,I5,I6, O>(
                Function<Tuple7<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple7<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6>, O>, Fn7<I0,I1,I2,I3,I4,I5,I6, O> {
            @Override
            public Future<O> apply(Tuple7<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6> v) {
                return caller.super.apply(v);
            }
            @Override
            public Future<O> apply(I0 i0,I1 i1,I2 i2,I3 i3,I4 i4,I5 i5,I6 i6) {
                return apply(Tuple.tuple(i0,i1,i2,i3,i4,i5,i6));
            }
        }
    }

    interface Fn8<I0,I1,I2,I3,I4,I5,I6,I7, O> extends Function8<I0,I1,I2,I3,I4,I5,I6,I7, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple8<I0,I1,I2,I3,I4,I5,I6,I7>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee8<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee8<I0,I1,I2,I3,I4,I5,I6,I7, O>(
                Function<Buffer, Tuple8<I0,I1,I2,I3,I4,I5,I6,I7>> decode,
                Function<O, Buffer> encode,
                Fn8<I0,I1,I2,I3,I4,I5,I6,I7, O> fn
        ) implements Callable.Callee.callee<Tuple8<I0,I1,I2,I3,I4,I5,I6,I7>, O> {
            @Override
            public Future<O> apply(Tuple8<I0,I1,I2,I3,I4,I5,I6,I7> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1,I2,I3,I4,I5,I6,I7, O> Fn8<I0,I1,I2,I3,I4,I5,I6,I7, O> caller(EventBus bus, String address,
                                                                                   Function<Tuple8<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7>, Buffer> encode,
                                                                                   Function<Buffer, O> decode) {
            return new Caller8<>(encode, decode, address, bus);
        }

        record Caller8<I0,I1,I2,I3,I4,I5,I6,I7, O>(
                Function<Tuple8<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple8<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7>, O>, Fn8<I0,I1,I2,I3,I4,I5,I6,I7, O> {
            @Override
            public Future<O> apply(Tuple8<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7> v) {
                return caller.super.apply(v);
            }
            @Override
            public Future<O> apply(I0 i0,I1 i1,I2 i2,I3 i3,I4 i4,I5 i5,I6 i6,I7 i7) {
                return apply(Tuple.tuple(i0,i1,i2,i3,i4,i5,i6,i7));
            }
        }
    }

    interface Fn9<I0,I1,I2,I3,I4,I5,I6,I7,I8, O> extends Function9<I0,I1,I2,I3,I4,I5,I6,I7,I8, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple9<I0,I1,I2,I3,I4,I5,I6,I7,I8>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee9<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee9<I0,I1,I2,I3,I4,I5,I6,I7,I8, O>(
                Function<Buffer, Tuple9<I0,I1,I2,I3,I4,I5,I6,I7,I8>> decode,
                Function<O, Buffer> encode,
                Fn9<I0,I1,I2,I3,I4,I5,I6,I7,I8, O> fn
        ) implements Callable.Callee.callee<Tuple9<I0,I1,I2,I3,I4,I5,I6,I7,I8>, O> {
            @Override
            public Future<O> apply(Tuple9<I0,I1,I2,I3,I4,I5,I6,I7,I8> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1,I2,I3,I4,I5,I6,I7,I8, O> Fn9<I0,I1,I2,I3,I4,I5,I6,I7,I8, O> caller(EventBus bus, String address,
                                                                                         Function<Tuple9<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8>, Buffer> encode,
                                                                                         Function<Buffer, O> decode) {
            return new Caller9<>(encode, decode, address, bus);
        }

        record Caller9<I0,I1,I2,I3,I4,I5,I6,I7,I8, O>(
                Function<Tuple9<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple9<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8>, O>, Fn9<I0,I1,I2,I3,I4,I5,I6,I7,I8, O> {
            @Override
            public Future<O> apply(Tuple9<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8> v) {
                return caller.super.apply(v);
            }
            @Override
            public Future<O> apply(I0 i0,I1 i1,I2 i2,I3 i3,I4 i4,I5 i5,I6 i6,I7 i7,I8 i8) {
                return apply(Tuple.tuple(i0,i1,i2,i3,i4,i5,i6,i7,i8));
            }
        }
    }

    interface Fn10<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9, O> extends Function10<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple10<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee10<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee10<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9, O>(
                Function<Buffer, Tuple10<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9>> decode,
                Function<O, Buffer> encode,
                Fn10<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9, O> fn
        ) implements Callable.Callee.callee<Tuple10<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9>, O> {
            @Override
            public Future<O> apply(Tuple10<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1,I2,I3,I4,I5,I6,I7,I8,I9, O> Fn10<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9, O> caller(EventBus bus, String address,
                                                                                                Function<Tuple10<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9>, Buffer> encode,
                                                                                                Function<Buffer, O> decode) {
            return new Caller10<>(encode, decode, address, bus);
        }

        record Caller10<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9, O>(
                Function<Tuple10<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple10<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9>, O>, Fn10<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9, O> {
            @Override
            public Future<O> apply(Tuple10<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9> v) {
                return caller.super.apply(v);
            }
            @Override
            public Future<O> apply(I0 i0,I1 i1,I2 i2,I3 i3,I4 i4,I5 i5,I6 i6,I7 i7,I8 i8,I9 i9) {
                return apply(Tuple.tuple(i0,i1,i2,i3,i4,i5,i6,i7,i8,i9));
            }
        }
    }

    interface Fn11<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10, O> extends Function11<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple11<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee11<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee11<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10, O>(
                Function<Buffer, Tuple11<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10>> decode,
                Function<O, Buffer> encode,
                Fn11<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10, O> fn
        ) implements Callable.Callee.callee<Tuple11<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10>, O> {
            @Override
            public Future<O> apply(Tuple11<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10, O> Fn11<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10, O> caller(EventBus bus, String address,
                                                                                                        Function<Tuple11<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10>, Buffer> encode,
                                                                                                        Function<Buffer, O> decode) {
            return new Caller11<>(encode, decode, address, bus);
        }

        record Caller11<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10, O>(
                Function<Tuple11<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple11<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10>, O>, Fn11<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10, O> {
            @Override
            public Future<O> apply(Tuple11<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10> v) {
                return caller.super.apply(v);
            }
            @Override
            public Future<O> apply(I0 i0,I1 i1,I2 i2,I3 i3,I4 i4,I5 i5,I6 i6,I7 i7,I8 i8,I9 i9,I10 i10) {
                return apply(Tuple.tuple(i0,i1,i2,i3,i4,i5,i6,i7,i8,i9,i10));
            }
        }
    }

    interface Fn12<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11, O> extends Function12<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple12<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee12<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee12<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11, O>(
                Function<Buffer, Tuple12<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11>> decode,
                Function<O, Buffer> encode,
                Fn12<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11, O> fn
        ) implements Callable.Callee.callee<Tuple12<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11>, O> {
            @Override
            public Future<O> apply(Tuple12<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11, O> Fn12<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11, O> caller(EventBus bus, String address,
                                                                                                                Function<Tuple12<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11>, Buffer> encode,
                                                                                                                Function<Buffer, O> decode) {
            return new Caller12<>(encode, decode, address, bus);
        }

        record Caller12<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11, O>(
                Function<Tuple12<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple12<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11>, O>, Fn12<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11, O> {
            @Override
            public Future<O> apply(Tuple12<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11> v) {
                return caller.super.apply(v);
            }
            @Override
            public Future<O> apply(I0 i0,I1 i1,I2 i2,I3 i3,I4 i4,I5 i5,I6 i6,I7 i7,I8 i8,I9 i9,I10 i10,I11 i11) {
                return apply(Tuple.tuple(i0,i1,i2,i3,i4,i5,i6,i7,i8,i9,i10,i11));
            }
        }
    }

    interface Fn13<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12, O> extends Function13<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple13<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee13<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee13<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12, O>(
                Function<Buffer, Tuple13<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12>> decode,
                Function<O, Buffer> encode,
                Fn13<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12, O> fn
        ) implements Callable.Callee.callee<Tuple13<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12>, O> {
            @Override
            public Future<O> apply(Tuple13<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12, O> Fn13<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12, O> caller(EventBus bus, String address,
                                                                                                                        Function<Tuple13<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12>, Buffer> encode,
                                                                                                                        Function<Buffer, O> decode) {
            return new Caller13<>(encode, decode, address, bus);
        }

        record Caller13<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12, O>(
                Function<Tuple13<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple13<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12>, O>, Fn13<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12, O> {
            @Override
            public Future<O> apply(Tuple13<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12> v) {
                return caller.super.apply(v);
            }
            @Override
            public Future<O> apply(I0 i0,I1 i1,I2 i2,I3 i3,I4 i4,I5 i5,I6 i6,I7 i7,I8 i8,I9 i9,I10 i10,I11 i11,I12 i12) {
                return apply(Tuple.tuple(i0,i1,i2,i3,i4,i5,i6,i7,i8,i9,i10,i11,i12));
            }
        }
    }

    interface Fn14<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13, O> extends Function14<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple14<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee14<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee14<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13, O>(
                Function<Buffer, Tuple14<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13>> decode,
                Function<O, Buffer> encode,
                Fn14<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13, O> fn
        ) implements Callable.Callee.callee<Tuple14<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13>, O> {
            @Override
            public Future<O> apply(Tuple14<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13, O> Fn14<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13, O> caller(EventBus bus, String address,
                                                                                                                                Function<Tuple14<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12,? extends I13>, Buffer> encode,
                                                                                                                                Function<Buffer, O> decode) {
            return new Caller14<>(encode, decode, address, bus);
        }

        record Caller14<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13, O>(
                Function<Tuple14<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12,? extends I13>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple14<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12,? extends I13>, O>, Fn14<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13, O> {
            @Override
            public Future<O> apply(Tuple14<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12,? extends I13> v) {
                return caller.super.apply(v);
            }
            @Override
            public Future<O> apply(I0 i0,I1 i1,I2 i2,I3 i3,I4 i4,I5 i5,I6 i6,I7 i7,I8 i8,I9 i9,I10 i10,I11 i11,I12 i12,I13 i13) {
                return apply(Tuple.tuple(i0,i1,i2,i3,i4,i5,i6,i7,i8,i9,i10,i11,i12,i13));
            }
        }
    }

    interface Fn15<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14, O> extends Function15<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple15<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee15<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee15<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14, O>(
                Function<Buffer, Tuple15<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14>> decode,
                Function<O, Buffer> encode,
                Fn15<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14, O> fn
        ) implements Callable.Callee.callee<Tuple15<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14>, O> {
            @Override
            public Future<O> apply(Tuple15<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14, O> Fn15<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14, O> caller(EventBus bus, String address,
                                                                                                                                        Function<Tuple15<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12,? extends I13,? extends I14>, Buffer> encode,
                                                                                                                                        Function<Buffer, O> decode) {
            return new Caller15<>(encode, decode, address, bus);
        }

        record Caller15<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14, O>(
                Function<Tuple15<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12,? extends I13,? extends I14>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple15<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12,? extends I13,? extends I14>, O>, Fn15<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14, O> {
            @Override
            public Future<O> apply(Tuple15<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12,? extends I13,? extends I14> v) {
                return caller.super.apply(v);
            }
            @Override
            public Future<O> apply(I0 i0,I1 i1,I2 i2,I3 i3,I4 i4,I5 i5,I6 i6,I7 i7,I8 i8,I9 i9,I10 i10,I11 i11,I12 i12,I13 i13,I14 i14) {
                return apply(Tuple.tuple(i0,i1,i2,i3,i4,i5,i6,i7,i8,i9,i10,i11,i12,i13,i14));
            }
        }
    }

    interface Fn16<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14,I15, O> extends Function16<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14,I15, Future<O>> {

        default Callee.Handler callee(
                EventBus bus,
                Function<Buffer, Tuple16<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14,I15>> decode,
                Function<O, Buffer> encode) {
            var ce = new Callee16<>(decode, encode, this);
            var addr = ce.getClass().getName() + "_" + Math.random();
            return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
        }
        record Callee16<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14,I15, O>(
                Function<Buffer, Tuple16<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14,I15>> decode,
                Function<O, Buffer> encode,
                Fn16<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14,I15, O> fn
        ) implements Callable.Callee.callee<Tuple16<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14,I15>, O> {
            @Override
            public Future<O> apply(Tuple16<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14,I15> v) {
                return fn.apply(v);
            }
        }
        static <I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14,I15, O> Fn16<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14,I15, O> caller(EventBus bus, String address,
                                                                                                                                                Function<Tuple16<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12,? extends I13,? extends I14,? extends I15>, Buffer> encode,
                                                                                                                                                Function<Buffer, O> decode) {
            return new Caller16<>(encode, decode, address, bus);
        }

        record Caller16<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14,I15, O>(
                Function<Tuple16<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12,? extends I13,? extends I14,? extends I15>, Buffer> encode,
                Function<Buffer, O> decode,
                String address,
                EventBus bus
        ) implements Callable.Caller.caller<Tuple16<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12,? extends I13,? extends I14,? extends I15>, O>, Fn16<I0,I1,I2,I3,I4,I5,I6,I7,I8,I9,I10,I11,I12,I13,I14,I15, O> {
            @Override
            public Future<O> apply(Tuple16<? extends I0,? extends I1,? extends I2,? extends I3,? extends I4,? extends I5,? extends I6,? extends I7,? extends I8,? extends I9,? extends I10,? extends I11,? extends I12,? extends I13,? extends I14,? extends I15> v) {
                return caller.super.apply(v);
            }
            @Override
            public Future<O> apply(I0 i0,I1 i1,I2 i2,I3 i3,I4 i4,I5 i5,I6 i6,I7 i7,I8 i8,I9 i9,I10 i10,I11 i11,I12 i12,I13 i13,I14 i14,I15 i15) {
                return apply(Tuple.tuple(i0,i1,i2,i3,i4,i5,i6,i7,i8,i9,i10,i11,i12,i13,i14,i15));
            }
        }
    }


//    public static void main(String[] args) {
//        Toolkit.getDefaultToolkit()
//               .getSystemClipboard()
//               .setContents(new StringSelection(Seq
//                                                        .range(2, 17)
//                                                        .map(n -> """
//                                                               interface Fn%1$d<%2$s, O> extends Function%1$d<%2$s, Future<O>> {
//
//                                                                   default Callee.Handler callee(
//                                                                           EventBus bus,
//                                                                           Function<Buffer, %3$s> decode,
//                                                                           Function<O, Buffer> encode) {
//                                                                       var ce = new Callee%1$d<>(decode, encode, this);
//                                                                       var addr = ce.getClass().getName() + "_" + Math.random();
//                                                                       return new Callee.Handler(addr, bus.consumer(addr, ce::handle)::unregister);
//                                                                   }
//                                                                   record Callee%1$d<%2$s, O>(
//                                                                           Function<Buffer, %3$s> decode,
//                                                                           Function<O, Buffer> encode,
//                                                                           Fn%1$d<%2$s, O> fn
//                                                                   ) implements Callable.Callee.callee<%3$s, O> {
//                                                                       @Override
//                                                                       public Future<O> apply(%3$s v) {
//                                                                           return fn.apply(v);
//                                                                       }
//                                                                   }
//                                                                   static <%2$s, O> Fn%1$d<%2$s, O> caller(EventBus bus, String address,
//                                                                                                  Function<%4$s, Buffer> encode,
//                                                                                                  Function<Buffer, O> decode) {
//                                                                       return new Caller%1$d<>(encode, decode, address, bus);
//                                                                   }
//
//                                                                   record Caller%1$d<%2$s, O>(
//                                                                           Function<%4$s, Buffer> encode,
//                                                                           Function<Buffer, O> decode,
//                                                                           String address,
//                                                                           EventBus bus
//                                                                   ) implements Callable.Caller.caller<%4$s, O>, Fn%1$d<%2$s, O> {
//                                                                       @Override
//                                                                       public Future<O> apply(%4$s v) {
//                                                                           return caller.super.apply(v);
//                                                                       }
//                                                                       @Override
//                                                                       public Future<O> apply(%5$s) {
//                                                                           return apply(Tuple.tuple(%6$s));
//                                                                       }
//                                                                   }
//                                                               }
//                                                               """.formatted(
//                                                                n
//                                                                , Seq.range(0, n).map("I%d"::formatted).toString(",")
//                                                                , Seq.range(0, n).map("I%1$d"::formatted)
//                                                                     .toString(",", "Tuple" + n + "<", ">")
//
//                                                                , Seq.range(0, n).map("? extends I%1$d"::formatted)
//                                                                     .toString(",", "Tuple" + n + "<", ">")
//                                                                , Seq.range(0, n).map("I%1$d i%1$d"::formatted).toString(",")
//                                                                , Seq.range(0, n).map("i%d"::formatted).toString(",")
//                                                            ))
//                                                        .toString("\n")), null);
//    }
}
