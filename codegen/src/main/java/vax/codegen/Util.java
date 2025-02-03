package vax.codegen;

import vax.codegen.utilties.Utility;

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
        RoundEnvironment roundEnv

) implements Utility {
    Util(ProcessingEnvironment procEnv, RoundEnvironment roundEnv) {
        this(procEnv.getOptions().containsKey("debug"),
             procEnv,
             roundEnv
            );
    }
}
