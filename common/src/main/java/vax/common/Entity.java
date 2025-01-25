package vax.common;

/**
 * Entity is a Record
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Entity extends Persis {
    @Override
    Class<? extends Entity> $type();
}
