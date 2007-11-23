package org.drools.spi;

import org.drools.WorkingMemory;
import org.drools.rule.Dialectable;

public interface Action {
    void execute(WorkingMemory workingMemory) throws Exception ;
}
