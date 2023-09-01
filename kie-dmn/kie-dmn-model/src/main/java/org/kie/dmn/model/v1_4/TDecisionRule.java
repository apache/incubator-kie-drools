package org.kie.dmn.model.v1_4;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.DecisionRule;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.RuleAnnotation;
import org.kie.dmn.model.api.UnaryTests;

public class TDecisionRule extends TDMNElement implements DecisionRule {

    protected List<UnaryTests> inputEntry;
    protected List<LiteralExpression> outputEntry;
    protected List<RuleAnnotation> annotationEntry;

    @Override
    public List<UnaryTests> getInputEntry() {
        if (inputEntry == null) {
            inputEntry = new ArrayList<>();
        }
        return this.inputEntry;
    }

    @Override
    public List<LiteralExpression> getOutputEntry() {
        if (outputEntry == null) {
            outputEntry = new ArrayList<>();
        }
        return this.outputEntry;
    }

    @Override
    public List<RuleAnnotation> getAnnotationEntry() {
        if (annotationEntry == null) {
            annotationEntry = new ArrayList<>();
        }
        return this.annotationEntry;
    }

}
