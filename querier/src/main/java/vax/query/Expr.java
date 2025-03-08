package vax.query;

/**
 * @author Zen.Liu
 * @since 2025-03-07
 */

public interface Expr {
    record Sorter(Value<?> v, boolean desc) implements Expr {}

    record Assign(Value<?> field, Value<?> val) implements Expr {}

    enum JoinMode {
        INNER, LEFT, RIGHT, FULL
    }


    record Join(JoinMode mode, Statement.Store<?, ?> target, Value.Bool cond) implements Expr {}
}
