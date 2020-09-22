package org.drools.ancompiler;

import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.rule.IndexableConstraint;

public class CompiledNetworkSource {

    private final String source;
    private final IndexableConstraint indexableConstraint;
    private final String sourceName;
    private final String binaryName;
    private final String name;
    private final ObjectTypeNode objectTypeNode;

    public CompiledNetworkSource(String source, IndexableConstraint indexableConstraint, String sourceName, String binaryName, String name, ObjectTypeNode objectTypeNode) {
        this.source = source;
        this.indexableConstraint = indexableConstraint;
        this.sourceName = sourceName;
        this.binaryName = binaryName;
        this.name = name;
        this.objectTypeNode = objectTypeNode;
    }

    public String getSource() {
        return source;
    }

    public IndexableConstraint getIndexableConstraint() {
        return indexableConstraint;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getBinaryName() {
        return binaryName;
    }

    public String getName() {
        return name;
    }

    public void setCompiledNetwork(CompiledNetwork compiledNetwork) {
        ((CompiledObjectTypeNode) objectTypeNode).setCompiledNetwork(compiledNetwork);
    }


}
