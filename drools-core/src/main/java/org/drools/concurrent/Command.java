/**
 * 
 */
package org.drools.concurrent;

import java.io.Externalizable;

import org.drools.WorkingMemory;

public interface Command extends Externalizable {
    void execute(WorkingMemory workingMemory);
}