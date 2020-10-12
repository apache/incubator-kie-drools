package org.drools.ancompiler;

import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.spi.InternalReadAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompiledNetworkSource {

    private final Logger logger = LoggerFactory.getLogger(KieBaseUpdaterANC.class);

    private final String source;
    private final IndexableConstraint indexableConstraint;
    private final String name;
    private final ObjectTypeNode objectTypeNode;

    public CompiledNetworkSource(String source,
                                 IndexableConstraint indexableConstraint,
                                 String name,
                                 ObjectTypeNode objectTypeNode) {
        this.source = source;
        this.indexableConstraint = indexableConstraint;
        this.name = name;
        this.objectTypeNode = objectTypeNode;
    }

    public String getSource() {
        return source;
    }

    public String getName() {
        return name;
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
