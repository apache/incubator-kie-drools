package org.drools.ancompiler;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.reteoo.Rete;
import org.drools.modelcompiler.builder.AdditionalFileGenerator;
import org.drools.modelcompiler.builder.GeneratedFile;
import org.kie.internal.builder.conf.AlphaNetworkCompilerOption;

public class ANCSourceGeneration implements AdditionalFileGenerator {

    @Override
    public List<GeneratedFile> additionalFiles(KnowledgeBuilderConfigurationImpl builderConfiguration, Rete rete) {
        AlphaNetworkCompilerOption ancMode = builderConfiguration.getAlphaNetworkCompilerOption();

        if (AlphaNetworkCompilerOption.COMPILED.equals(ancMode)) {
            return generateSources(rete);
        }

        return Collections.emptyList();
    }

    private List<GeneratedFile> generateSources(Rete rete) {
        Map<String, CompiledNetworkSource> compiledNetworkSourcesMap = ObjectTypeNodeCompiler.compiledNetworkSourceMap(rete);
        return compiledNetworkSourcesMap.values().stream().map(v -> new GeneratedFile(v.getSourceName(), v.getSource())).collect(Collectors.toList());
    }
}
