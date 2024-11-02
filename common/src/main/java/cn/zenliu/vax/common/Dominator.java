package cn.zenliu.vax.common;

import java.util.List;

/**
 * A dominator (Dom) is a root actor (verticle) for a domain.<br/>
 * A dominator will spawn Actors when received requests.<br/>
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Dominator extends Activity {
    @Override
    Class<? extends Dominator> $type();

    /**
     * other dom this is requiring.
     */
    default List<Class<? extends Dominator>> $requires() {
        return List.of();
    }
}
