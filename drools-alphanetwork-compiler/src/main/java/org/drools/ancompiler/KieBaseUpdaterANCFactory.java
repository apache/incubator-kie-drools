package org.drools.ancompiler;

import org.drools.compiler.kie.builder.impl.KieBaseUpdater;
import org.drools.compiler.kie.builder.impl.KieBaseUpdaterFactory;
import org.drools.compiler.kie.builder.impl.KieBaseUpdatersContext;

public class KieBaseUpdaterANCFactory implements KieBaseUpdaterFactory {

    @Override
    public KieBaseUpdater create(KieBaseUpdatersContext ctx) {
        return new KieBaseUpdaterANC(ctx);
    }
}
