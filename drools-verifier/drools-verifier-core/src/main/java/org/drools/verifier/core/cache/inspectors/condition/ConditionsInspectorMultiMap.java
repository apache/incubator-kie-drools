package org.drools.verifier.core.cache.inspectors.condition;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.ObjectField;
import org.drools.verifier.core.maps.InspectorMultiMap;

public class ConditionsInspectorMultiMap
        extends InspectorMultiMap<ObjectField, ConditionInspector> {

    public ConditionsInspectorMultiMap(final AnalyzerConfiguration configuration) {
        super(configuration);
    }
}
