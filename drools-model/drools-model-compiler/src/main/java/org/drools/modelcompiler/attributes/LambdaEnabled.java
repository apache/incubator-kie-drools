package org.drools.modelcompiler.attributes;

import org.drools.base.base.ValueResolver;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.Enabled;
import org.drools.core.reteoo.Tuple;
import org.drools.model.DynamicValueSupplier;

import static org.drools.modelcompiler.consequence.LambdaConsequence.declarationsToFacts;

public class LambdaEnabled extends DynamicAttributeEvaluator<Boolean> implements Enabled {

    public LambdaEnabled( DynamicValueSupplier<Boolean> supplier ) {
        super( supplier );
    }

    @Override
    public boolean getValue(BaseTuple tuple, Declaration[] declrs, RuleImpl rule, ValueResolver valueResolver) {
        Object[] facts = declarationsToFacts(valueResolver, tuple, getDeclarations((Tuple) tuple), supplier.getVariables() );
        return supplier.supply( facts );
    }
}
