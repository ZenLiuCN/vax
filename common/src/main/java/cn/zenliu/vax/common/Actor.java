package cn.zenliu.vax.common;

/**
 * An actor is an entry with extra data.
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Actor extends Entry, Element{
    @Override
    Class<? extends Actor> $type();
}
