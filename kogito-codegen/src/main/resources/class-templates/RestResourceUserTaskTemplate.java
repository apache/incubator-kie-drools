package com.myspace.demo;

import java.util.List;

import org.drools.core.WorkItemNotFoundException;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.impl.Sig;

public class $Type$Resource {

    @POST
    @Path("/{id}/$taskName$")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response signal(@PathParam("id") final String id) {
        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            return process
                          .instances()
                          .findById(id)
                          .map(pi -> {
                              pi.send(Sig.of("$taskNodeName$", java.util.Collections.emptyMap()));
                              java.util.Optional<WorkItem> task =
                                      pi.workItems().stream().filter(wi -> wi.getName().equals("$taskName$"))
                                        .findFirst();
                              if (task.isPresent()) {
                                  return Response.ok(pi.variables().toOutput())
                                                 .header("Link", "</" + id + "/$taskName$/" + task.get().getId() +
                                                                 ">; rel='instance'")
                                                 .build();
                              }
                              return Response.status(Response.Status.NOT_FOUND).build();
                          })
                          .orElse(null);
        });
    }

    @POST()
    @Path("/{id}/$taskName$/{workItemId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public $Type$Output completeTask(@PathParam("id") final String id,
                                     @PathParam("workItemId") final String workItemId,
                                     @QueryParam("phase") @DefaultValue("complete") final String phase,
                                     @QueryParam("user") final String user,
                                     @QueryParam("group") final List<String> groups,
                                     final $TaskOutput$ model) {
        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(),
                                                      () -> process
                                                                   .instances()
                                                                   .findById(id)
                                                                   .map(pi -> {
                                                                       pi.transitionWorkItem(workItemId,
                                                                                             HumanTaskTransition.withModel(phase,
                                                                                                                           model,
                                                                                                                           policies(user,
                                                                                                                                    groups)));
                                                                       return pi.variables().toOutput();
                                                                   })
                                                                   .orElse(null));
    }

    @GET()
    @Path("/{id}/$taskName$/{workItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public $TaskInput$ getTask(@PathParam("id") String id,
                               @PathParam("workItemId") String workItemId,
                               @QueryParam("user") final String user,
                               @QueryParam("group") final List<String> groups) {
        return process.instances()
                      .findById(id, ProcessInstanceReadMode.READ_ONLY)
                      .map(pi -> $TaskInput$.from(pi.workItem(workItemId, policies(user, groups))))
                      .orElse(null);
    }

    @GET()
    @Path("$taskName$/schema")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getSchema() {
        return JsonSchemaUtil.load(this.getClass().getClassLoader(), process.id(), "$taskName$");
    }

    @GET()
    @Path("/{id}/$taskName$/{workItemId}/schema")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getSchemaAndPhases(@PathParam("id") final String id,
                                                  @PathParam("workItemId") final String workItemId,
                                                  @QueryParam("user") final String user,
                                                  @QueryParam("group") final List<String> groups) {
        return JsonSchemaUtil.addPhases(process, application, id, workItemId, policies(user, groups),
                                        JsonSchemaUtil.load(this.getClass().getClassLoader(), process.id(),
                                                            "$taskName$"));
    }

    @DELETE()
    @Path("/{id}/$taskName$/{workItemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public $Type$Output abortTask(@PathParam("id") final String id,
                                  @PathParam("workItemId") final String workItemId,
                                  @QueryParam("phase") @DefaultValue("abort") final String phase,
                                  @QueryParam("user") final String user,
                                  @QueryParam("group") final List<String> groups) {
        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(),
                                                      () -> process
                                                                   .instances()
                                                                   .findById(id)
                                                                   .map(pi -> {
                                                                       pi.transitionWorkItem(workItemId,
                                                                                             HumanTaskTransition.withoutModel(phase,
                                                                                                                              policies(user,
                                                                                                                                       groups)));
                                                                       return pi.variables().toOutput();
                                                                   })
                                                                   .orElse(null));
    }
}
