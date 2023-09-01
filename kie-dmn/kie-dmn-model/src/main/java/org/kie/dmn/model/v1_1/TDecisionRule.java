package org.kie.dmn.model.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.RuleAnnotation;
import org.kie.dmn.model.api.UnaryTests;

public class TDecisionRule extends TDMNElement implements DecisionRule {

    private List<UnaryTests> inputEntry;
    private List<LiteralExpression> outputEntry;

    @Override
    public List<UnaryTests> getInputEntry() {
        if ( inputEntry == null ) {
            inputEntry = new ArrayList<>();
        }
        return this.inputEntry;
    }

    @Override
    public List<LiteralExpression> getOutputEntry() {
        if ( outputEntry == null ) {
            outputEntry = new ArrayList<>();
        }
        return this.outputEntry;
    }

    @Override
    public List<RuleAnnotation> getAnnotationEntry() {
        throw new UnsupportedOperationException("Not on DMN v1.1");
    }

}
