package org.optaplanner.quarkus.deployment;

import io.quarkus.builder.item.SimpleBuildItem;

public final class DetermineIfNativeBuildItem extends SimpleBuildItem {
    private final boolean isNative;

    public DetermineIfNativeBuildItem(boolean isNative) {
        this.isNative = isNative;
    }

    public boolean isNative() {
        return isNative;
    }
}
