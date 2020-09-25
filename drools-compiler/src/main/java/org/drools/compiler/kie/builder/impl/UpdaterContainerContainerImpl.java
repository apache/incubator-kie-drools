package org.drools.compiler.kie.builder.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UpdaterContainerContainerImpl implements Consumer<Updater>,
                                                      UpdaterContainer {

    List<Updater> children = new ArrayList<>();

    public UpdaterContainerContainerImpl() {
    }

    @Override
    public List<Updater> getChildren() {
        return children;
    }

    @Override
    public void accept(Updater o) {
        children.add(o);
    }
}
