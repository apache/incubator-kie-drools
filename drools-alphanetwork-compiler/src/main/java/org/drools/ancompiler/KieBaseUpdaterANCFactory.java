package org.drools.ancompiler;

import org.drools.compiler.kie.builder.impl.KieBaseUpdateContext;
import org.drools.compiler.kie.builder.impl.KieBaseUpdater;
import org.drools.compiler.kie.builder.impl.KieBaseUpdaterFactory;

public class KieBaseUpdaterANCFactory implements KieBaseUpdaterFactory {

    @Override
    public KieBaseUpdater create(KieBaseUpdateContext ctx) {
        return new KieBaseUpdaterANC(ctx);
    }
}
