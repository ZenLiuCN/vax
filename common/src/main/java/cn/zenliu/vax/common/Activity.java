package cn.zenliu.vax.common;

import cn.zenliu.vax.common.trait.Disposable;

/**
 * long live elements
 * @author Zen.Liu
 * @since 2024-11-02
 */
public interface Activity extends Element, Disposable {
    @Override
    Class<? extends Activity> $type();
}
