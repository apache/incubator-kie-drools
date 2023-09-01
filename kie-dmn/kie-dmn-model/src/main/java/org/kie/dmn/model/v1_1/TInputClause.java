package org.kie.dmn.model.v1_1;

import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.UnaryTests;

public class TInputClause extends TDMNElement implements InputClause {

    private LiteralExpression inputExpression;
    private UnaryTests inputValues;

    @Override
    public LiteralExpression getInputExpression() {
        return inputExpression;
    }

    @Override
    public void setInputExpression(final LiteralExpression value) {
        this.inputExpression = value;
    }

    @Override
    public UnaryTests getInputValues() {
        return inputValues;
    }

    @Override
    public void setInputValues(final UnaryTests value) {
        this.inputValues = value;
    }

}
