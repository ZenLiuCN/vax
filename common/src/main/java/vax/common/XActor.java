package vax.common;

/**
 * An actor is an entity that present an outside individual.<br/>
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface XActor extends XEntry{
    @Override
    Class<? extends XActor> $type();
}
