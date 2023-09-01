package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionTableEvent;

public class BeforeEvaluateDecisionTableEventImpl
        implements BeforeEvaluateDecisionTableEvent {

    private final String nodeName;
    private final String dtName;
    private final String dtId;
    private final DMNResult result;

    public BeforeEvaluateDecisionTableEventImpl(String nodeName, String dtName, String dtId, DMNResult result) {
        this.nodeName = nodeName;
        this.dtName = dtName;
        this.dtId = dtId;
        this.result = result;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public String getDecisionTableName() {
        return dtName;
    }

    @Override
    public String getDecisionTableId() {
        return dtId;
    }

    @Override
    public DMNResult getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "BeforeEvaluateDecisionTableEvent{ nodeName='"+nodeName+"' decisionTableName='"+dtName+"' }";
    }

}
