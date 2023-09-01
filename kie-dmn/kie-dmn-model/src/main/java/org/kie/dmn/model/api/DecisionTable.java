package org.kie.dmn.model.api;

import java.util.List;

public interface DecisionTable extends Expression {

    List<InputClause> getInput();

    List<OutputClause> getOutput();

    List<DecisionRule> getRule();

    HitPolicy getHitPolicy();

    void setHitPolicy(HitPolicy value);

    BuiltinAggregator getAggregation();

    void setAggregation(BuiltinAggregator value);

    DecisionTableOrientation getPreferredOrientation();

    void setPreferredOrientation(DecisionTableOrientation value);

    String getOutputLabel();

    void setOutputLabel(String value);

    List<RuleAnnotationClause> getAnnotation();

}
