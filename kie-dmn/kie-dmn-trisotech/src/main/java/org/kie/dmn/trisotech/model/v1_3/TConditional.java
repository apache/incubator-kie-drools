package org.kie.dmn.trisotech.model.v1_3;

import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.v1_3.TExpression;
import org.kie.dmn.trisotech.model.api.Conditional;

public class TConditional extends TExpression implements Conditional {

    private Expression ifExp;
    private Expression thenExp;
    private Expression elseExp;

    @Override
    public Expression getIf() {
        return ifExp;
    }

    @Override
    public Expression getThen() {
        return thenExp;
    }

    @Override
    public Expression getElse() {
        return elseExp;
    }

    @Override
    public void setIf(Expression expr) {
        this.ifExp = expr;
    }

    @Override
    public void setThen(Expression expr) {
        this.thenExp = expr;
    }

    @Override
    public void setElse(Expression expr) {
        this.elseExp = expr;
    }

}
