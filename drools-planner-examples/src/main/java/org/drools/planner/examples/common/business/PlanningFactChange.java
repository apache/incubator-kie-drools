package org.drools.planner.examples.common.business;

import org.drools.WorkingMemory;
import org.drools.planner.core.solution.Solution;

public interface PlanningFactChange {

    void doChange(Solution solution, WorkingMemory workingMemory);
    
}
