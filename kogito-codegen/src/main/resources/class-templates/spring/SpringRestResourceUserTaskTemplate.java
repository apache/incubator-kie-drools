package com.myspace.demo;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<$Type$Output> signal(@PathVariable("id") final String id,
                                               final UriComponentsBuilder uriComponentsBuilder) {
        return UnitOfWorkExecutor
            .executeInUnitOfWork(
                application.unitOfWorkManager(),
                () -> process
                    .instances()
                    .findById(id)
                    .map(pi -> {
                        pi.send(Sig.of("$taskNodeName$", java.util.Collections.emptyMap()));
                        java.util.Optional<WorkItem> task = pi
                            .workItems()
                            .stream()
                            .filter(wi -> wi.getName().equals("$taskName$"))
                            .findFirst();
                        if (task.isPresent()) {
                            UriComponents uriComponents =
                                    uriComponentsBuilder.path("/$name$/{id}/$taskName$/{taskId}")
                                            .buildAndExpand(id, task.get().getId());
                            URI location = uriComponents.toUri();
                            return ResponseEntity.created(location)
                                    .body(pi.checkError().variables().toOutput());
                        }
                        return new ResponseEntity<$Type$Output>(HttpStatus.NOT_FOUND);
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping(value = "/{id}/$taskName$/{workItemId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<$Type$Output> completeTask(@PathVariable("id") final String id,
                                                     @PathVariable("workItemId") final String workItemId,
                                                     @RequestParam(value = "phase", required = false, defaultValue =
                                                             "complete") final String phase,
                                                     @RequestParam(value = "user", required = false) final String user,
                                                     @RequestParam(value = "group", required = false, defaultValue = "") final List<String> groups,
                                                     @RequestBody(required = false) final $TaskOutput$ model) {
        return UnitOfWorkExecutor
                .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                                .instances()
                                .findById(id)
                                .map(pi -> {
                                    pi
                                            .transitionWorkItem(
                                                    workItemId,
                                                    HumanTaskTransition.withModel(phase, model, Policies.of(user,
                                                                                                            groups)));
                                    return ResponseEntity.ok(pi.checkError().variables().toOutput());
                                })
                                .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @GetMapping(value = "/{id}/$taskName$/{workItemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<$TaskInput$> getTask(@PathVariable("id") String id,
                               @PathVariable("workItemId") String workItemId,
                               @RequestParam(value = "user", required = false) final String user,
                               @RequestParam(value = "group", required = false, defaultValue = "") final List<String> groups) {
        return process
            .instances()
            .findById(id)
            .map(pi -> $TaskInput$.from(pi.workItem(workItemId, Policies.of(user, groups))))
            .map(m -> ResponseEntity.ok(m))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "$taskName$/schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getSchema() {
        return JsonSchemaUtil.load(this.getClass().getClassLoader(), process.id(), "$taskName$");
    }

    @GetMapping(value = "/{id}/$taskName$/{workItemId}/schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getSchemaAndPhases(@PathVariable("id") final String id,
                                         @PathVariable("workItemId") final String workItemId,
                                         @RequestParam(value = "user", required = false) final String user,
                                         @RequestParam(value = "group", required = false, defaultValue = "") final List<String> groups) {
        return JsonSchemaUtil
            .addPhases(
                process,
                application,
                id,
                workItemId,
                Policies.of(user, groups),
                JsonSchemaUtil.load(this.getClass().getClassLoader(), process.id(), "$taskName$"));
    }

    @DeleteMapping(value = "/{id}/$taskName$/{workItemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<$Type$Output> abortTask(@PathVariable("id") final String id,
                                                  @PathVariable("workItemId") final String workItemId,
                                                  @RequestParam(value = "phase", required = false, defaultValue =
                                                          "abort") final String phase,
                                                  @RequestParam(value = "user", required = false) final String user,
                                                  @RequestParam(value = "group", required = false, defaultValue = "") final List<String> groups) {
        return UnitOfWorkExecutor
                .executeInUnitOfWork(
                        application.unitOfWorkManager(),
                        () -> process
                                .instances()
                                .findById(id)
                                .map(pi -> {
                                    pi
                                            .transitionWorkItem(
                                                    workItemId,
                                                    HumanTaskTransition.withoutModel(phase, Policies.of(user, groups)));
                                    return ResponseEntity.ok(pi.checkError().variables().toOutput());
                                })
                                .orElseGet(() -> ResponseEntity.notFound().build()));
    }
}
