package org.kie.dmn.model.api;

import java.util.List;

public interface DecisionRule extends DMNElement {

    List<UnaryTests> getInputEntry();

    List<LiteralExpression> getOutputEntry();

    /**
     * @since DMN v1.2
     */
    List<RuleAnnotation> getAnnotationEntry();

}
