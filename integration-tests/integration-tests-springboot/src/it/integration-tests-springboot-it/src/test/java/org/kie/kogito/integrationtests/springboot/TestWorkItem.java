package org.kie.kogito.integrationtests.springboot;

import java.util.Map;

import org.kie.kogito.process.WorkItem;


public class TestWorkItem implements WorkItem {
/*
 * It is interesting to have an implementation of WorkItem for testing that
 * the information returned by REST API is consistent with the interface definition. 
 * This will force us to change this class every time we change the interface and
 * will automatically test that the serialization/deserialization process is ok when
 * that unlikely event occurs. 
 */

    private String id;
    private String nodeInstanceId;
    private String name;
    private int state;
    private String phase;
    private String phaseStatus;
    private Map<String, Object> parameters;
    private Map<String, Object> results;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getPhaseStatus() {
        return phaseStatus;
    }

    public void setPhaseStatus(String phaseStatus) {
        this.phaseStatus = phaseStatus;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Map<String, Object> getResults() {
        return results;
    }

    public void setResults(Map<String, Object> results) {
        this.results = results;
    }


}
