package org.kie.internal.runtime.error;

import java.util.List;

public interface ExecutionErrorStorage {

    ExecutionError store(ExecutionError error);
    
    ExecutionError get(String errorId);
    
    void acknowledge(String user, String... errorId);
    
    List<ExecutionError> list(Integer page, Integer pageSize);
    
    List<ExecutionError> listByProcessInstance(String processInstanceId, Integer page, Integer pageSize);
    
    List<ExecutionError> listByActivity(String activityName, Integer page, Integer pageSize);
    
    List<ExecutionError> listByDeployment(String deploymentId, Integer page, Integer pageSize);
}
