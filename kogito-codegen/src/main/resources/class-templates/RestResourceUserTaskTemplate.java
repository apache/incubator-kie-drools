package com.myspace.demo;

import org.drools.core.WorkItemNotFoundException;


public class $Type$Resource {

    
    @POST()
    @Path("/{id}/$taskname$/{workItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public $Type$ completeTask(@PathParam("id") final String id, @PathParam("workItemId") final String workItemId, final $TaskOutput$ model) {
        try {
            return org.kie.kogito.services.uow.UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
                ProcessInstance<$Type$> pi = process.instances().findById(id).orElse(null);
                if (pi == null) {
                    return null;
                } else {
                    pi.completeWorkItem(workItemId, model.toMap());
                    
                    return pi.variables();
                }
            });
        } catch (WorkItemNotFoundException e) {
            return null;
        }
    }
    
    
    @GET()
    @Path("/{id}/$taskname$/{workItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public $TaskInput$ getTask(@PathParam("id") String id, @PathParam("workItemId") String workItemId) {
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
    public $Type$ abortTask(@PathParam("id") final String id, @PathParam("workItemId") final String workItemId) {
        
        try {
            
            return org.kie.kogito.services.uow.UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
                ProcessInstance<$Type$> pi = process.instances().findById(id).orElse(null);
                if (pi == null) {
                    return null;
                } else {
                    pi.abortWorkItem(workItemId);
                    
                    return pi.variables();
                }
            });
        } catch (WorkItemNotFoundException e) {
            return null;
        }
    }
}
