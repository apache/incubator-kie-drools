package org.kie.dmn.api.core.event;

public interface BeforeEvaluateDecisionTableEvent extends DMNEvent {
    String getNodeName();

    String getDecisionTableName();
    
    default String getDecisionTableId() {
        return null;
    }
}
