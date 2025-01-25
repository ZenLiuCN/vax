package vax.query;

/**
 * @author Zen.Liu
 * @since 2025-01-18
 */
public interface Statement<T> extends Expr<T> {
    interface Query<T> extends Statement<T> {}

    interface Update extends Statement<Integer> {}

    interface Delete extends Statement<Integer> {}

    interface Create extends Statement<Integer> {}
}
