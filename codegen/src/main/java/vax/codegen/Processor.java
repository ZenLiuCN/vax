package vax.codegen;

import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface Processor {

    Set<Class<? extends Annotation>> accepted();

    default int order() {
        return Integer.MAX_VALUE;
    }

    /**
     * @param skip  have done processing (any false is false)
     * @param util  the apt utilities
     * @param types the types
     * @return have done processing
     */
    boolean handle(boolean skip, Util util, Set<? extends TypeElement> types);
}
