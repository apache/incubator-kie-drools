package org.kie.dmn.trisotech.model.v1_3;

import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.v1_3.TExpression;
import org.kie.dmn.trisotech.model.api.Iterator;

public class TIterator extends TExpression implements Iterator {

    private String var;

    private Expression inExpr;

    private Expression returnExpr;

    private IteratorType iteratorType;

    @Override
    public String getVariable() {
        return var;
    }

    @Override
    public Expression getIn() {
        return inExpr;
    }

    @Override
    public Expression getReturn() {
        return returnExpr;
    }

    @Override
    public IteratorType getIteratorType() {
        return iteratorType;
    }

    @Override
    public void setVariable(String var) {
        this.var = var;

    }

    @Override
    public void setIn(Expression expr) {
        this.inExpr = expr;

    }

    @Override
    public void setReturn(Expression expr) {
        this.returnExpr = expr;

    }

    @Override
    public void setIteratorType(IteratorType type) {
        this.iteratorType = type;

    }

}
