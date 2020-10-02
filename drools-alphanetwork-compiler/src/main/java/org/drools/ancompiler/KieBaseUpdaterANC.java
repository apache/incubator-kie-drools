package org.drools.ancompiler;

import java.util.Map;

import org.drools.compiler.kie.builder.impl.KieBaseUpdater;
import org.drools.compiler.kie.builder.impl.KieBaseUpdatersContext;
import org.drools.core.reteoo.Rete;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.drools.core.util.MapUtils.mapValues;

public class KieBaseUpdaterANC implements KieBaseUpdater {

    private final KieBaseUpdatersContext ctx;

    public KieBaseUpdaterANC(KieBaseUpdatersContext ctx) {
        this.ctx = ctx;
    }

    public void run() {
        final boolean isAlphaNetworkEnabled = ctx.getKnowledgeBuilderConfiguration().isAlphaNetworkCompilerEnabled();

        // find the new compiled alpha network in the classpath, if it's not there,
        // generate compile it and reattach it
        if(isAlphaNetworkEnabled) {
            inMemoryUpdate(ctx.getClassLoader(), ctx.getRete());
        }
    }

    /**
     * This assumes the kie-memory-compiler module is provided at runtime
     * @param rootClassLoader
     * @param rete
     */
    private void inMemoryUpdate(ClassLoader rootClassLoader, Rete rete) {
        Map<String, CompiledNetworkSource> compiledNetworkSourcesMap = ObjectTypeNodeCompiler.compiledNetworkSourceMap(rete);
        if (!compiledNetworkSourcesMap.isEmpty()) {
            Map<String, Class<?>> compiledClasses = KieMemoryCompiler.compile(mapValues(compiledNetworkSourcesMap, CompiledNetworkSource::getSource),
                                                                              rootClassLoader);
            // No need to clear previous sinks/ANC compiled instances
            // as they are removed by ReteOOBuilder.removeTerminalNode after standard KieBaseUpdaterImpl
            compiledNetworkSourcesMap.values().forEach(c -> {
                Class<?> aClass = compiledClasses.get(c.getName());
                c.setCompiledNetwork(aClass);
            });
        }
    }
}
