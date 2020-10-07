package org.drools.ancompiler;

import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.spi.InternalReadAccessor;

public class CompiledNetworkSource {

    private final String source;
    private final IndexableConstraint indexableConstraint;
    private final String sourceName;
    private final String binaryName;
    private final String name;
    private final ObjectTypeNode objectTypeNode;

    public CompiledNetworkSource(String source, IndexableConstraint indexableConstraint, String sourceName, String binaryName, String name, ObjectTypeNode objectTypeNode, ObjectTypeNodeCompiler objectTypeNodeCompiler) {
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

    public String getSourceName() {
        return sourceName;
    }

    public String getName() {
        return name;
    }

    public void setCompiledNetwork(Class<?> compiledNetworkClass) {
        CompiledNetwork compiledNetwork = newCompiledNetworkInstance(compiledNetworkClass);
        compiledNetwork.setNetwork(objectTypeNode);
    }

    public CompiledNetwork newCompiledNetworkInstance(Class<?> aClass) {
        try {
            return (CompiledNetwork) aClass.getDeclaredConstructor(org.drools.core.spi.InternalReadAccessor.class)
                    .newInstance(getFieldExtractor());
        } catch (Exception e) { // TODO LUCA
            throw new RuntimeException(e);
        }
    }

    private InternalReadAccessor getFieldExtractor() {
        return indexableConstraint == null ? null : indexableConstraint.getFieldExtractor();
    }
}
