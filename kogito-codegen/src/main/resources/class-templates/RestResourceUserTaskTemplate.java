package com.myspace.demo;

import org.drools.core.WorkItemNotFoundException;


public class $Type$Resource {

    
    @POST()
    @Path("/{id}/$taskname$/{workItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public $Type$ completeTask(@PathParam("id") Long id, @PathParam("workItemId") Long workItemId, $TaskOutput$ model) {
        try {
            ProcessInstance<$Type$> pi = process.instances().findById(id).orElse(null);
            if (pi == null) {
                return null;
            } else {
                pi.completeWorkItem(workItemId, model.toMap());
                
                return pi.variables();
            }
        } catch (WorkItemNotFoundException e) {
            return null;
        }
    }
    
    
    @GET()
    @Path("/{id}/$taskname$/{workItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public $TaskInput$ getTask(@PathParam("id") Long id, @PathParam("workItemId") Long workItemId) {
        try {
            ProcessInstance<$Type$> pi = process.instances().findById(id).orElse(null);
            if (pi == null) {
                return null;
            } else {
                WorkItem workItem = pi.workItem(workItemId);
                if (workItem == null) {
                    return null;
                }
                return $TaskInput$.fromMap(workItem.getId(), workItem.getName(), workItem.getParameters());
            }
        } catch (WorkItemNotFoundException e) {
            return null;
        }
    }
    
    @DELETE()
    @Path("/{id}/$taskname$/{workItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public $Type$ abortTask(@PathParam("id") Long id, @PathParam("workItemId") Long workItemId) {
        
        try {
            ProcessInstance<$Type$> pi = process.instances().findById(id).orElse(null);
            if (pi == null) {
                return null;
            } else {
                pi.abortWorkItem(workItemId);
                
                return pi.variables();
            }
        } catch (WorkItemNotFoundException e) {
            return null;
        }
    }
}
