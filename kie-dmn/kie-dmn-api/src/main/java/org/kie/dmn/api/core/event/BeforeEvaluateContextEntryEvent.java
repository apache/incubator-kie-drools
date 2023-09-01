package org.kie.dmn.api.core.event;

public interface BeforeEvaluateContextEntryEvent extends DMNEvent {

    String getNodeName();

    String getVariableName();

    String getVariableId();

    String getExpressionId();

}
