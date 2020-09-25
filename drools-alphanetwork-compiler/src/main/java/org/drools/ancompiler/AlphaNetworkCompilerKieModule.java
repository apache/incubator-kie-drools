package org.drools.ancompiler;

import org.drools.compiler.kie.builder.impl.KieBaseUpdateContext;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;

public class AlphaNetworkCompilerKieModule extends MemoryKieModule {

    @Override
    public Runnable createKieBaseUpdater(KieBaseUpdateContext context) {
        return new CompiledAlphaNetworkUpdater(context);
    }
}
