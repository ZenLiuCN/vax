package vax.common;

import vax.common.trait.Auditable;

/**
 * Persis is a group of data that will be persisted.
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Persis extends Entry, Auditable {
    @Override
    Class<? extends Persis> $type();
}
