package org.drools.process.instance;

import java.util.Map;


/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface InternalWorkItem extends WorkItem {
	
	void setName(String name);
	
	void setParameter(String name, Object value);
	
	void setParameters(Map<String, Object> parameters);
	
	void setResults(Map<String, Object> results);
	
	void setState(int state);
	
	void setProcessInstanceId(long processInstanceId);
	
}
