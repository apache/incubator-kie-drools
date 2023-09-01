package org.drools.verifier.core.index.model;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Values;

public class BRLAction
        extends Action {

    public BRLAction(final Column column,
                     final Values values,
                     final AnalyzerConfiguration configuration) {
        super(column,
              ActionSuperType.BRL_ACTION,
              values,
              configuration);
    }
}
