package cn.zenliu.vax.common.meta;

import io.vertx.core.Future;

/**
 * @author Zen.Liu
 * @since 2024-10-13
 */
public interface Registry {
    Future<Meta> find(long identifier);

    Future<Long> register(Meta meta);
}
