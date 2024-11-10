package cn.zenliu.vax.common;

import java.util.List;

/**
 * A dominator (Dom) is a root actor (verticle) for a domain.<br/>
 * A dominator will spawn Actors when received requests.<br/>
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface XDomain extends XActivity {
    @Override
    Class<? extends XDomain> $type();

    /**
     * other dom this is requiring.
     */
    default List<Class<? extends XDomain>> $dependencies() {
        return List.of();
    }
}
