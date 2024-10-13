package cn.zenliu.vax.common;

/**
 * An ability is a group of actions related to an Actor.
 * The entry should always be the actor itself.
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Ability extends Entry, Element {
    @Override
    Class<? extends Ability> $type();

}
