package org.drools.modelcompiler.attributes;

import org.drools.base.rule.Declaration;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.Tuple;
import org.drools.model.DynamicValueSupplier;

public class DynamicAttributeEvaluator<T> {
    protected final DynamicValueSupplier<T> supplier;

    public DynamicAttributeEvaluator( DynamicValueSupplier<T> supplier ) {
        this.supplier = supplier;
    }

    protected Declaration[] getDeclarations(Tuple tuple) {
        Declaration[] declarations = new Declaration[supplier.getVariables().length];
        Declaration[] allDeclarations = ((RuleTerminalNode) tuple.getTupleSink()).getAllDeclarations();
        for (int i = 0; i < supplier.getVariables().length; i++) {
            for (Declaration d : allDeclarations) {
                if (d.getIdentifier().equals(supplier.getVariables()[i].getName())) {
                    declarations[i] = d;
                    break;
                }
            }
        }
        return declarations;
    }
}
