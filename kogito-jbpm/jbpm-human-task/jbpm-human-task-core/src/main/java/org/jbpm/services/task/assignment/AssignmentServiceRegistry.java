/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.task.assignment;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.kie.internal.task.api.assignment.AssignmentStrategy;

public class AssignmentServiceRegistry {
    
    private static final ServiceLoader<AssignmentStrategy> foundStrategies = ServiceLoader.load(AssignmentStrategy.class, AssignmentServiceRegistry.class.getClassLoader());

    private Map<String, AssignmentStrategy> assignmentStrategies = new HashMap<>();    

    private AssignmentServiceRegistry() {

        foundStrategies
            .forEach(strategy -> assignmentStrategies.put(strategy.getIdentifier(), strategy));
    }
    
    public static AssignmentServiceRegistry get() {
        return Holder.INSTANCE;
    }
    
    public AssignmentStrategy getStrategy(String id) {
        AssignmentStrategy strategy = assignmentStrategies.get(id);
        if (strategy == null) {
            throw new IllegalArgumentException("No assignment strategy was found with id " + id);
        }
        
        return strategy;
    }
  
    private static class Holder {
        static final AssignmentServiceRegistry INSTANCE = new AssignmentServiceRegistry();
    }
    
    public synchronized void addStrategy(AssignmentStrategy assignmentStrategy) {
        this.assignmentStrategies.put(assignmentStrategy.getIdentifier(), assignmentStrategy);
        
    }
}
