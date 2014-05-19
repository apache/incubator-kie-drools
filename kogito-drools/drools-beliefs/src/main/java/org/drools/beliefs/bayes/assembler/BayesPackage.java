package org.drools.beliefs.bayes.assembler;

import org.drools.beliefs.bayes.JunctionTree;
import org.kie.internal.io.ResourceTypePackage;
import org.kie.api.io.ResourceType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BayesPackage implements ResourceTypePackage {
    private Map<String, JunctionTree> trees;

    public BayesPackage() {
        trees = new HashMap<String, JunctionTree>();
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
}
