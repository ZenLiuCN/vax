package vax.common;

/**
 * Singular functional element
 *
 * @author Zen.Liu
 * @since 2024-11-10
 */
public interface XFunc extends XElement {
    @Override
    Class<? extends XFunc> $type();
}
