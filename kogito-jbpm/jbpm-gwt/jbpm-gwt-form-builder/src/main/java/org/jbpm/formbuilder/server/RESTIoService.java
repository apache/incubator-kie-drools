/*
 * Copyright 2011 JBoss Inc 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formbuilder.server;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jbpm.formapi.server.form.FormEncodingServerFactory;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formbuilder.server.xml.ListTasksDTO;
import org.jbpm.formbuilder.shared.task.TaskDefinitionService;
import org.jbpm.formbuilder.shared.task.TaskRef;
import org.jbpm.formbuilder.shared.task.TaskServiceException;

@Path("/io")
public class RESTIoService extends RESTBaseService {

    private TaskDefinitionService taskService = null;
    
    public void setContext(@Context ServletContext context) {
        if (taskService == null) {
            taskService = ServiceFactory.getInstance().getTaskDefinitionService();
        }
    }
    
    public RESTIoService() {
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), FormEncodingServerFactory.getDecoder());
    }
    
    @GET @Path("/package/{pkgName}")
    public Response getIoAssociations(@QueryParam("q") String filter, @PathParam("pkgName") String pkgName, @Context ServletContext context) {
        setContext(context);
        String[] filters = filter == null ? new String[0] : filter.split(" ");
        String newFilter = filters.length == 0 ? (filter == null ? "" : filter) : "";
        for (String subFilter : filters) {
            if (subFilter.startsWith("iotype:")) {
                //TODO String type = subFilter.replace("iotype:", ""); decide what to do with this filter
            } else {
                newFilter += subFilter + " ";
            }
        }
        if (newFilter.endsWith(" ")) {
            newFilter = newFilter.substring(0, newFilter.length() - 1); //remove last space
        }
        try {
            List<TaskRef> tasks = taskService.query(pkgName, newFilter);
            ListTasksDTO dto = new ListTasksDTO(tasks);
            return Response.ok(dto, MediaType.APPLICATION_XML).build();
        } catch (TaskServiceException e) {
            return error("Problem getting io associations for package " + pkgName + " with filter " + filter, e);
        }
    }
    
    @GET @Path("/package/{pkgName}/process/{procName}/task/{taskName}")
    public Response getIoAssociation(@PathParam("pkgName") String pkgName, 
            @PathParam("procName") String procName, @PathParam("taskName") String taskName,
            @Context ServletContext context) {
        setContext(context);
        try {
            List<TaskRef> tasks = taskService.getTasksByName(pkgName, procName, taskName);
            ListTasksDTO dto = new ListTasksDTO(tasks);
            return Response.ok(dto, MediaType.APPLICATION_XML).build();
        } catch (TaskServiceException e) {
            return error("Problem getting io association for package " + pkgName + ", process " + procName + ", task " + taskName, e);
        }
    }
    
    /**
     * @param taskService the taskService to set (for test cases purpose)
     */
    public void setTaskService(TaskDefinitionService taskService) {
        this.taskService = taskService;
    }

    public TaskDefinitionService getTaskService() {
        return this.taskService;
    }
}
