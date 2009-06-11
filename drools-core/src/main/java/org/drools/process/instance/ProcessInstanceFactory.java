package org.drools.process.instance;

import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.definition.process.Process;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface ProcessInstanceFactory {
    
    ProcessInstance createProcessInstance(Process process, WorkingMemory workingMemory, Map<String, Object> parameters);

}
