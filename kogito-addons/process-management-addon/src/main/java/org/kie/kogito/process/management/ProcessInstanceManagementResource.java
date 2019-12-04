/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.process.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.kie.kogito.Application;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessError;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;

@Path("/management/processes")
public class ProcessInstanceManagementResource {

    @Inject
    Processes processes;
    
    @Inject
    Application application;
    
    @GET
    @Path("{processId}/instances/{processInstanceId}/error")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstanceInError(@PathParam("processId") String processId, @PathParam("processInstanceId") String processInstanceId) {
        
        return executeOnInstanceInError(processId, processInstanceId, processInstance -> {
                ProcessError error = processInstance.error().get();
                
                Map<String, String> data = new HashMap<>();
                data.put("id", processInstance.id());
                data.put("failedNodeId", error.failedNodeId());
                data.put("message", error.errorMessage());
                                
                return Response
                        .status(Response.Status.OK)
                        .entity(data)
                        .build();
                
            });
    }
    
    @GET
    @Path("{processId}/instances/{processInstanceId}/nodeInstances")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWorkItemsInProcessInstance(@PathParam("processId") String processId, @PathParam("processInstanceId") String processInstanceId) {
        
        return executeOnInstance(processId, processInstanceId, processInstance -> {
            List<WorkItem> workItems = processInstance.workItems();
                                   
                return Response
                        .status(Response.Status.OK)
                        .entity(workItems)
                        .build();
                
            });
    }
    
    @POST
    @Path("{processId}/instances/{processInstanceId}/retrigger")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retriggerInstanceInError(@PathParam("processId") String processId, @PathParam("processInstanceId") String processInstanceId) {
        
        return executeOnInstanceInError(processId, processInstanceId, processInstance -> {
                processInstance.error().get().retrigger();
                
                if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                    throw new ProcessInstanceExecutionException(processInstance.id(), processInstance.error().get().failedNodeId(), processInstance.error().get().errorMessage());
                } else {
                    return Response.status(Response.Status.OK).entity(processInstance.variables()).build();
                }
            });
    }
    
    @POST
    @Path("{processId}/instances/{processInstanceId}/skip")
    @Produces(MediaType.APPLICATION_JSON)
    public Response skipInstanceInError(@PathParam("processId") String processId, @PathParam("processInstanceId") String processInstanceId) {
        
        return executeOnInstanceInError(processId, processInstanceId, processInstance -> {
                processInstance.error().get().skip();
                
                if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                    throw new ProcessInstanceExecutionException(processInstance.id(), processInstance.error().get().failedNodeId(), processInstance.error().get().errorMessage());
                } else {
                    return Response.status(Response.Status.OK).entity(processInstance.variables()).build();
                }
            });
    }
    
    @POST
    @Path("{processId}/instances/{processInstanceId}/nodes/{nodeId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response triggerNodeInstanceId(@PathParam("processId") String processId, @PathParam("processInstanceId") String processInstanceId, @PathParam("nodeId") String nodeId) {
        
        return executeOnInstance(processId, processInstanceId, processInstance -> {
            processInstance.triggerNode(nodeId);
            
            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(processInstance.id(), processInstance.error().get().failedNodeId(), processInstance.error().get().errorMessage());
            } else {
                return Response.status(Response.Status.OK).entity(processInstance.variables()).build();
            }
        });
    }
    
    @POST
    @Path("{processId}/instances/{processInstanceId}/nodeInstances/{nodeInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retriggerNodeInstanceId(@PathParam("processId") String processId, @PathParam("processInstanceId") String processInstanceId, @PathParam("nodeInstanceId") String nodeInstanceId) {
        
        return executeOnInstance(processId, processInstanceId, processInstance -> {
            processInstance.retriggerNodeInstance(nodeInstanceId);
            
            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(processInstance.id(), processInstance.error().get().failedNodeId(), processInstance.error().get().errorMessage());
            } else {
                return Response.status(Response.Status.OK).entity(processInstance.variables()).build();
            }
        });
    }
    
    @DELETE
    @Path("{processId}/instances/{processInstanceId}/nodeInstances/{nodeInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelNodeInstanceId(@PathParam("processId") String processId, @PathParam("processInstanceId") String processInstanceId, @PathParam("nodeInstanceId") String nodeInstanceId) {
        
        return executeOnInstance(processId, processInstanceId, processInstance -> {
            processInstance.cancelNodeInstance(nodeInstanceId);
            
            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(processInstance.id(), processInstance.error().get().failedNodeId(), processInstance.error().get().errorMessage());
            } else {
                return Response.status(Response.Status.OK).entity(processInstance.variables()).build();
            }
        });
    }
    
    @DELETE
    @Path("{processId}/instances/{processInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelProcessInstanceId(@PathParam("processId") String processId, @PathParam("processInstanceId") String processInstanceId) {
        
        return executeOnInstance(processId, processInstanceId, processInstance -> {
            processInstance.abort();
            
            if (processInstance.status() == ProcessInstance.STATE_ERROR) {
                throw new ProcessInstanceExecutionException(processInstance.id(), processInstance.error().get().failedNodeId(), processInstance.error().get().errorMessage());
            } else {
                return Response.status(Response.Status.OK).entity(processInstance.variables()).build();
            }
        });
    }
    
    /*
     * Helper methods
     */
    
    protected Response executeOnInstanceInError(String processId, String processInstanceId, Function<ProcessInstance<?>, Response> supplier) {
        if (processId == null || processInstanceId == null) {
            return Response.status(Status.BAD_REQUEST).entity(PROCESS_AND_INSTANCE_REQUIRED).build();
        }
        
        
        Process<?> process = processes.processById(processId);        
        if (process == null) {
            return Response.status(Status.NOT_FOUND).entity(String.format(PROCESS_NOT_FOUND, processId)).build();
        }
        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            Optional<? extends ProcessInstance<?>> processInstanceFound = process.instances().findById(processInstanceId);        
            if (processInstanceFound.isPresent()) {
                ProcessInstance<?> processInstance = processInstanceFound.get();
                
                
                if (processInstance.error().isPresent()) {
                    return supplier.apply(processInstance);
                } else {
                    return Response.status(Status.BAD_REQUEST).entity(String.format(PROCESS_INSTANCE_NOT_IN_ERROR, processInstanceId)).build();
                }
            } else {
                return Response.status(Status.NOT_FOUND).entity(String.format(PROCESS_INSTANCE_NOT_FOUND, processInstanceId)).build();
            }
        });
    }
    
    protected Response executeOnInstance(String processId, String processInstanceId, Function<ProcessInstance<?>, Response> supplier) {
        if (processId == null || processInstanceId == null) {
            return Response.status(Status.BAD_REQUEST).entity(PROCESS_AND_INSTANCE_REQUIRED).build();
        }
        
        
        Process<?> process = processes.processById(processId);        
        if (process == null) {
            return Response.status(Status.NOT_FOUND).entity(String.format(PROCESS_NOT_FOUND, processId)).build();
        }
        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            Optional<? extends ProcessInstance<?>> processInstanceFound = process.instances().findById(processInstanceId);        
            if (processInstanceFound.isPresent()) {
                ProcessInstance<?> processInstance = processInstanceFound.get();

                return supplier.apply(processInstance);
    
            } else {
                return Response.status(Status.NOT_FOUND).entity(String.format(PROCESS_INSTANCE_NOT_FOUND, processInstanceId)).build();
            }
        });
    }
    
    private static final String PROCESS_AND_INSTANCE_REQUIRED = "Process id and Process instance id must be given";
    private static final String PROCESS_NOT_FOUND = "Process with id %s not found";
    private static final String PROCESS_INSTANCE_NOT_FOUND = "Process instance with id %s not found";
    private static final String PROCESS_INSTANCE_NOT_IN_ERROR = "Process instance with id %s is not in error state";
    
}
