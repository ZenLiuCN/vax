package vax.query;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.jooq.lambda.tuple.*;
import vax.query.Expr.JoinMode;

import java.util.List;
import java.util.Map;
import java.util.function.Function;


/**
 * @author Zen.Liu
 * @since 2025-03-07
 */

public interface Statement {
    interface Deferred {
        interface Put<T> extends Deferred {
            Future<Void> putRaw(JsonObject one, JsonObject... more);

            @SuppressWarnings("unchecked")
            Future<Void> put(T one, T... more);

        }

        interface One<E> extends Deferred {
            Future<E> one(Map<String, Object> parameters);
        }

        interface Any<E> extends Deferred {
            Future<List<E>> one(Map<String, Object> parameters);

        }

        interface Count extends Deferred {
            Future<Integer> count(Map<String, Object> parameters);
        }

        interface Delete extends Deferred {
            Future<Integer> delete(Map<String, Object> parameters);
        }

        interface Into extends Deferred {
            Future<Integer> into(Map<String, Object> parameters);
        }

        interface Modify extends Deferred {
            Future<Integer> modify(Map<String, Object> parameters);
        }
    }

    interface Singular<E, S extends Singular<E, S>> extends Store<E, Singular<E, S>> {
        Future<Void> putRaw(JsonObject value, JsonObject... more);

        @SuppressWarnings("unchecked")
        Future<Void> put(E value, E... more);

        Deferred.Put<E> deferredPut();

    }

    interface Store<E, S extends Store<E, ?>> extends Stage<E, Store<E, S>> {

        default <V0, S0 extends Store<V0, ?>> UStore2<E, V0, S, S0> join(
                S0 store,
                Function<UStore2<E, V0, S, S0>, Value.Bool> cond) {
            return join(JoinMode.INNER, store, cond);
        }

        <E0, S0 extends Store<E0, ?>> UStore2<E, E0, S, S0> join(
                JoinMode mode,
                S0 store,
                Function<UStore2<E, E0, S, S0>, Value.Bool> cond);
    }

    interface Stage<E, S extends Stage<E, S>> extends Filter<E, S> {}

    //region UStore
    interface UStore2<E0, E1,
            S0, S1> extends Stage<Tuple2<E0, E1>,
            UStore2<E0, E1,
                    S0, S1>> {
        S0 s0();

        S1 s1();

        default <E2, S2 extends Store<E2, ?>> UStore3<E0, E1, E2, S0, S1, S2> andJoin(
                S2 store
                , Function<UStore3<E0, E1, E2, S0, S1, S2>, Value.Bool> cond) {
            return andJoin(JoinMode.INNER, store, cond);
        }

        <E2, S2 extends Store<E2, ?>> UStore3<E0, E1, E2, S0, S1, S2> andJoin(
                JoinMode mode
                , S2 store
                , Function<UStore3<E0, E1, E2, S0, S1, S2>, Value.Bool> cond);

    }

    interface UStore3<E0, E1, E2,
            S0, S1, S2> extends Stage<Tuple3<E0, E1, E2>,
            UStore3<E0, E1, E2,
                    S0, S1, S2>> {
        S0 s0();

        S1 s1();

        S2 s2();

        default <E3, S3 extends Store<E3, ?>> UStore4<E0, E1, E2, E3, S0, S1, S2, S3> andJoin(
                S3 store
                , Function<UStore4<E0, E1, E2, E3, S0, S1, S2, S3>, Value.Bool> cond) {
            return andJoin(JoinMode.INNER, store, cond);
        }

        <E3, S3 extends Store<E3, ?>> UStore4<E0, E1, E2, E3, S0, S1, S2, S3> andJoin(
                JoinMode mode
                , S3 store
                , Function<UStore4<E0, E1, E2, E3, S0, S1, S2, S3>, Value.Bool> cond);

    }

    interface UStore4<E0, E1, E2, E3,
            S0, S1, S2, S3> extends Stage<Tuple4<E0, E1, E2, E3>,
            UStore4<E0, E1, E2, E3,
                    S0, S1, S2, S3>> {
        S0 s0();

        S1 s1();

        S2 s2();

        S3 s3();

        default <E4, S4 extends Store<E4, ?>> UStore5<E0, E1, E2, E3, E4, S0, S1, S2, S3, S4> andJoin(
                S4 store
                , Function<UStore5<E0, E1, E2, E3, E4, S0, S1, S2, S3, S4>, Value.Bool> cond) {
            return andJoin(JoinMode.INNER, store, cond);
        }

        <E4, S4 extends Store<E4, ?>> UStore5<E0, E1, E2, E3, E4, S0, S1, S2, S3, S4> andJoin(
                JoinMode mode
                , S4 store
                , Function<UStore5<E0, E1, E2, E3, E4, S0, S1, S2, S3, S4>, Value.Bool> cond);

    }

    interface UStore5<E0, E1, E2, E3, E4,
            S0, S1, S2, S3, S4> extends Stage<Tuple5<E0, E1, E2, E3, E4>,
            UStore5<E0, E1, E2, E3, E4,
                    S0, S1, S2, S3, S4>> {
        S0 s0();

        S1 s1();

        S2 s2();

        S3 s3();

        S4 s4();

        default <E5, S5 extends Store<E5, ?>> UStore6<E0, E1, E2, E3, E4, E5, S0, S1, S2, S3, S4, S5> andJoin(
                S5 store
                , Function<UStore6<E0, E1, E2, E3, E4, E5, S0, S1, S2, S3, S4, S5>, Value.Bool> cond) {
            return andJoin(JoinMode.INNER, store, cond);
        }

        <E5, S5 extends Store<E5, ?>> UStore6<E0, E1, E2, E3, E4, E5, S0, S1, S2, S3, S4, S5> andJoin(
                JoinMode mode
                , S5 store
                , Function<UStore6<E0, E1, E2, E3, E4, E5, S0, S1, S2, S3, S4, S5>, Value.Bool> cond);

    }

    interface UStore6<E0, E1, E2, E3, E4, E5,
            S0, S1, S2, S3, S4, S5> extends Stage<Tuple6<E0, E1, E2, E3, E4, E5>,
            UStore6<E0, E1, E2, E3, E4, E5,
                    S0, S1, S2, S3, S4, S5>> {
        S0 s0();

        S1 s1();

        S2 s2();

        S3 s3();

        S4 s4();

        S5 s5();

        default <E6, S6 extends Store<E6, ?>> UStore7<E0, E1, E2, E3, E4, E5, E6, S0, S1, S2, S3, S4, S5, S6> andJoin(
                S6 store
                , Function<UStore7<E0, E1, E2, E3, E4, E5, E6, S0, S1, S2, S3, S4, S5, S6>, Value.Bool> cond) {
            return andJoin(JoinMode.INNER, store, cond);
        }

        <E6, S6 extends Store<E6, ?>> UStore7<E0, E1, E2, E3, E4, E5, E6, S0, S1, S2, S3, S4, S5, S6> andJoin(
                JoinMode mode
                , S6 store
                , Function<UStore7<E0, E1, E2, E3, E4, E5, E6, S0, S1, S2, S3, S4, S5, S6>, Value.Bool> cond);

    }

    interface UStore7<E0, E1, E2, E3, E4, E5, E6,
            S0, S1, S2, S3, S4, S5, S6> extends Stage<Tuple7<E0, E1, E2, E3, E4, E5, E6>,
            UStore7<E0, E1, E2, E3, E4, E5, E6,
                    S0, S1, S2, S3, S4, S5, S6>> {
        S0 s0();

        S1 s1();

        S2 s2();

        S3 s3();

        S4 s4();

        S5 s5();

        S6 s6();

        default <E7, S7 extends Store<E7, ?>> UStore8<E0, E1, E2, E3, E4, E5, E6, E7, S0, S1, S2, S3, S4, S5, S6, S7> andJoin(
                S7 store
                , Function<UStore8<E0, E1, E2, E3, E4, E5, E6, E7, S0, S1, S2, S3, S4, S5, S6, S7>, Value.Bool> cond) {
            return andJoin(JoinMode.INNER, store, cond);
        }

        <E7, S7 extends Store<E7, ?>> UStore8<E0, E1, E2, E3, E4, E5, E6, E7, S0, S1, S2, S3, S4, S5, S6, S7> andJoin(
                JoinMode mode
                , S7 store
                , Function<UStore8<E0, E1, E2, E3, E4, E5, E6, E7, S0, S1, S2, S3, S4, S5, S6, S7>, Value.Bool> cond);

    }

    interface UStore8<E0, E1, E2, E3, E4, E5, E6, E7,
            S0, S1, S2, S3, S4, S5, S6, S7> extends Stage<Tuple8<E0, E1, E2, E3, E4, E5, E6, E7>,
            UStore8<E0, E1, E2, E3, E4, E5, E6, E7,
                    S0, S1, S2, S3, S4, S5, S6, S7>> {
        S0 s0();

        S1 s1();

        S2 s2();

        S3 s3();

        S4 s4();

        S5 s5();

        S6 s6();

        S7 s7();

        default <E8, S8 extends Store<E8, ?>> UStore9<E0, E1, E2, E3, E4, E5, E6, E7, E8, S0, S1, S2, S3, S4, S5, S6, S7, S8> andJoin(
                S8 store
                ,
                Function<UStore9<E0, E1, E2, E3, E4, E5, E6, E7, E8, S0, S1, S2, S3, S4, S5, S6, S7, S8>, Value.Bool> cond) {
            return andJoin(JoinMode.INNER, store, cond);
        }

        <E8, S8 extends Store<E8, ?>> UStore9<E0, E1, E2, E3, E4, E5, E6, E7, E8, S0, S1, S2, S3, S4, S5, S6, S7, S8> andJoin(
                JoinMode mode
                , S8 store
                ,
                Function<UStore9<E0, E1, E2, E3, E4, E5, E6, E7, E8, S0, S1, S2, S3, S4, S5, S6, S7, S8>, Value.Bool> cond);

    }

    interface UStore9<E0, E1, E2, E3, E4, E5, E6, E7, E8,
            S0, S1, S2, S3, S4, S5, S6, S7, S8> extends Stage<Tuple9<E0, E1, E2, E3, E4, E5, E6, E7, E8>,
            UStore9<E0, E1, E2, E3, E4, E5, E6, E7, E8,
                    S0, S1, S2, S3, S4, S5, S6, S7, S8>> {
        S0 s0();

        S1 s1();

        S2 s2();

        S3 s3();

        S4 s4();

        S5 s5();

        S6 s6();

        S7 s7();

        S8 s8();

        default <E9, S9 extends Store<E9, ?>> UStore10<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9> andJoin(
                S9 store
                ,
                Function<UStore10<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9>, Value.Bool> cond) {
            return andJoin(JoinMode.INNER, store, cond);
        }

        <E9, S9 extends Store<E9, ?>> UStore10<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9> andJoin(
                JoinMode mode
                , S9 store
                ,
                Function<UStore10<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9>, Value.Bool> cond);

    }

    interface UStore10<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9,
            S0, S1, S2, S3, S4, S5, S6, S7, S8, S9> extends Stage<Tuple10<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9>,
            UStore10<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9,
                    S0, S1, S2, S3, S4, S5, S6, S7, S8, S9>> {
        S0 s0();

        S1 s1();

        S2 s2();

        S3 s3();

        S4 s4();

        S5 s5();

        S6 s6();

        S7 s7();

        S8 s8();

        S9 s9();

        default <E10, S10 extends Store<E10, ?>> UStore11<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10> andJoin(
                S10 store
                ,
                Function<UStore11<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10>, Value.Bool> cond) {
            return andJoin(JoinMode.INNER, store, cond);
        }

        <E10, S10 extends Store<E10, ?>> UStore11<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10> andJoin(
                JoinMode mode
                , S10 store
                ,
                Function<UStore11<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10>, Value.Bool> cond);

    }

    interface UStore11<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10,
            S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10> extends
                                                         Stage<Tuple11<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10>,
                                                                 UStore11<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10,
                                                                         S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10>> {
        S0 s0();

        S1 s1();

        S2 s2();

        S3 s3();

        S4 s4();

        S5 s5();

        S6 s6();

        S7 s7();

        S8 s8();

        S9 s9();

        S10 s10();

        default <E11, S11 extends Store<E11, ?>> UStore12<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11> andJoin(
                S11 store
                ,
                Function<UStore12<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11>, Value.Bool> cond) {
            return andJoin(JoinMode.INNER, store, cond);
        }

        <E11, S11 extends Store<E11, ?>> UStore12<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11> andJoin(
                JoinMode mode
                , S11 store
                ,
                Function<UStore12<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11>, Value.Bool> cond);

    }

    interface UStore12<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11,
            S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11> extends
                                                              Stage<Tuple12<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11>,
                                                                      UStore12<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11,
                                                                              S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11>> {
        S0 s0();

        S1 s1();

        S2 s2();

        S3 s3();

        S4 s4();

        S5 s5();

        S6 s6();

        S7 s7();

        S8 s8();

        S9 s9();

        S10 s10();

        S11 s11();

        default <E12, S12 extends Store<E12, ?>> UStore13<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12> andJoin(
                S12 store
                ,
                Function<UStore13<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12>, Value.Bool> cond) {
            return andJoin(JoinMode.INNER, store, cond);
        }

        <E12, S12 extends Store<E12, ?>> UStore13<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12> andJoin(
                JoinMode mode
                , S12 store
                ,
                Function<UStore13<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12>, Value.Bool> cond);

    }

    interface UStore13<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12,
            S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12> extends
                                                                   Stage<Tuple13<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12>,
                                                                           UStore13<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12,
                                                                                   S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12>> {
        S0 s0();

        S1 s1();

        S2 s2();

        S3 s3();

        S4 s4();

        S5 s5();

        S6 s6();

        S7 s7();

        S8 s8();

        S9 s9();

        S10 s10();

        S11 s11();

        S12 s12();

        default <E13, S13 extends Store<E13, ?>> UStore14<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13> andJoin(
                S13 store
                ,
                Function<UStore14<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13>, Value.Bool> cond) {
            return andJoin(JoinMode.INNER, store, cond);
        }

        <E13, S13 extends Store<E13, ?>> UStore14<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13> andJoin(
                JoinMode mode
                , S13 store
                ,
                Function<UStore14<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13>, Value.Bool> cond);

    }

    interface UStore14<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13,
            S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13> extends
                                                                        Stage<Tuple14<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13>,
                                                                                UStore14<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13,
                                                                                        S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13>> {
        S0 s0();

        S1 s1();

        S2 s2();

        S3 s3();

        S4 s4();

        S5 s5();

        S6 s6();

        S7 s7();

        S8 s8();

        S9 s9();

        S10 s10();

        S11 s11();

        S12 s12();

        S13 s13();

        default <E14, S14 extends Store<E14, ?>> UStore15<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14> andJoin(
                S14 store
                ,
                Function<UStore15<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14>, Value.Bool> cond) {
            return andJoin(JoinMode.INNER, store, cond);
        }

        <E14, S14 extends Store<E14, ?>> UStore15<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14> andJoin(
                JoinMode mode
                , S14 store
                ,
                Function<UStore15<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14>, Value.Bool> cond);

    }

    interface UStore15<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14,
            S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14> extends
                                                                             Stage<Tuple15<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14>,
                                                                                     UStore15<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14,
                                                                                             S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14>> {
        S0 s0();

        S1 s1();

        S2 s2();

        S3 s3();

        S4 s4();

        S5 s5();

        S6 s6();

        S7 s7();

        S8 s8();

        S9 s9();

        S10 s10();

        S11 s11();

        S12 s12();

        S13 s13();

        S14 s14();

        default <E15, S15 extends Store<E15, ?>> UStore16<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14, S15> andJoin(
                S15 store
                ,
                Function<UStore16<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14, S15>, Value.Bool> cond) {
            return andJoin(JoinMode.INNER, store, cond);
        }

        <E15, S15 extends Store<E15, ?>> UStore16<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14, S15> andJoin(
                JoinMode mode
                , S15 store
                ,
                Function<UStore16<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15, S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14, S15>, Value.Bool> cond);

    }

    interface UStore16<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15,
            S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14, S15>
            extends
            Statement.Store<
                    Tuple16<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15>,
                    UStore16<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15,
                            S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14, S15>> {
        S0 s0();

        S1 s1();

        S2 s2();

        S3 s3();

        S4 s4();

        S5 s5();

        S6 s6();

        S7 s7();

        S8 s8();

        S9 s9();

        S10 s10();

        S11 s11();

        S12 s12();

        S13 s13();

        S14 s14();

        S15 s15();
    }


//endregion

    interface Filter<E, S extends Stage<E, ?>> {
        Assigner<E, ?> filter(Function<S, Value.Bool> filter);
    }

    interface Assigner<E, S extends Stage<E, ?>> extends Sort<E, S> {
        Future<Void> into(Function<S, List<Expr.Assign>> fn);

        Deferred.Into deferredInto(Function<S, List<Expr.Assign>> fn);

        Future<Void> modify(Function<S, List<Expr.Assign>> fn);

        Deferred.Modify deferredModify(Function<S, List<Expr.Assign>> fn);
    }

    interface Sort<E, S extends Stage<E, ?>> extends Query<E, S> {
        Skip<E, ?> sort(Function<S, List<Expr.Sorter>> sort);
    }

    interface Skip<E, S extends Stage<E, ?>> extends Query<E, S> {
        Limit<E, ?> skip(int n);
    }

    interface Limit<E, S extends Stage<E, ?>> extends Query<E, S> {
        Query<E, ?> limit(int n);
    }

    interface Query<E, S extends Stage<E, ?>> {
        /**
         * as virtual store
         */
        Singular<E,?> as(String alias);

        Future<Integer> delete();

        Deferred.Delete deferredDelete();

        Future<E> one();

        Deferred.One<E> deferredOne();

        Future<List<E>> any();

        Deferred.Any<E> deferredAny();

        Future<Integer> count();

        Deferred.Count deferredCount();

        Future<Integer> count(Function<S, Model.Field<?>> pick);

        Deferred.Count deferredCount(Function<S, Model.Field<?>> pick);

        <V> Future<V> one(Function<S, Model.Field<V>> pick);

        <V> Deferred.One<V> deferredOne(Function<S, Model.Field<V>> pick);

        <V> Future<List<V>> any(Function<S, Model.Field<V>> pick);

        <V> Deferred.Any<List<V>> deferredAny(Function<S, Model.Field<V>> pick);

        //region Picks
        <E0, E1> Future<Tuple2<E0, E1>> one2(Function<S, Tuple2<Model.Field<E0>, Model.Field<E1>>> pick);

        <E0, E1> Deferred.One<Tuple2<E0, E1>> deferredOne2(Function<S, Tuple2<Model.Field<E0>, Model.Field<E1>>> pick);

        <E0, E1> Future<List<Tuple2<E0, E1>>> any2(Function<S, Tuple2<Model.Field<E0>, Model.Field<E1>>> pick);

        <E0, E1> Deferred.Any<Tuple2<E0, E1>> deferredAny2(Function<S, Tuple2<Model.Field<E0>, Model.Field<E1>>> pick);

        <E0, E1, E2> Future<Tuple3<E0, E1, E2>> one3(
                Function<S, Tuple3<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>>> pick);

        <E0, E1, E2> Deferred.One<Tuple3<E0, E1, E2>> deferredOne3(
                Function<S, Tuple3<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>>> pick);

        <E0, E1, E2> Future<List<Tuple3<E0, E1, E2>>> any3(
                Function<S, Tuple3<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>>> pick);

        <E0, E1, E2> Deferred.Any<Tuple3<E0, E1, E2>> deferredAny3(
                Function<S, Tuple3<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>>> pick);

        <E0, E1, E2, E3> Future<Tuple4<E0, E1, E2, E3>> one4(
                Function<S, Tuple4<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>>> pick);

        <E0, E1, E2, E3> Deferred.One<Tuple4<E0, E1, E2, E3>> deferredOne4(
                Function<S, Tuple4<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>>> pick);

        <E0, E1, E2, E3> Future<List<Tuple4<E0, E1, E2, E3>>> any4(
                Function<S, Tuple4<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>>> pick);

        <E0, E1, E2, E3> Deferred.Any<Tuple4<E0, E1, E2, E3>> deferredAny4(
                Function<S, Tuple4<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>>> pick);

        <E0, E1, E2, E3, E4> Future<Tuple5<E0, E1, E2, E3, E4>> one5(
                Function<S, Tuple5<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>>> pick);

        <E0, E1, E2, E3, E4> Deferred.One<Tuple5<E0, E1, E2, E3, E4>> deferredOne5(
                Function<S, Tuple5<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>>> pick);

        <E0, E1, E2, E3, E4> Future<List<Tuple5<E0, E1, E2, E3, E4>>> any5(
                Function<S, Tuple5<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>>> pick);

        <E0, E1, E2, E3, E4> Deferred.Any<Tuple5<E0, E1, E2, E3, E4>> deferredAny5(
                Function<S, Tuple5<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>>> pick);

        <E0, E1, E2, E3, E4, E5> Future<Tuple6<E0, E1, E2, E3, E4, E5>> one6(
                Function<S, Tuple6<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>>> pick);

        <E0, E1, E2, E3, E4, E5> Deferred.One<Tuple6<E0, E1, E2, E3, E4, E5>> deferredOne6(
                Function<S, Tuple6<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>>> pick);

        <E0, E1, E2, E3, E4, E5> Future<List<Tuple6<E0, E1, E2, E3, E4, E5>>> any6(
                Function<S, Tuple6<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>>> pick);

        <E0, E1, E2, E3, E4, E5> Deferred.Any<Tuple6<E0, E1, E2, E3, E4, E5>> deferredAny6(
                Function<S, Tuple6<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>>> pick);

        <E0, E1, E2, E3, E4, E5, E6> Future<Tuple7<E0, E1, E2, E3, E4, E5, E6>> one7(
                Function<S, Tuple7<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>>> pick);

        <E0, E1, E2, E3, E4, E5, E6> Deferred.One<Tuple7<E0, E1, E2, E3, E4, E5, E6>> deferredOne7(
                Function<S, Tuple7<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>>> pick);

        <E0, E1, E2, E3, E4, E5, E6> Future<List<Tuple7<E0, E1, E2, E3, E4, E5, E6>>> any7(
                Function<S, Tuple7<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>>> pick);

        <E0, E1, E2, E3, E4, E5, E6> Deferred.Any<Tuple7<E0, E1, E2, E3, E4, E5, E6>> deferredAny7(
                Function<S, Tuple7<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7> Future<Tuple8<E0, E1, E2, E3, E4, E5, E6, E7>> one8(
                Function<S, Tuple8<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7> Deferred.One<Tuple8<E0, E1, E2, E3, E4, E5, E6, E7>> deferredOne8(
                Function<S, Tuple8<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7> Future<List<Tuple8<E0, E1, E2, E3, E4, E5, E6, E7>>> any8(
                Function<S, Tuple8<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7> Deferred.Any<Tuple8<E0, E1, E2, E3, E4, E5, E6, E7>> deferredAny8(
                Function<S, Tuple8<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8> Future<Tuple9<E0, E1, E2, E3, E4, E5, E6, E7, E8>> one9(
                Function<S, Tuple9<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8> Deferred.One<Tuple9<E0, E1, E2, E3, E4, E5, E6, E7, E8>> deferredOne9(
                Function<S, Tuple9<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8> Future<List<Tuple9<E0, E1, E2, E3, E4, E5, E6, E7, E8>>> any9(
                Function<S, Tuple9<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8> Deferred.Any<Tuple9<E0, E1, E2, E3, E4, E5, E6, E7, E8>> deferredAny9(
                Function<S, Tuple9<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9> Future<Tuple10<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9>> one10(
                Function<S, Tuple10<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9> Deferred.One<Tuple10<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9>> deferredOne10(
                Function<S, Tuple10<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9> Future<List<Tuple10<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9>>> any10(
                Function<S, Tuple10<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9> Deferred.Any<Tuple10<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9>> deferredAny10(
                Function<S, Tuple10<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10> Future<Tuple11<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10>> one11(
                Function<S, Tuple11<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10> Deferred.One<Tuple11<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10>> deferredOne11(
                Function<S, Tuple11<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10> Future<List<Tuple11<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10>>> any11(
                Function<S, Tuple11<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10> Deferred.Any<Tuple11<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10>> deferredAny11(
                Function<S, Tuple11<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11> Future<Tuple12<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11>> one12(
                Function<S, Tuple12<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11> Deferred.One<Tuple12<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11>> deferredOne12(
                Function<S, Tuple12<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11> Future<List<Tuple12<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11>>> any12(
                Function<S, Tuple12<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11> Deferred.Any<Tuple12<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11>> deferredAny12(
                Function<S, Tuple12<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12> Future<Tuple13<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12>> one13(
                Function<S, Tuple13<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12> Deferred.One<Tuple13<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12>> deferredOne13(
                Function<S, Tuple13<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12> Future<List<Tuple13<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12>>> any13(
                Function<S, Tuple13<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12> Deferred.Any<Tuple13<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12>> deferredAny13(
                Function<S, Tuple13<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13> Future<Tuple14<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13>> one14(
                Function<S, Tuple14<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>, Model.Field<E13>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13> Deferred.One<Tuple14<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13>> deferredOne14(
                Function<S, Tuple14<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>, Model.Field<E13>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13> Future<List<Tuple14<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13>>> any14(
                Function<S, Tuple14<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>, Model.Field<E13>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13> Deferred.Any<Tuple14<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13>> deferredAny14(
                Function<S, Tuple14<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>, Model.Field<E13>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14> Future<Tuple15<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14>> one15(
                Function<S, Tuple15<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>, Model.Field<E13>, Model.Field<E14>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14> Deferred.One<Tuple15<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14>> deferredOne15(
                Function<S, Tuple15<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>, Model.Field<E13>, Model.Field<E14>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14> Future<List<Tuple15<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14>>> any15(
                Function<S, Tuple15<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>, Model.Field<E13>, Model.Field<E14>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14> Deferred.Any<Tuple15<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14>> deferredAny15(
                Function<S, Tuple15<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>, Model.Field<E13>, Model.Field<E14>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15> Future<Tuple16<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15>> one16(
                Function<S, Tuple16<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>, Model.Field<E13>, Model.Field<E14>, Model.Field<E15>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15> Deferred.One<Tuple16<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15>> deferredOne16(
                Function<S, Tuple16<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>, Model.Field<E13>, Model.Field<E14>, Model.Field<E15>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15> Future<List<Tuple16<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15>>> any16(
                Function<S, Tuple16<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>, Model.Field<E13>, Model.Field<E14>, Model.Field<E15>>> pick);

        <E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15> Deferred.Any<Tuple16<E0, E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12, E13, E14, E15>> deferredAny16(
                Function<S, Tuple16<Model.Field<E0>, Model.Field<E1>, Model.Field<E2>, Model.Field<E3>, Model.Field<E4>, Model.Field<E5>, Model.Field<E6>, Model.Field<E7>, Model.Field<E8>, Model.Field<E9>, Model.Field<E10>, Model.Field<E11>, Model.Field<E12>, Model.Field<E13>, Model.Field<E14>, Model.Field<E15>>> pick);

        //endregion

    }

}
