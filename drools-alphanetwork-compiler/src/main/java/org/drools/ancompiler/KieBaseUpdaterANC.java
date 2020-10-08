package org.drools.ancompiler;

import java.util.Map;
import java.util.Optional;

import org.drools.compiler.kie.builder.impl.KieBaseUpdater;
import org.drools.compiler.kie.builder.impl.KieBaseUpdatersContext;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.kie.api.conf.Option;
import org.kie.internal.builder.conf.AlphaNetworkCompilerOption;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.drools.core.util.MapUtils.mapValues;

public class KieBaseUpdaterANC implements KieBaseUpdater {

    private final KieBaseUpdatersContext ctx;

    public KieBaseUpdaterANC(KieBaseUpdatersContext ctx) {
        this.ctx = ctx;
    }

    public void run() {
        Optional<Option> ancMode = ctx.getOption(AlphaNetworkCompilerOption.class);

        // find the new compiled alpha network in the classpath, if it's not there,
        // generate compile it and reattach it
        if (ancMode.filter(AlphaNetworkCompilerOption.INMEMORY::equals).isPresent()) {
            inMemoryUpdate(ctx.getClassLoader(), ctx.getRete());
        } // load it from the kjar
        else if (ancMode.filter(AlphaNetworkCompilerOption.COMPILED::equals).isPresent()) {
            loadFromKJar(ctx.getClassLoader(), ctx.getRete());
        }
    }

    /**
     * This assumes the kie-memory-compiler module is provided at runtime
     *
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

    /**
     * This assumes the kie-memory-compiler module is provided at runtime
     *
     * @param rootClassLoader
     * @param rete
     */
    private void loadFromKJar(ClassLoader rootClassLoader, Rete rete) {
        Map<ObjectTypeNode, String> compiledNetworkSourcesMap = ObjectTypeNodeCompiler.otnWithBinaryName(rete);
        for (Map.Entry<ObjectTypeNode, String> kv : compiledNetworkSourcesMap.entrySet()) {
            String compiledNetworkClassName = kv.getValue();
            Class<?> aClass = null;
            try {
                aClass = rootClassLoader.loadClass(compiledNetworkClassName);
            } catch (ClassNotFoundException e) {
                // BaseUpdater gets called by the plugin when creating the kiebase, but we don't need to update the rete there
                return;
            }
            CompiledNetwork newInstance = ClazzUtils.newCompiledNetworkInstance(aClass);
            newInstance.setNetwork(kv.getKey());
        }
    }
}
