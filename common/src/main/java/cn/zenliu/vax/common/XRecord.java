package cn.zenliu.vax.common;

import cn.zenliu.vax.common.trait.XAuditable;

/**
 * Record is a group of data.
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface XRecord extends XEntry, XAuditable {
    @Override
    Class<? extends XRecord> $type();
}
