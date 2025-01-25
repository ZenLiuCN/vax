package vax.codegen;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author Zen.Liu
 * @since 2025-01-25
 */
public
record Util(
        boolean debug,
        ProcessingEnvironment procEnv,
        RoundEnvironment roundEnv,
        Elements elements,
        Types types,
        Messager messager,
        Filer filer
) implements Utility{
    Util(ProcessingEnvironment procEnv, RoundEnvironment roundEnv) {
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
