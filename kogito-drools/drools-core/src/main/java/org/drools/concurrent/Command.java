/**
 * 
 */
package org.drools.concurrent;

import java.io.Serializable;

import org.drools.WorkingMemory;

public interface Command extends Serializable {
    void execute(WorkingMemory workingMemory);
}