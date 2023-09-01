package org.kie.internal.builder;

import java.util.function.Predicate;

import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.model.KieModuleModel;

public interface InternalKieBuilder extends KieBuilder {

    KieBuilderSet createFileSet(String... files);
    KieBuilderSet createFileSet(Message.Level minimalLevel, String... files);

    KieModule getKieModuleIgnoringErrors();

    IncrementalResults incrementalBuild();

    /**
     * Builds all the KieBases contained in the KieModule for which this KieBuilder has been created
     * @param classFilter Used to prevent compilation of Java source files.
     *          This filter will be tested on all source file names before they are compiled.
     *          Only source files for which the filter returns true will be compiled.
     */
    KieBuilder buildAll(Predicate<String> classFilter);

    InternalKieBuilder withKModuleModel( KieModuleModel kModuleModel );
}
