package vax.codegen;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author Zen.Liu
 * @since 2024-10-01
 */
public interface APT {
    record Unit(
            boolean debug,
            ProcessingEnvironment procEnv,
            RoundEnvironment roundEnv,
            Elements elements,
            Types types,
            Messager messager,
            Filer filer
    ) {
        Unit(ProcessingEnvironment procEnv, RoundEnvironment roundEnv) {
            this(procEnv.getOptions().containsKey("debug"),
                    procEnv,
                    roundEnv,
                    procEnv.getElementUtils(),
                    procEnv.getTypeUtils(),
                    procEnv.getMessager(),
                    procEnv.getFiler()
            );
        }
    }

    Set<Class<? extends Annotation>> accepted();

    default int order() {
        return Integer.MAX_VALUE;
    }

    /**
     * @param skip  have done processing (any false is false)
     * @param unit  the apt utilities
     * @param types the types
     * @return have done processing
     */
    boolean handle(boolean skip, Unit unit, Set<? extends TypeElement> types);
}
