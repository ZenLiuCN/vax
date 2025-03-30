package vax.codegen;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Zen.Liu
 * @since 2024-10-01
 */
@AutoService(javax.annotation.processing.Processor.class)
public class GenerateProcessor extends AbstractProcessor {
    @Override
    public Set<String> getSupportedOptions() {
        return Set.of("debug");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return apt.stream()
                .flatMap(v -> v.accepted().stream())
                .map(Class::getCanonicalName)
                .collect(Collectors.toSet());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    final List<Processor> apt = ServiceLoader.load(Processor.class)
                                             .stream().map(ServiceLoader.Provider::get)
                                             .distinct()
                                             .sorted(Comparator.comparing(Processor::order))
                                             .toList();
    private volatile ProcessingEnvironment processingEnv;

    @Override
    public void init(ProcessingEnvironment processingEnvironment) {
        processingEnv = processingEnvironment;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        var skip = true;
        for (var proc : apt) {
            skip = skip && proc.handle(skip, new Util(processingEnv, roundEnvironment), set);
        }
        return skip;
    }


}
