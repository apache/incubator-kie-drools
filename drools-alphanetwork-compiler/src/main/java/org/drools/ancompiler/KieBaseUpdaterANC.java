package org.drools.ancompiler;

import java.util.Map;

import org.drools.compiler.kie.builder.impl.KieBaseUpdateContext;
import org.drools.compiler.kie.builder.impl.KieBaseUpdater;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.core.reteoo.Rete;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.drools.core.util.MapUtils.mapValues;

public class KieBaseUpdaterANC extends KieBaseUpdater {

    public KieBaseUpdaterANC(KieBaseUpdateContext ctx) {
        super(ctx);
    }

    public void run() {

        // TODO LUCA fix configuration
        final String configurationProperty = ctx.newKieBaseModel.getKModule().getConfigurationProperty(KieContainerImpl.ALPHA_NETWORK_COMPILER_OPTION);
        final boolean isAlphaNetworkEnabled = true;

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
