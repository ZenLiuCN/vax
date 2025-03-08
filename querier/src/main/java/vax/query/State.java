package vax.query;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.Nullable;
import org.jooq.lambda.tuple.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Zen.Liu
 * @since 2025-03-08
 */
@SuppressWarnings({"rawtypes", "unchecked", "DataFlowIssue"})
public class State implements Statement.Singular,
                              Statement.Store,
                              Statement.Assigner,
                              Statement.Skip,
                              Statement.Limit,
                              Statement.Query,
                              Statement.UStore2,
                              Statement.UStore16 {
    public interface Executor {

    }

    record Deferred(MODE mode, String sql, Map<String, Object> parameters, Map<String, Value.hold<?>> holders,
                    List<List<Object>> arguments,
                    Executor exec)
            implements
            Statement.Deferred.Put
                    , Statement.Deferred.Any
                    , Statement.Deferred.One
                    , Statement.Deferred.Count
                    , Statement.Deferred.Delete
                    , Statement.Deferred.Into
                    , Statement.Deferred.Modify {
        @Override
        public Future<Integer> delete(Map parameters) {
            return null;
        }

        @Override
        public Future<List> one(Map parameters) {
            return null;
        }

        @Override
        public Future<Integer> count(Map parameters) {
            return null;
        }

        @Override
        public Future<Integer> into(Map parameters) {
            return null;
        }

        @Override
        public Future<Void> putRaw(JsonObject i, JsonObject... m) {
            return null;
        }

        @Override
        public Future<Void> put(Object i, Object... m) {
            return null;
        }

        @Override
        public Future<Integer> modify(Map parameters) {
            return null;
        }

        public Future<List> any() {
            return null;
        }

        public Future one() {
            return null;
        }

        public Future delete() {
            return null;
        }

        public Future count() {
            return null;
        }

        public Future into() {
            return null;
        }

        public Future put() {
            return null;
        }

        public Future modify() {
            return null;
        }

        public Future info() {
            return null;
        }
    }

    //region Wrapper
    record U3(State s) implements Statement.UStore3 {
        @Override
        public Statement.UStore4 andJoin(Expr.JoinMode mode, Statement.Store store, Function cond) {
            s.state.join(new Expr.Join(mode, store, (Value.Bool) cond.apply(s)));
            return new U4(s);
        }

        @Override
        public Object s0() {
            return s.s0();
        }

        @Override
        public Object s1() {
            return s.s1();
        }

        @Override
        public Object s2() {
            return s.s2();
        }


        @Override
        public Statement.Assigner filter(Function filter) {
            return s.filter(filter);
        }
    }

    record U4(State s) implements Statement.UStore4 {
        @Override
        public Statement.UStore5 andJoin(Expr.JoinMode mode, Statement.Store store, Function cond) {
            s.state.join(new Expr.Join(mode, store, (Value.Bool) cond.apply(s)));
            return new U5(s);
        }

        @Override
        public Object s0() {
            return s.s0();
        }

        @Override
        public Object s1() {
            return s.s1();
        }

        @Override
        public Object s2() {
            return s.s2();
        }

        @Override
        public Object s3() {
            return s.s3();
        }

        @Override
        public Statement.Assigner filter(Function filter) {
            return s.filter(filter);
        }
    }

    record U5(State s) implements Statement.UStore5 {
        @Override
        public Statement.UStore6 andJoin(Expr.JoinMode mode, Statement.Store store, Function cond) {
            s.state.join(new Expr.Join(mode, store, (Value.Bool) cond.apply(s)));
            return new U6(s);
        }

        @Override
        public Object s0() {
            return s.s0();
        }

        @Override
        public Object s1() {
            return s.s1();
        }

        @Override
        public Object s2() {
            return s.s2();
        }

        @Override
        public Object s3() {return s.s3();}

        @Override
        public Object s4() {return s.s4();}


        @Override
        public Statement.Assigner filter(Function filter) {
            return s.filter(filter);
        }
    }

    record U6(State s) implements Statement.UStore6 {
        @Override
        public Statement.UStore7 andJoin(Expr.JoinMode mode, Statement.Store store, Function cond) {
            s.state.join(new Expr.Join(mode, store, (Value.Bool) cond.apply(s)));
            return new U7(s);
        }

        @Override
        public Object s0() {
            return s.s0();
        }

        @Override
        public Object s1() {
            return s.s1();
        }

        @Override
        public Object s2() {
            return s.s2();
        }

        @Override
        public Object s3() {return s.s3();}

        @Override
        public Object s4() {return s.s4();}

        @Override
        public Object s5() {return s.s5();}


        @Override
        public Statement.Assigner filter(Function filter) {
            return s.filter(filter);
        }
    }

    record U7(State s) implements Statement.UStore7 {
        @Override
        public Statement.UStore8 andJoin(Expr.JoinMode mode, Statement.Store store, Function cond) {
            s.state.join(new Expr.Join(mode, store, (Value.Bool) cond.apply(s)));
            return new U8(s);
        }

        @Override
        public Object s0() {
            return s.s0();
        }

        @Override
        public Object s1() {
            return s.s1();
        }

        @Override
        public Object s2() {
            return s.s2();
        }

        @Override
        public Object s3() {return s.s3();}

        @Override
        public Object s4() {return s.s4();}

        @Override
        public Object s5() {return s.s5();}

        @Override
        public Object s6() {return s.s6();}


        @Override
        public Statement.Assigner filter(Function filter) {
            return s.filter(filter);
        }
    }

    record U8(State s) implements Statement.UStore8 {
        @Override
        public Statement.UStore9 andJoin(Expr.JoinMode mode, Statement.Store store, Function cond) {
            s.state.join(new Expr.Join(mode, store, (Value.Bool) cond.apply(s)));
            return new U9(s);
        }

        @Override
        public Object s0() {
            return s.s0();
        }

        @Override
        public Object s1() {
            return s.s1();
        }

        @Override
        public Object s2() {
            return s.s2();
        }

        @Override
        public Object s3() {return s.s3();}

        @Override
        public Object s4() {return s.s4();}

        @Override
        public Object s5() {return s.s5();}

        @Override
        public Object s6() {return s.s6();}

        @Override
        public Object s7() {return s.s7();}


        @Override
        public Statement.Assigner filter(Function filter) {
            return s.filter(filter);
        }
    }

    record U9(State s) implements Statement.UStore9 {
        @Override
        public Statement.UStore10 andJoin(Expr.JoinMode mode, Statement.Store store, Function cond) {
            s.state.join(new Expr.Join(mode, store, (Value.Bool) cond.apply(s)));
            return new U10(s);
        }

        @Override
        public Object s0() {
            return s.s0();
        }

        @Override
        public Object s1() {
            return s.s1();
        }

        @Override
        public Object s2() {
            return s.s2();
        }

        @Override
        public Object s3() {return s.s3();}

        @Override
        public Object s4() {return s.s4();}

        @Override
        public Object s5() {return s.s5();}

        @Override
        public Object s6() {return s.s6();}

        @Override
        public Object s7() {return s.s7();}

        @Override
        public Object s8() {return s.s8();}


        @Override
        public Statement.Assigner filter(Function filter) {
            return s.filter(filter);
        }
    }

    record U10(State s) implements Statement.UStore10 {
        @Override
        public Statement.UStore11 andJoin(Expr.JoinMode mode, Statement.Store store, Function cond) {
            s.state.join(new Expr.Join(mode, store, (Value.Bool) cond.apply(s)));
            return new U11(s);
        }

        @Override
        public Object s0() {
            return s.s0();
        }

        @Override
        public Object s1() {
            return s.s1();
        }

        @Override
        public Object s2() {
            return s.s2();
        }

        @Override
        public Object s3() {return s.s3();}

        @Override
        public Object s4() {return s.s4();}

        @Override
        public Object s5() {return s.s5();}

        @Override
        public Object s6() {return s.s6();}

        @Override
        public Object s7() {return s.s7();}

        @Override
        public Object s8() {return s.s8();}

        @Override
        public Object s9() {return s.s9();}

        @Override
        public Statement.Assigner filter(Function filter) {
            return s.filter(filter);
        }
    }

    record U11(State s) implements Statement.UStore11 {
        @Override
        public Statement.UStore12 andJoin(Expr.JoinMode mode, Statement.Store store, Function cond) {
            s.state.join(new Expr.Join(mode, store, (Value.Bool) cond.apply(s)));
            return new U12(s);
        }

        @Override
        public Object s0() {
            return s.s0();
        }

        @Override
        public Object s1() {
            return s.s1();
        }

        @Override
        public Object s2() {
            return s.s2();
        }

        @Override
        public Object s3() {return s.s3();}

        @Override
        public Object s4() {return s.s4();}

        @Override
        public Object s5() {return s.s5();}

        @Override
        public Object s6() {return s.s6();}

        @Override
        public Object s7() {return s.s7();}

        @Override
        public Object s8() {return s.s8();}

        @Override
        public Object s9() {return s.s9();}

        @Override
        public Object s10() {return s.s10();}


        @Override
        public Statement.Assigner filter(Function filter) {
            return s.filter(filter);
        }
    }

    record U12(State s) implements Statement.UStore12 {
        @Override
        public Statement.UStore13 andJoin(Expr.JoinMode mode, Statement.Store store, Function cond) {
            s.state.join(new Expr.Join(mode, store, (Value.Bool) cond.apply(s)));
            return new U13(s);
        }

        @Override
        public Object s0() {
            return s.s0();
        }

        @Override
        public Object s1() {
            return s.s1();
        }

        @Override
        public Object s2() {
            return s.s2();
        }

        @Override
        public Object s3() {return s.s3();}

        @Override
        public Object s4() {return s.s4();}

        @Override
        public Object s5() {return s.s5();}

        @Override
        public Object s6() {return s.s6();}

        @Override
        public Object s7() {return s.s7();}

        @Override
        public Object s8() {return s.s8();}

        @Override
        public Object s9() {return s.s9();}

        @Override
        public Object s10() {return s.s10();}

        @Override
        public Object s11() {return s.s11();}

        @Override
        public Statement.Assigner filter(Function filter) {
            return s.filter(filter);
        }
    }

    record U13(State s) implements Statement.UStore13 {
        @Override
        public Statement.UStore14 andJoin(Expr.JoinMode mode, Statement.Store store, Function cond) {
            s.state.join(new Expr.Join(mode, store, (Value.Bool) cond.apply(s)));
            return new U14(s);
        }

        @Override
        public Object s0() {
            return s.s0();
        }

        @Override
        public Object s1() {
            return s.s1();
        }

        @Override
        public Object s2() {
            return s.s2();
        }


        @Override
        public Object s3() {return s.s3();}

        @Override
        public Object s4() {return s.s4();}

        @Override
        public Object s5() {return s.s5();}

        @Override
        public Object s6() {return s.s6();}

        @Override
        public Object s7() {return s.s7();}

        @Override
        public Object s8() {return s.s8();}

        @Override
        public Object s9() {return s.s9();}

        @Override
        public Object s10() {return s.s10();}

        @Override
        public Object s11() {return s.s11();}

        @Override
        public Object s12() {return s.s12();}


        @Override
        public Statement.Assigner filter(Function filter) {
            return s.filter(filter);
        }
    }

    record U14(State s) implements Statement.UStore14 {
        @Override
        public Statement.UStore15 andJoin(Expr.JoinMode mode, Statement.Store store, Function cond) {
            s.state.join(new Expr.Join(mode, store, (Value.Bool) cond.apply(s)));
            return new U15(s);
        }

        @Override
        public Object s0() {
            return s.s0();
        }

        @Override
        public Object s1() {
            return s.s1();
        }

        @Override
        public Object s2() {
            return s.s2();
        }


        @Override
        public Object s3() {return s.s3();}

        @Override
        public Object s4() {return s.s4();}

        @Override
        public Object s5() {return s.s5();}

        @Override
        public Object s6() {return s.s6();}

        @Override
        public Object s7() {return s.s7();}

        @Override
        public Object s8() {return s.s8();}

        @Override
        public Object s9() {return s.s9();}

        @Override
        public Object s10() {return s.s10();}

        @Override
        public Object s11() {return s.s11();}

        @Override
        public Object s12() {return s.s12();}

        @Override
        public Object s13() {return s.s13();}

        @Override
        public Statement.Assigner filter(Function filter) {
            return s.filter(filter);
        }
    }

    record U15(State s) implements Statement.UStore15 {
        @Override
        public Statement.UStore16 andJoin(Expr.JoinMode mode, Statement.Store store, Function cond) {
            s.state.join(new Expr.Join(mode, store, (Value.Bool) cond.apply(s)));
            return s;
        }

        @Override
        public Object s0() {
            return s.s0();
        }

        @Override
        public Object s1() {
            return s.s1();
        }

        @Override
        public Object s2() {
            return s.s2();
        }


        @Override
        public Object s3() {return s.s3();}

        @Override
        public Object s4() {return s.s4();}

        @Override
        public Object s5() {return s.s5();}

        @Override
        public Object s6() {return s.s6();}

        @Override
        public Object s7() {return s.s7();}

        @Override
        public Object s8() {return s.s8();}

        @Override
        public Object s9() {return s.s9();}

        @Override
        public Object s10() {return s.s10();}

        @Override
        public Object s11() {return s.s11();}

        @Override
        public Object s12() {return s.s12();}

        @Override
        public Object s13() {return s.s13();}

        @Override
        public Object s14() {return s.s14();}

        @Override
        public Statement.Assigner filter(Function filter) {
            return s.filter(filter);
        }
    }
    //endregion


    //region Impl


    @Override
    public Future<Void> putRaw(JsonObject value, JsonObject... more) {
        var x = more.length == 0 ? List.of(value) : Arrays.asList(more);
        if (more.length != 0) x.add(value);
        this.state.values = x;
        this.state.mode = MODE.PUT;
        return deferred().put().mapEmpty();
    }

    @Override
    public Statement.Deferred.Put deferredPut() {
        this.state.mode = MODE.PUT;
        return deferred();
    }


    @Override
    public Future<Void> put(Object value, Object... more) {
        return putRaw(map.apply(value));
    }


    @Override
    public Statement.UStore2 join(Expr.JoinMode mode, Statement.Store store,
                                  Function cond) {
        this.state.join(new Expr.Join(mode, store, (Value.Bool) cond.apply(this)));
        return this;
    }

    @Override
    public Statement.Assigner filter(Function filter) {
        this.state.filter = ((Value.Bool) filter.apply(this));
        return this;
    }

    @Override
    public Future<Void> into(Function fn) {
        this.state.into = (List<Expr.Assign>) fn.apply(this);
        this.state.mode = MODE.INTO;
        return deferred().into();
    }

    @Override
    public Statement.Deferred.Into deferredInto(Function fn) {
        this.state.into = (List<Expr.Assign>) fn.apply(this);
        this.state.mode = MODE.INTO;
        return deferred();
    }

    @Override
    public Future<Void> modify(Function fn) {
        this.state.modified = (List<Expr.Assign>) fn.apply(this);
        this.state.mode = MODE.SET;
        return deferred().modify();
    }

    @Override
    public Statement.Deferred.Modify deferredModify(Function fn) {
        this.state.modified = (List<Expr.Assign>) fn.apply(this);
        this.state.mode = MODE.SET;
        return deferred();
    }

    @Override
    public Statement.Skip sort(Function sort) {
        this.state.sort = ((List<Expr.Sorter>) sort.apply(this));
        return this;
    }

    @Override
    public Statement.Limit skip(int n) {
        this.state.skip = n;
        return this;
    }

    @Override
    public Statement.Query limit(int n) {
        this.state.limit = n;
        return this;
    }

    @Override
    public Future<List> any() {
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any deferredAny() {
        this.state.mode = MODE.ANY;
        return deferred();
    }

    @Override
    public Future<Integer> delete() {
        this.state.mode = MODE.DELETE;
        return deferred().delete();
    }

    @Override
    public Statement.Deferred.Delete deferredDelete() {
        this.state.mode = MODE.DELETE;
        return deferred();
    }


    @Override
    public Future one() {
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One deferredOne() {
        this.state.mode = MODE.ONE;
        return deferred();
    }

    @Override
    public Future<Integer> count() {
        this.state.mode = MODE.COUNT;
        return deferred().count();
    }

    @Override
    public Statement.Deferred.Count deferredCount() {
        this.state.mode = MODE.COUNT;
        return deferred();
    }


    @Override
    public Future<Integer> count(Function pick) {
        this.state.mode = MODE.COUNT;
        this.state.count = (Model.Field<?>) pick.apply(this);
        return deferred().count();
    }

    @Override
    public Statement.Deferred.Count deferredCount(Function pick) {
        this.state.count = ((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.COUNT;
        return deferred();
    }

    @Override
    public Future one(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Future<List> any(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred();
    }

    @Override
    public Statement.Deferred.One deferredOne(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred();
    }
    //region many

    @Override
    public Future<List> any2(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny2(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple2> one2(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple2> deferredOne2(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<List> any3(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny3(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple3> one3(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple3> deferredOne3(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<List> any4(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny4(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple4> one4(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple4> deferredOne4(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<List> any5(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny5(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple5> one5(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple5> deferredOne5(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<List> any6(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny6(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple6> one6(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple6> deferredOne6(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<List> any7(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny7(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple7> one7(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple7> deferredOne7(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<List> any8(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny8(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple8> one8(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple8> deferredOne8(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<List> any9(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny9(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple9> one9(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple9> deferredOne9(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<List> any10(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny10(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple10> one10(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple10> deferredOne10(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<List> any11(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny11(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple11> one11(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple11> deferredOne11(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<List> any12(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny12(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple12> one12(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple12> deferredOne12(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<List> any13(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny13(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple13> one13(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple13> deferredOne13(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<List> any14(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny14(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple14> one14(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple14> deferredOne14(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<List> any15(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny15(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple15> one15(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple15> deferredOne15(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<List> any16(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ANY;
        return deferred().any();
    }

    @Override
    public Statement.Deferred.Any<List> deferredAny16(Function pick) {
        state.mode = MODE.ANY;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    @Override
    public Future<Tuple16> one16(Function pick) {
        this.state.pick = List.of((Model.Field<?>) pick.apply(this));
        this.state.mode = MODE.ONE;
        return deferred().one();
    }

    @Override
    public Statement.Deferred.One<Tuple16> deferredOne16(Function pick) {
        state.mode = MODE.ONE;
        state.pick = (List<Model.Field<?>>) pick.apply(this);
        return deferred();
    }

    //endregion
    @Override
    public Statement.UStore3 andJoin(Expr.JoinMode mode, Statement.Store store, Function cond) {
        state.join.add(new Expr.Join(mode, store, (Value.Bool) cond.apply(this)));
        state.stores.add(store);
        return new U3(this);
    }

    @Override
    public Object s0() {
        return state.stores.get(0);
    }

    @Override
    public Object s1() {
        return state.stores.get(1);
    }

    @Override
    public Object s10() {
        return state.stores.get(10);
    }

    @Override
    public Object s2() {
        return state.stores.get(2);
    }

    @Override
    public Object s3() {
        return state.stores.get(3);
    }

    @Override
    public Object s4() {
        return state.stores.get(4);
    }

    @Override
    public Object s5() {
        return state.stores.get(5);
    }

    @Override
    public Object s6() {
        return state.stores.get(6);
    }

    @Override
    public Object s7() {
        return state.stores.get(7);
    }

    @Override
    public Object s8() {
        return state.stores.get(8);
    }

    @Override
    public Object s9() {
        return state.stores.get(9);
    }

    @Override
    public Object s11() {
        return state.stores.get(11);
    }

    @Override
    public Object s12() {
        return state.stores.get(12);
    }

    @Override
    public Object s13() {
        return state.stores.get(13);
    }

    @Override
    public Object s14() {
        return state.stores.get(14);
    }

    @Override
    public Object s15() {
        return state.stores.get(15);
    }
    //endregion


    @Override
    public Statement.Singular as(String alias) {
        assert state.mode == null : "mode require null";

        return state.computeVirtual(dialect, alias);
    }

    public enum MODE {
        /**
         * mode of fetch one value
         */
        ONE(s -> true),
        /**
         * mode of fetch rows of value
         */
        ANY(s -> true),
        /**
         * mode of count the rows
         */
        COUNT(s -> true),
        /**
         * mode of put value or values into store
         */
        PUT(s -> true),
        /**
         * mode of remove values from store
         */
        DELETE(s -> true),
        /**
         * mode of cross input values into store
         */
        INTO(s -> true),
        /**
         * mode of modify values
         */
        SET(s -> true),
        ;
        public final Predicate<Status> validator;

        MODE(Predicate<Status> validator) {this.validator = validator;}
    }

    public static final Predicate<Status> HAVE_PICK = s -> s.pick != null && !s.pick.isEmpty();
    public static final Predicate<Status> HAVE_COUNT = s -> s.count != null;
    public static final Predicate<Status> HAVE_JOIN = s -> s.join != null && !s.join.isEmpty();
    public static final Predicate<Status> HAVE_VALUES = s -> s.values != null && !s.values.isEmpty();
    public static final Predicate<Status> HAVE_MODIFIED = s -> s.modified != null && !s.modified.isEmpty();
    public static final Predicate<Status> HAVE_FILTER = s -> s.filter != null;
    public static final Predicate<Status> HAVE_SORT = s -> s.sort != null && !s.sort.isEmpty();
    public static final Predicate<Status> HAVE_LIMIT = s -> s.limit != null && s.limit > 0;
    public static final Predicate<Status> HAVE_SKIP = s -> s.skip != null && s.skip > 0;
    public static final Predicate<Status> HAVE_INTO = s -> s.into != null && !s.into.isEmpty();
    //region asserts
    //@formatter:off
    public static final Predicate<Status> ASSERT_PICK = s -> {assert HAVE_PICK.test(s):"ASSERT_PICK fail";return true;};
    public static final Predicate<Status> ASSERT_COUNT = s -> {assert HAVE_COUNT.test(s):"ASSERT_COUNT fail";return true;};
    public static final Predicate<Status> ASSERT_JOIN = s -> {assert HAVE_JOIN.test(s):"ASSERT_JOIN fail";return true;};
    public static final Predicate<Status> ASSERT_VALUES = s -> {assert HAVE_VALUES.test(s):"ASSERT_VALUES fail";return true;};
    public static final Predicate<Status> ASSERT_MODIFIED = s -> {assert HAVE_MODIFIED.test(s):"ASSERT_MODIFIED fail";return true;};
    public static final Predicate<Status> ASSERT_FILTER = s -> {assert HAVE_FILTER.test(s):"ASSERT_FILTER fail";return true;};
    public static final Predicate<Status> ASSERT_SORT = s -> {assert HAVE_SORT.test(s):"ASSERT_SORT fail";return true;};
    public static final Predicate<Status> ASSERT_LIMIT = s -> {assert HAVE_LIMIT.test(s):"ASSERT_LIMIT fail";return true;};
    public static final Predicate<Status> ASSERT_SKIP = s -> {assert HAVE_SKIP.test(s):"ASSERT_SKIP fail";return true;};
    public static final Predicate<Status> ASSERT_INTO = s -> {assert HAVE_INTO.test(s):"ASSERT_INTO fail";return true;};

    public static final Predicate<Status> ASSERT_NO_PICK = s -> {assert !HAVE_PICK.test(s):"ASSERT_NO_PICK fail";return true;};
    public static final Predicate<Status> ASSERT_NO_COUNT = s -> {assert !HAVE_COUNT.test(s):"ASSERT_NO_COUNT fail";return true;};
    public static final Predicate<Status> ASSERT_NO_JOIN = s -> {assert !HAVE_JOIN.test(s):"ASSERT_NO_JOIN fail";return true;};
    public static final Predicate<Status> ASSERT_NO_VALUES = s -> {assert !HAVE_VALUES.test(s):"ASSERT_NO_VALUES fail";return true;};
    public static final Predicate<Status> ASSERT_NO_MODIFIED = s -> {assert !HAVE_MODIFIED.test(s):"ASSERT_NO_MODIFIED fail";return true;};
    public static final Predicate<Status> ASSERT_NO_FILTER = s -> {assert !HAVE_FILTER.test(s):"ASSERT_NO_FILTER fail";return true;};
    public static final Predicate<Status> ASSERT_NO_SORT = s -> {assert !HAVE_SORT.test(s):"ASSERT_NO_SORT fail";return true;};
    public static final Predicate<Status> ASSERT_NO_LIMIT = s -> {assert !HAVE_LIMIT.test(s):"ASSERT_NO_LIMIT fail";return true;};
    public static final Predicate<Status> ASSERT_NO_SKIP = s -> {assert !HAVE_SKIP.test(s):"ASSERT_NO_SKIP fail";return true;};
    public static final Predicate<Status> ASSERT_NO_INTO = s -> {assert !HAVE_INTO.test(s):"ASSERT_NO_INTO fail";return true;};
    //@formatter:on
    //endregion


    private Deferred deferred() {
        var d = dialect.render(state);
        return new Deferred(d.mode(), d.sql(), d.parameters(), d.holders(), d.arguments(), dialect.executor());
    }

    private final Dialect dialect;
    /**
     * mapper that convert store value to JsonObject
     */
    private final Function<Object, JsonObject> map;

    private final Status state;

    public State(Dialect dialect, Function<Object, JsonObject> map, Model<?> primary) {
        this.dialect = dialect;
        this.map = map;
        state = new Status(primary);

    }

    public static final class Status {
        public Status(Model<?> primary) {this.primary = primary;}

        /**
         * all used stores
         */
        public final List stores = new ArrayList();

        public void join(Expr.Join join) {
            if (this.join == null) {
                this.join = new ArrayList<>();
            }
            assert this.join.size() <= 16 : "overflow of joined stores. max is 16";
            this.join.add(join);
            this.stores.add(join.target());
        }

        /**
         * the mode of state
         */
        public MODE mode;
        /**
         * the primary model
         */
        public final Model<?> primary;
        /**
         * optional counting field
         */
        @Nullable
        public Model.Field<?> count;
        /**
         * optional put values , empty for deferred mode.
         */
        @Nullable
        public List<JsonObject> values;
        /**
         * modified assign expressions
         */
        @Nullable
        public List<Expr.Assign> modified;
        /**
         * join expressions
         */
        @Nullable
        public List<Expr.Join> join;
        /**
         * filter expressions
         */
        @Nullable
        public Value.Bool filter;
        /**
         * sort expressions
         */
        @Nullable
        public List<Expr.Sorter> sort;
        /**
         * skip value
         */
        @Nullable
        public Integer skip;
        /**
         * limit value
         */
        @Nullable
        public Integer limit;
        /**
         * pick fields for retuning.
         *
         * @see Storage.Field
         * @see Model.Virtual
         */
        @Nullable
        public List<Model.Field<?>> pick;
        /**
         * fetch into assignments.
         */
        @Nullable
        public List<Expr.Assign> into;


        public Statement.Singular computeVirtual(Dialect dialect, String alias) {
            var orders = new ArrayList<>();
            var fields = new HashMap<>();
            Function<Object, JsonObject> mapper = null;
            //!TODO compute from current status
            var m = new Model.Virtual(orders, fields, this, alias);
            return new State(dialect, mapper, m);
        }
    }

}
