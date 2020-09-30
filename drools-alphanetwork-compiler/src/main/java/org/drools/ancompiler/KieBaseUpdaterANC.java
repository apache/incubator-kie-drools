package org.drools.ancompiler;

import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.kie.builder.impl.KieBaseUpdateContext;
import org.drools.compiler.kie.builder.impl.KieBaseUpdater;
import org.drools.core.reteoo.Rete;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.drools.core.util.MapUtils.mapValues;

public class KieBaseUpdaterANC extends KieBaseUpdater {

    KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration;

    public KieBaseUpdaterANC(KnowledgeBuilderConfigurationImpl knowledgeBuilderConfiguration, KieBaseUpdateContext ctx) {
        super(ctx);
        this.knowledgeBuilderConfiguration = knowledgeBuilderConfiguration;
    }

    public void run() {
        final boolean isAlphaNetworkEnabled = knowledgeBuilderConfiguration.isAlphaNetworkCompilerEnabled();

        // find the new compiled alpha network in the classpath, if it's not there,
        // generate compile it and reattach it
        if(isAlphaNetworkEnabled) {
            inMemoryUpdate();
        }
    }

    /**
     * This assumes the kie-memory-compiler module is provided at runtime
     */
    private void inMemoryUpdate() {
        Rete rete = ctx.kBase.getRete();
        Map<String, CompiledNetworkSource> compiledNetworkSourcesMap = ObjectTypeNodeCompiler.compiledNetworkSourceMap(rete);
        if (!compiledNetworkSourcesMap.isEmpty()) {
            Map<String, Class<?>> compiledClasses = KieMemoryCompiler.compile(mapValues(compiledNetworkSourcesMap, CompiledNetworkSource::getSource),
                                                                              ctx.kBase.getRootClassLoader());
            // No need to clear previous sinks/ANC compiled instances
            // as they are removed by ReteOOBuilder.removeTerminalNode after standard KieBaseUpdaterImpl
            compiledNetworkSourcesMap.values().forEach(c -> {
                Class<?> aClass = compiledClasses.get(c.getName());
                c.setCompiledNetwork(aClass);
            });
        }
    }
}
