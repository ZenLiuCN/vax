package cn.zenliu.vax.common;

import cn.zenliu.vax.common.trait.XDisposable;

/**
 * long live elements
 * @author Zen.Liu
 * @since 2024-11-02
 */
public interface XActivity extends XElement, XDisposable {
    @Override
    Class<? extends XActivity> $type();
}
