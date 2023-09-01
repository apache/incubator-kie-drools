package org.kie.dmn.model.v1_2;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.FunctionKind;
import org.kie.dmn.model.api.InformationItem;

public class TFunctionDefinition extends TExpression implements FunctionDefinition {

    protected List<InformationItem> formalParameter;
    protected Expression expression;
    protected FunctionKind kind;

    @Override
    public List<InformationItem> getFormalParameter() {
        if (formalParameter == null) {
            formalParameter = new ArrayList<>();
        }
        return this.formalParameter;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(Expression value) {
        this.expression = value;
    }

    @Override
    public FunctionKind getKind() {
        if (kind == null) {
            return FunctionKind.FEEL;
        } else {
            return kind;
        }
    }

    @Override
    public void setKind(FunctionKind value) {
        this.kind = value;
    }

}
