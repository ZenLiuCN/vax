package cn.zenliu.vax.common;

import cn.zenliu.vax.common.trait.XIdentified;
import cn.zenliu.vax.common.trait.XVersioned;

/**
 * Entry is a reference of an element. or element itself.
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface XEntry extends
        XIdentified,
        XVersioned,
        XElement {
    @Override
    Class<? extends XEntry> $type();
}
