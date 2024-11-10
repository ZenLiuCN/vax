package cn.zenliu.vax.common.trait;


import io.vertx.core.Future;

/**
 * Disposable for anything that need free when will not be used anymore.
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface XDisposable {
    Future<Void> dispose();
}
