package org.drools.spi;

import org.drools.WorkingMemory;

public interface Action {
    
    public void execute(final KnowledgeHelper knowledgeHelper, final WorkingMemory workingMemory) throws Exception ;
}
