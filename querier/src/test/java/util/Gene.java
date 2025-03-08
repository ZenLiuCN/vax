package util;

import org.jooq.lambda.Seq;

/**
 * @author Zen.Liu
 * @since 2025-03-08
 */
public interface Gene {
    static String stores() {
        return Seq.range(2, 17)
                  .map(n -> """
                          interface UStore%1$d<%2$s,
                          %3$s> extends Statement.Store<Tuple%1$d<%2$s>,
                          UStore%1$d<%2$s,
                          %3$s>> {
                                  %4$s;
                                 default <E%1$d,S%1$d extends Store<E%1$d,?>> UStore%5$d<%2$s,E%1$d,%3$s,S%1$d> andJoin(
                                 S%1$d store
                                 ,Function<UStore%5$d<%2$s,E%1$d,%3$s,S%1$d>,Value.Bool> cond){
                                   return andJoin(JoinMode.INNER,store,cond);
                                 }
                                  <E%1$d,S%1$d extends Store<E%1$d,?>> UStore%5$d<%2$s,E%1$d,%3$s,S%1$d> andJoin(
                                  JoinMode mode
                                  ,S%1$d store
                                  , Function<UStore%5$d<%2$s,E%1$d,%3$s,S%1$d>,Value.Bool> cond);
                          
                          }
                          """.formatted(
                          n,
                          Seq.range(0, n)
                             .map("E%d"::formatted)
                             .toString(","),
                          Seq.range(0, n)
                             .map("S%d"::formatted)
                             .toString(","),
                          Seq.range(0, n)
                             .map("S%1$d s%1$d()"::formatted)
                             .toString(";\n\t\t\t\t"),
                          n + 1
                                       ))
                  .toString("\n");
    }
    static String funcFields(){
        return Seq.range(2, 17)
                  .map(n -> """
                          <%2$s> Future<Tuple%1$d<%2$s>> one%1$d(Function<S,Tuple%1$d<%3$s>> pick);
                          <%2$s> Deferred.One<Tuple%1$d<%2$s>> deferredOne%1$d(Function<S, Tuple%1$d<%3$s>> pick);
                          <%2$s> Future<List<Tuple%1$d<%2$s>>> any%1$d(Function<S,Tuple%1$d<%3$s>> pick);
                          <%2$s> Deferred.Any<Tuple%1$d<%2$s>> deferredAny%1$d(Function<S,Tuple%1$d<%3$s>> pick);
                          """.formatted(
                          n,
                          Seq.range(0, n)
                             .map("E%d"::formatted)
                             .toString(","),
                          Seq.range(0, n)
                             .map("Model.Field<E%d>"::formatted)
                             .toString(",")
                                       ))
                  .toString("\n");
    }
    static String implFields(){
        return Seq.range(2, 17)
                  .map("""
                       @Override
                       public Future<List> any%1$d(Function pick) {
                           this.state.pick = List.of((Model.Field<?>) pick.apply(this));
                           this.state.mode = MODE.ANY;
                           return deferred().any();
                       }
                       @Override
                       public Statement.Deferred.Any<List> deferredAny%1$d(Function pick) {
                            state.mode=MODE.ANY;
                            state.pick= (List<Model.Field<?>>) pick.apply(this);
                            return deferred();
                       }
                       @Override
                       public Future<Tuple%1$d> one%1$d(Function pick) {
                           this.state.pick = List.of((Model.Field<?>) pick.apply(this));
                           this.state.mode = MODE.ONE;
                           return deferred().one();
                       }
                       @Override
                       public Statement.Deferred.One<Tuple%1$d> deferredOne%1$d(Function pick) {
                            state.mode=MODE.ONE;
                            state.pick= (List<Model.Field<?>>) pick.apply(this);
                            return deferred();
                       }
                       """::formatted)
                  .toString("\n");
    }

    public static void main(String[] args) {
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new java.awt.datatransfer.
                                StringSelection(implFields()), null);
    }


    //<E1,S1 extends Statement.Store<E1, S1>> Statement.Store<Tuple2<E,E1>, Statement.Store<Tuple2<E,E1>>>join(S1 s1, Function2<S, S1, Value.Bool> cond);
}
