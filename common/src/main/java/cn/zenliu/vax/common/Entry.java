package cn.zenliu.vax.common;

import cn.zenliu.vax.common.trait.Identified;
import cn.zenliu.vax.common.trait.Versioned;

/**
 * Entry is a reference of an element. or element itself.
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Entry extends
        Identified,
        Versioned,
        Element {
    @Override
    Class<? extends Entry> $type();
}
