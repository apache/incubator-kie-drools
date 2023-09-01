package org.drools.verifier.core.cache.inspectors;

import org.drools.verifier.core.cache.inspectors.action.ActionsInspectorMultiMap;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Field;

public class FieldActionsInspectorMultiMap
        extends ActionsInspectorMultiMap<Field> {

    public FieldActionsInspectorMultiMap(final AnalyzerConfiguration configuration) {
        super(configuration);
    }
}
