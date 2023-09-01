package org.kie.dmn.model.api;

import java.util.List;

public interface FunctionDefinition extends Expression {

    List<InformationItem> getFormalParameter();

    Expression getExpression();

    void setExpression(Expression value);

    FunctionKind getKind();

    void setKind(FunctionKind value);

}
