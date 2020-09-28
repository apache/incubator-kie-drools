package org.drools.compiler.kie.builder.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UpdaterContainerContainerImpl implements Consumer<KieBaseUpdater>,
                                                      KieBaseUpdaters {

    List<KieBaseUpdater> children = new ArrayList<>();

    public UpdaterContainerContainerImpl() {
    }

    @Override
    public List<KieBaseUpdater> getChildren() {
        return children;
    }

    @Override
    public void accept(KieBaseUpdater o) {
        children.add(o);
    }
}
