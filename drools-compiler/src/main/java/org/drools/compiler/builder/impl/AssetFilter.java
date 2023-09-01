package org.drools.compiler.builder.impl;

import org.kie.internal.builder.ResourceChange;

public interface AssetFilter {

    enum Action {
        DO_NOTHING,
        ADD,
        REMOVE,
        UPDATE
    }

    Action accept(ResourceChange.Type type, String pkgName, String assetName);
}
