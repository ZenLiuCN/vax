package cn.zenliu.vax.codegen;

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
    record U(
            ProcessingEnvironment procEnv,
            RoundEnvironment roundEnv,
            Elements elements,
            Types types,
            Messager messager,
            Filer filer
    ) {
        U(ProcessingEnvironment procEnv, RoundEnvironment roundEnv) {
            this(procEnv,
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
    boolean handle(boolean skip, U unit, Set<? extends TypeElement> types);
}
