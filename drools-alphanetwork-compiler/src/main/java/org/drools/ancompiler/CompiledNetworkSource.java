package org.drools.ancompiler;

import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.spi.InternalReadAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompiledNetworkSource {

    private final Logger logger = LoggerFactory.getLogger(CompiledNetworkSource.class);

    private final String source;
    private final IndexableConstraint indexableConstraint;
    private final String name;
    private final String sourceName;
    private final ObjectTypeNode objectTypeNode;

    public CompiledNetworkSource(String source,
                                 IndexableConstraint indexableConstraint,
                                 String name,
                                 String sourceName,
                                 ObjectTypeNode objectTypeNode) {
        this.source = source;
        this.indexableConstraint = indexableConstraint;
        this.name = name;
        this.sourceName = sourceName;
        this.objectTypeNode = objectTypeNode;
    }

    public String getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setCompiledNetwork(Class<?> compiledNetworkClass) {
        CompiledNetwork compiledNetwork = newCompiledNetworkInstance(compiledNetworkClass);
        compiledNetwork.setNetwork(objectTypeNode);
        logger.debug("Updating {} with instance of class: {}",
                     objectTypeNode,
                     compiledNetworkClass.getName());
    }

    public CompiledNetwork newCompiledNetworkInstance(Class<?> aClass) {
        try {
            return (CompiledNetwork) aClass.getDeclaredConstructor(org.drools.core.spi.InternalReadAccessor.class)
                    .newInstance(getFieldExtractor());
        } catch (Exception e) {
            throw new CouldNotCreateAlphaNetworkCompilerException(e);
        }
    }

    private InternalReadAccessor getFieldExtractor() {
        return indexableConstraint == null ? null : indexableConstraint.getFieldExtractor();
    }
}
