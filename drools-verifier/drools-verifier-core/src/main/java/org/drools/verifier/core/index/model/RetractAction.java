package org.drools.verifier.core.index.model;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Values;

public class RetractAction
        extends Action {

    public RetractAction(final Column column,
                         final Values values,
                         final AnalyzerConfiguration configuration) {
        super(column,
              ActionSuperType.RETRACT_ACTION,
              values,
              configuration);
    }
}
