package org.drools.verifier.core.cache.inspectors.action;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.BRLAction;

public class BRLActionInspector
        extends ActionInspector {

    public BRLActionInspector(final BRLAction action,
                              final AnalyzerConfiguration configuration) {
        super(action,
              configuration);
    }
}
