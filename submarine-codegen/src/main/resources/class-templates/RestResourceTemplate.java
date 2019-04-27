package com.myspace.demo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.kie.api.runtime.process.WorkItemNotFoundException;
import org.kie.submarine.process.Process;
import org.kie.submarine.process.ProcessInstance;
import org.kie.submarine.process.WorkItem;

@Path("/$name$")
@Api(description = "$documentation$")
public class $Type$Resource {

    Process<$Type$> process;

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("Creates new instance of $name$")
    public $Type$ createResource(@ApiParam(value="$name$ data that should be created") $Type$ resource) {
        if (resource == null) {
            resource = new $Type$();
        }

        ProcessInstance<$Type$> pi = process.createInstance(resource);
        pi.start();
        return pi.variables();
    }

    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Returns a list of $name$")
    public List<$Type$> getResources() {
        return process.instances().values().stream()
                .map(ProcessInstance::variables)
                .collect(Collectors.toList());
    }

    @GET()
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Returns information about specified $name$")
    public $Type$ getResource(@PathParam("id") Long id) {
        return process.instances()
                .findById(id)
                .map(ProcessInstance::variables)
                .orElse(null);
    }

    @DELETE()
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Cancels specified $name$")
    public $Type$ deleteResource(@PathParam("id") Long id) {
        ProcessInstance<$Type$> pi = process.instances()
                .findById(id)
                .orElse(null);
        if (pi == null) {
            return null;
        } else {
            pi.abort();
            return pi.variables();
        }
    }
    
    @GET()
    @Path("/{id}/tasks")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Returns list of task ids that are currently active in given $name$")
    public Map<Long, String> getTasks(@PathParam("id") Long id) {
        return process.instances()
                .findById(id)
                .map(ProcessInstance::workItems)
                .map(l -> l.stream().collect(Collectors.toMap(WorkItem::getId, WorkItem::getName)))
                .orElse(null);
    }
}
