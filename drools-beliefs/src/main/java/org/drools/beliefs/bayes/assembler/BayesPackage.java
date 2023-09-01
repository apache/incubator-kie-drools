package org.drools.beliefs.bayes.assembler;

import org.drools.beliefs.bayes.JunctionTree;
import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.api.io.ResourceType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BayesPackage implements ResourceTypePackage<JunctionTree> {
    private Map<String, JunctionTree> trees;
    private String namespace;

    public BayesPackage(String namespace) {
        this.trees = new HashMap<>();
        this.namespace = namespace;
    }

    public Collection<String> listJunctionTrees() {
        return trees.keySet();
    }

    public void addJunctionTree(String name, JunctionTree tree) {
        trees.put( name, tree );
    }
    public JunctionTree getJunctionTree(String name) {
        return trees.get( name );
    }

    public void removeJunctionTree(String name) {
        trees.remove( name );
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.BAYES;
    }

    @Override
    public void add(JunctionTree processedResource) {
        addJunctionTree(processedResource.getName(), processedResource);
    }

    @Override
    public Iterator<JunctionTree> iterator() {
        return trees.values().iterator();
    }
}
