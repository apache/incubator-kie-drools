package org.drools.ancompiler;

import org.drools.base.base.ValueType;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;

/**
 * This class is used to hold information for Hashed {@link org.kie.reteoo.AlphaNode}s for generated subclasses
 * of {@link CompiledNetwork}.
 *
 * @see org.kie.reteoo.compiled.DeclarationsHandler
 */
public class HashedAlphasDeclaration {
    private final String variableName;
    private final ValueType valueType;

    /**
     * This map contains keys which are different values of the same field and the node id that of the
     * {@link org.kie.common.NetworkNode} the value is from.
     */
    private final Map<Object, String> hashedValuesToNodeIds = new HashMap<>();

    HashedAlphasDeclaration(String variableName,ValueType valueType) {
        this.variableName = variableName;
        this.valueType = valueType;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public String getVariableName() {
        return variableName;
    }

    void add(Object hashedValue, String nodeId) {
        hashedValuesToNodeIds.put(hashedValue, nodeId);
    }

    public Collection<Object> getHashedValues() {
        return Collections.unmodifiableSet(hashedValuesToNodeIds.keySet());
    }

    public String getNodeId(Object hashedValue) {
        return hashedValuesToNodeIds.get(hashedValue);
    }
}
