package com.myspace.demo;

import java.util.List;

import org.drools.core.WorkItemNotFoundException;


public class $Type$Resource {

    
    @POST()
    @Path("/{id}/$taskname$/{workItemId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public $Type$ completeTask(@PathParam("id") final String id, @PathParam("workItemId") final String workItemId, @QueryParam("phase") @DefaultValue("complete") final String phase, @QueryParam("user") final String user, @QueryParam("group") final List<String> groups, final $TaskOutput$ model) {
        try {
            return org.kie.kogito.services.uow.UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
                ProcessInstance<$Type$> pi = process.instances().findById(id).orElse(null);
                if (pi == null) {
                    return null;
                } else {
                    org.kie.kogito.auth.IdentityProvider identity = null;
                    if (user != null) {
                        identity = new org.kie.kogito.services.identity.StaticIdentityProvider(user, groups);
                    }
                    org.jbpm.process.instance.impl.humantask.HumanTaskTransition transition = new org.jbpm.process.instance.impl.humantask.HumanTaskTransition(phase, model.toMap(), identity);
                    pi.transitionWorkItem(workItemId, transition);
                    
                    return getModel(pi);
                }
            });
        } catch (WorkItemNotFoundException e) {
            return null;
        }
    }
    
    
    @GET()
    @Path("/{id}/$taskname$/{workItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public $TaskInput$ getTask(@PathParam("id") String id, @PathParam("workItemId") String workItemId, @QueryParam("user") final String user, @QueryParam("group") final List<String> groups) {
        try {
            ProcessInstance<$Type$> pi = process.instances().findById(id).orElse(null);
            if (pi == null) {
                return null;
            } else {
                
                WorkItem workItem = pi.workItem(workItemId, policies(user, groups));
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
    public $Type$ abortTask(@PathParam("id") final String id, @PathParam("workItemId") final String workItemId, @QueryParam("phase") @DefaultValue("abort") final String phase, @QueryParam("user") final String user, @QueryParam("group") final List<String> groups) {
        
        try {
            
            return org.kie.kogito.services.uow.UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
                ProcessInstance<$Type$> pi = process.instances().findById(id).orElse(null);
                if (pi == null) {
                    return null;
                } else {
                    org.kie.kogito.auth.IdentityProvider identity = null;
                    if (user != null) {
                        identity = new org.kie.kogito.services.identity.StaticIdentityProvider(user, groups);
                    }
                    org.jbpm.process.instance.impl.humantask.HumanTaskTransition transition = new org.jbpm.process.instance.impl.humantask.HumanTaskTransition(phase, null, identity);
                    pi.transitionWorkItem(workItemId, transition);
                    
                    return getModel(pi);
                }
            });
        } catch (WorkItemNotFoundException e) {
            return null;
        }
    }
}
