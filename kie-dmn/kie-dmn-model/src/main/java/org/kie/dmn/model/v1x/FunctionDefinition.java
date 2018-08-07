package org.kie.dmn.model.v1x;

import java.util.List;

public interface FunctionDefinition {

    List<InformationItem> getFormalParameter();

    Expression getExpression();

    void setExpression(Expression value);

    FunctionKind getKind();

    void setKind(FunctionKind value);

}
