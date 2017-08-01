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

import org.jbpm.services.task.assignment.impl.AssignmentServiceImpl;
import org.kie.internal.task.api.assignment.AssignmentStrategy;

public class AssignmentServiceProvider {

    private AssignmentService assignmentService;
    
    private AssignmentServiceProvider() {
        this.assignmentService = new AssignmentServiceImpl();
    } 
    
    public AssignmentService getAssignmentService() {
        return this.assignmentService;
    }

    public void setAssignmentService(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
        
    }
    
    public static AssignmentService get() {
        return Holder.INSTANCE.getAssignmentService();
    }
        
    private static class Holder {
        static final AssignmentServiceProvider INSTANCE = new AssignmentServiceProvider();
    }
    
    // for test purpose
    static AssignmentService override(AssignmentStrategy strategy) {
        Holder.INSTANCE.setAssignmentService(new AssignmentServiceImpl(strategy)); 
        
        return get();
    }
    
    static void clear() {
        Holder.INSTANCE.setAssignmentService(new AssignmentServiceImpl()); 
    }
    

}
