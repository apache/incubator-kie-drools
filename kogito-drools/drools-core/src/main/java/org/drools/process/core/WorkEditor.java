package org.drools.process.core;

import org.drools.process.core.Work;
import org.drools.process.core.WorkDefinition;

public interface WorkEditor {
    
    void setWorkDefinition(WorkDefinition definition);
    
    void setWork(Work work);
    
    boolean show();
    
    Work getWork();

}
