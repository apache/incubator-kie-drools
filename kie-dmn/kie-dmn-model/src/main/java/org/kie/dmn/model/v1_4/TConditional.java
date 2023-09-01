package org.kie.dmn.model.v1_4;

import org.kie.dmn.model.api.ChildExpression;
import org.kie.dmn.model.api.Conditional;

public class TConditional extends TExpression implements Conditional {

    private ChildExpression ifExp;
    private ChildExpression thenExp;
    private ChildExpression elseExp;

    @Override
    public ChildExpression getIf() {
        return ifExp;
    }

    @Override
    public ChildExpression getThen() {
        return thenExp;
    }

    @Override
    public ChildExpression getElse() {
        return elseExp;
    }

    @Override
    public void setIf(ChildExpression value) {
        this.ifExp = value;
    }

    @Override
    public void setThen(ChildExpression value) {
        this.thenExp = value;
    }

    @Override
    public void setElse(ChildExpression value) {
        this.elseExp = value;
    }

}
