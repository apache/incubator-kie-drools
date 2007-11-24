package org.drools.ruleflow.common.instance;

import java.util.Map;

public interface WorkItem {
	
	int PENDING = 0;
	int ACTIVE = 1;
	int COMPLETED = 2;
	int ABORTED = 3;

    //void setId(long id);
    long getId();
    
    //void setName(String name);
    String getName();
    
    //void setState(int state);
    int getState();
    
    //void setParameters(Map parameters);
    Object getParameter(String name);
    Map getParameters();
    
    //void setResults(Map results);
    //void setResult(String name, Object value);
    Object getResult(String name);
    Map getResults();

    //void setProcessInstanceId(long processInstanceId);
    long getProcessInstanceId();

}
