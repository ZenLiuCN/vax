package vax.common;

import vax.common.trait.XAuditable;

/**
 * Persis is a group of data that will be persisted.
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface XPersis extends XEntry, XAuditable {
    @Override
    Class<? extends XPersis> $type();
}
