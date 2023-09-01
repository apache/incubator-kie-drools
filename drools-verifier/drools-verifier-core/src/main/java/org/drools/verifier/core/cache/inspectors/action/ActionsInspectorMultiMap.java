package org.drools.verifier.core.cache.inspectors.action;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.maps.InspectorMultiMap;

public class ActionsInspectorMultiMap<GroupBy extends Comparable>
        extends InspectorMultiMap<GroupBy, ActionInspector> {

    public ActionsInspectorMultiMap(final AnalyzerConfiguration configuration) {
        super(configuration);
    }
}
