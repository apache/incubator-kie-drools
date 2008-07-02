package org.drools.spi;

import org.drools.WorkingMemory;

public interface Action {
    
    void execute(final KnowledgeHelper knowledgeHelper, final WorkingMemory workingMemory, ActionContext context) throws Exception;
    
}
