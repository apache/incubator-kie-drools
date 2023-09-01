package org.drools.traits.core.factmodel;

public interface TraitRegistry {

    HierarchyEncoder<String> getHierarchy();

    void merge(TraitRegistry other);
}
