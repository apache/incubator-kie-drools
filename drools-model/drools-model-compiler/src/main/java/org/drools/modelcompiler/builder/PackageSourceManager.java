package org.drools.modelcompiler.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PackageSourceManager<T> {

    private final Map<String, T> packageSources = new HashMap<>();

    public void put(String name, T generated) {
        packageSources.put( name, generated );
    }

    public Collection<T> getPackageSources() {
        return packageSources.values();
    }

    public T getPackageSource(String packageName) {
        return packageSources.get(packageName);
    }

    public Collection<T> values() {
        return packageSources.values();
    }

    public T get(String packageName) {
        return packageSources.get(packageName);
    }
}
