package org.drools.compiler.kie.builder.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ServiceLoader;

public class KieBaseUpdatersImpl implements KieBaseUpdaters {

    private volatile Collection<KieBaseUpdaterFactory> children;

    @Override
    public Collection<KieBaseUpdaterFactory> getChildren() {
        if (children == null) {
            synchronized (this) {
                if (children == null) {
                    children = new ArrayList<>();
                    ServiceLoader<KieBaseUpdaterFactory> loader = ServiceLoader.load(KieBaseUpdaterFactory.class);
                    for (KieBaseUpdaterFactory kieBaseUpdaterFactory : loader) {
                        children.add(kieBaseUpdaterFactory);
                    }
                }
            }
        }
        return children;
    }
}
