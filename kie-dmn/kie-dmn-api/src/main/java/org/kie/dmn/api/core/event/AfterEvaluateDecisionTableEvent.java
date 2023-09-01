package org.kie.dmn.api.core.event;

import java.util.List;

public interface AfterEvaluateDecisionTableEvent extends DMNEvent {

    String getNodeName();

    String getDecisionTableName();
    
    default String getDecisionTableId() {
        return null;
    }

    List<Integer> getMatches();

    List<Integer> getSelected();
}
