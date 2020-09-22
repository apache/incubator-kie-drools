package org.drools.ancompiler;

import org.drools.compiler.builder.InternalKnowledgeBuilder;
import org.drools.compiler.kie.builder.impl.KieBaseUpdateContext;
import org.drools.compiler.kie.builder.impl.KieBaseUpdater;
import org.drools.core.InitialFact;

// TODO LUCA use this instead of KieBaseUpdater when ANC is enabled
/*
final String configurationProperty = ctx.newKieBaseModel.getKModule().getConfigurationProperty(KieContainerImpl.ALPHA_NETWORK_COMPILER_OPTION);
        final boolean isAlphaNetworkEnabled = Boolean.valueOf(configurationProperty);

 */
public class CompiledAlphaNetworkUpdater extends KieBaseUpdater {

    public CompiledAlphaNetworkUpdater(KieBaseUpdateContext ctx) {
        super(ctx);
    }

    @Override
    public void afterUpdate(InternalKnowledgeBuilder kbuilder) {
        updateCompiledAlphaNetwork(kbuilder);
    }

    public void updateCompiledAlphaNetwork(InternalKnowledgeBuilder kbuilder) {
        ctx.kBase.getRete().getEntryPointNodes().values().stream()
                .flatMap(ep -> ep.getObjectTypeNodes().values().stream())
                .filter(f -> !InitialFact.class.isAssignableFrom(f.getObjectType().getClassType()))
                .forEach(otn -> {
//                    CompiledObjectTypeNode cotn = (CompiledObjectTypeNode)otn;
//                    final CompiledNetwork oldCompiledNetwork = cotn.getCompiledNetwork();
//                    if (oldCompiledNetwork != null) {
//                        clearInstancesOfModifiedClass(oldCompiledNetwork.getClass());
//                    }

                    // TODO Luca fix this
//                    final CompiledNetwork compile = ObjectTypeNodeCompiler.compile(kbuilder, otn);
//                    cotn.setCompiledNetwork(compile);
                });
    }
}
