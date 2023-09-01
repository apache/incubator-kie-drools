package org.drools.verifier.core.cache.inspectors.condition;

import java.util.Iterator;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.BRLCondition;

public class BRLConditionInspector
        extends ConditionInspector {

    public BRLConditionInspector(final BRLCondition brlCondition,
                                 final AnalyzerConfiguration configuration) {
        super(brlCondition,
              configuration);
    }

    @Override
    public String toHumanReadableString() {

        final StringBuilder builder = new StringBuilder();

        final Iterator iterator = getValues().iterator();

        while (iterator.hasNext()) {
            builder.append(iterator.next()
                                   .toString());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }

    @Override
    public boolean conflicts(final Object other) {
        if (other instanceof BRLConditionInspector) {
            return !getValues().containsAll(((BRLConditionInspector) other).getValues());
        } else {
            return false;
        }
    }

    @Override
    public boolean overlaps(final Object other) {
        if (other instanceof BRLConditionInspector) {
            return ((BRLConditionInspector) other).getValues()
                    .containsAny(getValues());
        } else {
            return false;
        }
    }

    @Override
    public boolean subsumes(final Object other) {
        if (other instanceof BRLConditionInspector) {
            return ((BRLConditionInspector) other).getValues()
                    .containsAll(getValues());
        } else {
            return false;
        }
    }
}
