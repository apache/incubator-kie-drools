package org.kie.dmn.api.core.event;

public interface AfterEvaluateContextEntryEvent extends DMNEvent {

    String getNodeName();

    String getVariableName();

    String getVariableId();

    String getExpressionId();

    Object getExpressionResult();

}
