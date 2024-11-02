package cn.zenliu.vax.common;

/**
 * An actor is an entry that spawns from a Dominator.<br/>
 * An actor should always register to its dominator with an address to receives messages.<br/>
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Actor extends Entry, Activity{
    @Override
    Class<? extends Actor> $type();
}
