package cn.zenliu.vax.common;

import cn.zenliu.vax.common.trait.Disposable;

import java.util.List;

/**
 * Sys (System) is a group of actions
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Sys extends Element, Disposable {
    @Override
    Class<? extends Sys> $type();

    /**
     * other systems this is requiring.
     */
    default List<Class<? extends Sys>> $requires() {
        return List.of();
    }
}
