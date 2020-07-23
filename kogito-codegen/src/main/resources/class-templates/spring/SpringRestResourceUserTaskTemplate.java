package com.myspace.demo;

import java.util.List;

import org.jbpm.util.JsonSchemaUtil;
import org.kie.api.runtime.process.WorkItemNotFoundException;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.impl.Sig;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public class $Type$Resource {

    @PostMapping(value = "/{id}/$taskName$", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public org.springframework.http.ResponseEntity<$Type$Output> signal(@PathVariable("id") final String id) {
        return org.kie.kogito.services.uow.UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            ProcessInstance<$Type$> pi = process.instances().findById(id).orElse(null);
            if (pi == null) {
                return null;
            }
            pi.send(Sig.of("$taskNodeName$", java.util.Collections.emptyMap()));
            java.util.Optional<WorkItem> task = pi.workItems().stream().filter(wi -> wi.getName().equals("$taskName$")).findFirst();
            if(task.isPresent()) {
                return org.springframework.http.ResponseEntity.ok()
                        .header("Link", "</" + id + "/$taskName$/" + task.get().getId() + ">; rel='instance'")
                        .body(getModel(pi));
            }
            return org.springframework.http.ResponseEntity.notFound().build();
        });
    }

    @PostMapping(value = "/{id}/$taskName$/{workItemId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public $Type$Output completeTask(@PathVariable("id") final String id,
                                     @PathVariable("workItemId") final String workItemId,
                                     @RequestParam(value = "phase", defaultValue = "complete") final String phase,
                                     @RequestParam(value = "user", required = false) final String user,
                                     @RequestParam(value = "group", required = false) final List<String> groups,
                                     @RequestBody final $TaskOutput$ model) {
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

    @GetMapping(value = "/{id}/$taskName$/{workItemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public $TaskInput$ getTask(@PathVariable("id") String id, @PathVariable("workItemId") String workItemId,
                               @RequestParam(value = "user", required = false) final String user,
                               @RequestParam(value = "group", required = false) final List<String> groups) {
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

    @GetMapping(value = "$taskName$/schema", produces = MediaType.APPLICATION_JSON)
    public JsonSchema getSchema() {
        return JsonSchemaUtil.load(this.getClass().getClassLoader(), process.id(), "$taskName$");
    }

    @GetMapping(value = "/{id}/$taskName$/{workItemId}/schema", produces = MediaType.APPLICATION_JSON)
    public JsonSchema getSchemaAndPhases(@PathParam("id") final String id, @PathParam("workItemId") final String workItemId, @QueryParam("user") final String user, @QueryParam("group") final List<String> groups) {
        return JsonSchemaUtil.addPhases(process, application, id, workItemId, policies(user, groups), JsonSchemaUtil.load(this.getClass().getClassLoader(), process.id(), "$taskName$"));
    }

    @DeleteMapping(value = "/{id}/$taskName$/{workItemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public $Type$Output abortTask(@PathVariable("id") final String id,
                                  @PathVariable("workItemId") final String workItemId,
                                  @RequestParam(value = "phase", defaultValue = "abort") final String phase,
                                  @RequestParam(value = "user", required = false) final String user,
                                  @RequestParam(value = "group", required = false) final List<String> groups) {

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
