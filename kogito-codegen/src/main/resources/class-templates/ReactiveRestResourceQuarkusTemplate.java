package com.myspace.demo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.kie.api.runtime.process.WorkItemNotFoundException;
import org.kie.kogito.Application;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.workitem.Policies;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.services.identity.StaticIdentityProvider;
import org.kie.kogito.auth.IdentityProvider;

@Path("/$name$")
public class $Type$ReactiveResource {

    Process<$Type$> process;

    Application application;

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public CompletionStage<$Type$Output> createResource_$name$(@Context HttpHeaders httpHeaders,
                                                               @QueryParam("businessKey") String businessKey,
                                                               $Type$Input resource) {
        return CompletableFuture
            .supplyAsync(
                () -> UnitOfWorkExecutor
                    .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> {
                            $Type$Input inputModel = resource != null ? resource : new $Type$Input();
                            ProcessInstance<$Type$> pi = process.createInstance(businessKey, inputModel.toModel());
                            String startFromNode = httpHeaders.getHeaderString("X-KOGITO-StartFromNode");
                            if (startFromNode != null) {
                                pi.startFrom(startFromNode);
                            } else {
                                pi.start();
                            }
                            return pi.checkError().variables().toOutput();
                        }));
    }

    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<List<$Type$Output>> getResources_$name$() {
        return CompletableFuture
            .supplyAsync(
                () -> process
                    .instances()
                    .values()
                    .stream()
                    .map(pi -> pi.variables().toOutput())
                    .collect(Collectors.toList()));
    }

    @GET()
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<$Type$Output> getResource_$name$(@PathParam("id") String id) {
        return CompletableFuture
            .supplyAsync(
                () -> process
                    .instances()
                    .findById(id, ProcessInstanceReadMode.READ_ONLY)
                    .map(pi -> pi.variables().toOutput())
                    .orElse(null));
    }

    @DELETE()
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<$Type$Output> deleteResource_$name$(@PathParam("id") final String id) {
        return CompletableFuture
            .supplyAsync(
                () -> UnitOfWorkExecutor
                    .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                            .instances()
                            .findById(id)
                            .map(pi -> {
                                pi.abort();
                                return pi.checkError().variables().toOutput();
                            })
                            .orElse(null)));
    }

    @PUT()
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<$Type$Output> updateModel_$name$(@PathParam("id") String id, $Type$ resource) {
        return CompletableFuture
            .supplyAsync(
                () -> UnitOfWorkExecutor
                    .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                            .instances()
                            .findById(id)
                            .map(pi -> pi.updateVariables(resource).toOutput())
                            .orElse(null)));
    }

    @GET()
    @Path("/{id}/tasks")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<List<WorkItem>> getTasks_$name$(@PathParam("id") String id,
                                                                @QueryParam("user") final String user,
                                                                @QueryParam("group") final List<String> groups) {
        return CompletableFuture
            .supplyAsync(
                () -> process
                    .instances()
                    .findById(id, ProcessInstanceReadMode.READ_ONLY)
                    .map(pi -> pi.workItems(Policies.of(user, groups)))
                    .orElse(null));
    }
}
