package vax.common;

/**
 * An actor is an entity that present an outside individual.<br/>
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Actor extends Entry {
    @Override
    Class<? extends Actor> $type();
}
