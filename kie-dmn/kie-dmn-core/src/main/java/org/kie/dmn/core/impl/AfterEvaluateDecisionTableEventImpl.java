package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;

import java.util.Collections;
import java.util.List;

public class AfterEvaluateDecisionTableEventImpl
        implements AfterEvaluateDecisionTableEvent {

    private final String        nodeName;
    private final String        dtName;
    private final String        dtId;
    private final DMNResult     result;
    private final List<Integer> matches;
    private final List<Integer> fired;

    public AfterEvaluateDecisionTableEventImpl(String nodeName, String dtName, String dtId, DMNResult result, List<Integer> matches, List<Integer> fired) {
        this.nodeName = nodeName;
        this.dtName = dtName;
        this.dtId = dtId;
        this.result = result;
        this.matches = matches;
        this.fired = fired;
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
    public List<Integer> getMatches() {
        return matches == null ? Collections.emptyList() : matches;
    }

    @Override
    public List<Integer> getSelected() {
        return fired == null ? Collections.emptyList() : fired;
    }

    @Override
    public String toString() {
        return "AfterEvaluateDecisionTableEvent{ nodeName='"+nodeName+"' decisionTableName='" + dtName + "' matches=" + getMatches() + " fired=" + getSelected() + " }";
    }

}
