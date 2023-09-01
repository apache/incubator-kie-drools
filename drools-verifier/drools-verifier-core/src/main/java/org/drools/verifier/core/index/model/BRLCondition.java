package org.drools.verifier.core.index.model;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Values;

public class BRLCondition
        extends Condition<Comparable> {

    public BRLCondition(final Column column,
                        final Values values,
                        final AnalyzerConfiguration configuration) {
        super(column,
              ConditionSuperType.BRL_CONDITION,
              values,
              configuration);
    }
}
