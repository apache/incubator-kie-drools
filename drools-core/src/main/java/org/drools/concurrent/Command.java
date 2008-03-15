/**
 * 
 */
package org.drools.concurrent;

import org.drools.WorkingMemory;

import java.io.Externalizable;

public interface Command extends Externalizable {
    void execute(WorkingMemory workingMemory);
}