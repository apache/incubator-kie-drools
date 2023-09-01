package org.kie.dmn.core.compiler.alphanetbased;

import org.drools.base.base.ClassObjectType;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.model.Variable;

import static org.drools.model.DSL.declarationOf;

public class AlphaNetworkEvaluationContext {

    private final Variable<PropertyEvaluator> variable;
    private final Declaration declaration;
    private final Results results;

    public AlphaNetworkEvaluationContext(Results results) {
        ClassObjectType objectType = new ClassObjectType(PropertyEvaluator.class);
        variable = declarationOf(PropertyEvaluator.class, "$ctx");

        Pattern pattern = new Pattern(1, objectType, "$ctx");
        declaration = pattern.getDeclaration();

        this.results = results;
    }

    public Variable<PropertyEvaluator> getVariable() {
        return variable;
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    public Results getResultCollector() {
        return results;
    }
}
