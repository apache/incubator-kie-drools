package org.drools.modelcompiler.attributes;

import org.drools.base.base.ValueResolver;
import org.drools.base.rule.accessor.Salience;
import org.drools.core.reteoo.Tuple;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.model.DynamicValueSupplier;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.Match;

import static org.drools.modelcompiler.consequence.LambdaConsequence.declarationsToFacts;

public class LambdaSalience extends DynamicAttributeEvaluator<Integer> implements Salience {

    public LambdaSalience( DynamicValueSupplier<Integer> supplier ) {
        super(supplier);
    }

    @Override
    public int getValue(Match match, Rule rule, ValueResolver valueResolver) {
        Tuple tuple = ((InternalMatch)match).getTuple();
        Object[] facts = declarationsToFacts(valueResolver, tuple, getDeclarations(tuple), supplier.getVariables() );
        return supplier.supply( facts );
    }

    @Override
    public int getValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
