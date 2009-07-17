package org.drools.reteoo.compiled;

import org.drools.base.ValueType;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;

/**
 * This class is used to hold information for Hashed {@link org.drools.reteoo.AlphaNode}s for generated subclasses
 * of {@link CompiledNetwork}.
 *
 * @see org.drools.reteoo.compiled.DeclarationsHandler
 */
class HashedAlphasDeclaration {
    private final String variableName;
    private final ValueType valueType;

    /**
     * This map contains keys which are different values of the same field and the node id that of the
     * {@link org.drools.common.NetworkNode} the value is from.
     */
    private final Map<Object, String> hashedValuesToNodeIds = new HashMap<Object, String>();

    HashedAlphasDeclaration(String variableName,ValueType valueType) {
        this.variableName = variableName;
        this.valueType = valueType;
    }

    ValueType getValueType() {
        return valueType;
    }

    String getVariableName() {
        return variableName;
    }

    void add(Object hashedValue, String nodeId) {
        hashedValuesToNodeIds.put(hashedValue,  nodeId);
    }

    Collection<Object> getHashedValues() {
        return Collections.unmodifiableSet(hashedValuesToNodeIds.keySet());
    }

    String getNodeId(Object hashedValue) {
        return hashedValuesToNodeIds.get(hashedValue);
    }
}
