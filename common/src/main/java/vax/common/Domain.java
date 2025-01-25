package vax.common;

import java.util.List;

/**
 * A dominator (Dom) is a root actor (verticle) for a domain.<br/>
 * A dominator will spawn Actors when received requests.<br/>
 *
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Domain extends Element {
    @Override
    Class<? extends Domain> $type();

    /**
     * other dom this is requiring.
     */
    default List<Class<? extends Domain>> $requirement() {
        return List.of();
    }
}
