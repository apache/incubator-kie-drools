package com.myspace.demo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.kie.api.runtime.process.WorkItemNotFoundException;
import org.jbpm.util.JsonSchemaUtil;
import org.kie.kogito.Application;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.ProcessInstanceNotFoundException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.workitem.Policies;
import org.kie.kogito.process.impl.Sig;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;
import org.kie.kogito.auth.IdentityProvider;
import org.jbpm.process.instance.impl.humantask.HumanTaskTransition;

@Path("/$name$")
public class $Type$Resource {

    Process<$Type$> process;

    Application application;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createResource_$name$(@Context HttpHeaders httpHeaders,
                                              @Context UriInfo uriInfo,
                                              @QueryParam("businessKey") String businessKey,
                                              $Type$Input resource) {
        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            $Type$Input inputModel = resource != null ? resource : new $Type$Input();
            ProcessInstance<$Type$> pi = process.createInstance(businessKey, inputModel.toModel());
            String startFromNode = httpHeaders.getHeaderString("X-KOGITO-StartFromNode");

            if (startFromNode != null) {
                pi.startFrom(startFromNode);
            } else {
                pi.start();
            }
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path(pi.id());
            return Response.created(uriBuilder.build())
                    .entity(pi.checkError().variables().toOutput())
                    .build();
        });
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<$Type$Output> getResources_$name$() {
        return process.instances().values().stream()
                      .map(pi -> pi.variables().toOutput())
                      .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public $Type$Output getResource_$name$(@PathParam("id") String id) {
        return process.instances()
                      .findById(id, ProcessInstanceReadMode.READ_ONLY)
                      .map(pi -> pi.variables().toOutput())
                      .orElseThrow(() -> new NotFoundException());
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public $Type$Output deleteResource_$name$(@PathParam("id") final String id) {
        return UnitOfWorkExecutor.executeInUnitOfWork(
                                                      application.unitOfWorkManager(),
                                                      () -> process
                                                                   .instances()
                                                                   .findById(id)
                                                                   .map(pi -> {
                                                                       pi.abort();
                                                                       return pi.checkError().variables().toOutput();
                                                                   })
                                                              .orElseThrow(() -> new NotFoundException()));
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public $Type$Output updateModel_$name$(@PathParam("id") String id, $Type$ resource) {
        return UnitOfWorkExecutor.executeInUnitOfWork(
                                                      application.unitOfWorkManager(),
                                                      () -> process
                                                                   .instances()
                                                                   .findById(id)
                                                                   .map(pi -> pi.updateVariables(resource).toOutput())
                                                              .orElseThrow(() -> new NotFoundException()));
    }

    @GET
    @Path("/{id}/tasks")
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkItem> getTasks_$name$(@PathParam("id") String id,
                                               @QueryParam("user") final String user,
                                               @QueryParam("group") final List<String> groups) {
        return process.instances()
                      .findById(id, ProcessInstanceReadMode.READ_ONLY)
                      .map(pi -> pi.workItems(Policies.of(user, groups)))
                      .orElseThrow(() -> new NotFoundException());
    }

}
