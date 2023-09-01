package org.drools.core.process;

import java.util.Map;

public interface WorkItem extends org.kie.api.runtime.process.WorkItem {

    void setName(String name);

    void setParameter(String name, Object value);

    void setParameters(Map<String, Object> parameters);

    void setResults(Map<String, Object> results);

    void setState(int state);

    void setProcessInstanceId(String processInstanceId);

    void setDeploymentId(String deploymentId);

    void setNodeInstanceId(long deploymentId);

    void setNodeId(long deploymentId);

    String getDeploymentId();

    long getNodeInstanceId();

    long getNodeId();
}
