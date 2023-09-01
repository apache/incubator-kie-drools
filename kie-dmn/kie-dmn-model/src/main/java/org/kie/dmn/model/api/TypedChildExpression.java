package org.kie.dmn.model.api;

public interface TypedChildExpression extends ChildExpression {

    String getTypeRef();

    void setTypeRef(String value);

}
