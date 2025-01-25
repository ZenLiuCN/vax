package vax.common;

/**
 * Singular functional element
 *
 * @author Zen.Liu
 * @since 2024-11-10
 */
public interface Func extends Element {
    @Override
    Class<? extends Func> $type();
}
