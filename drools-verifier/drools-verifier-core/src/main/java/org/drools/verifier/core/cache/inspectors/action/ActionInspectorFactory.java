package org.drools.verifier.core.cache.inspectors.action;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.FieldAction;
import org.drools.verifier.core.maps.InspectorFactory;

public class ActionInspectorFactory
        extends InspectorFactory<ActionInspector, Action> {

    public ActionInspectorFactory(final AnalyzerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public ActionInspector make(final Action action) {
        if (action instanceof FieldAction) {
            return new FieldActionInspector((FieldAction) action,
                                            configuration);
        } else {
            return null;
        }
    }
}
