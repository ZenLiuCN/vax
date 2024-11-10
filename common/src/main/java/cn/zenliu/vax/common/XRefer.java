package cn.zenliu.vax.common;

import io.vertx.core.Future;


/**
 * refer is reference to something.
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface XRefer extends XEntry {
    @Override
    Class<? extends XRefer> $type();


}
