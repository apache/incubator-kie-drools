package org.drools.process.instance;

import java.util.Map;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface WorkItem {
	
	int PENDING = 0;
	int ACTIVE = 1;
	int COMPLETED = 2;
	int ABORTED = 3;

    long getId();
    
    String getName();
    
    int getState();
    
    Object getParameter(String name);
    Map<String, Object> getParameters();
    
    Object getResult(String name);
    Map<String, Object> getResults();

    long getProcessInstanceId();

}
