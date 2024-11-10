package cn.zenliu.vax.common;

import io.vertx.core.Future;


/**
 * An ability is a group of actions related to an actor of one Domain.
 * The entry should always be the actor itself.
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface XAbility extends XEntry, XElement {
    @Override
    Class<? extends XAbility> $type();

    /**
     * @return the simple refer of presents actor
     */
    Future<XActor> $actor();

}
