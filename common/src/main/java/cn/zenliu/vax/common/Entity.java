package cn.zenliu.vax.common;

import cn.zenliu.vax.common.trait.Disposable;

/**
 * Entity is a Record with actions
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Entity extends Records, Disposable {
    @Override
    Class<? extends Entity> $type();
}
