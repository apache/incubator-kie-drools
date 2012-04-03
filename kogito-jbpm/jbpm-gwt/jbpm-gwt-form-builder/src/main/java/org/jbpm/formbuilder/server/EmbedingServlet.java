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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.jbpm.formapi.server.form.FormEncodingServerFactory;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;
import org.jbpm.formapi.shared.form.FormRepresentationEncoder;
import org.jbpm.formbuilder.server.form.GuvnorFormDefinitionService;
import org.jbpm.formbuilder.server.task.GuvnorTaskDefinitionService;
import org.jbpm.formbuilder.shared.form.FormDefinitionService;
import org.jbpm.formbuilder.shared.form.FormServiceException;
import org.jbpm.formbuilder.shared.task.TaskDefinitionService;
import org.jbpm.formbuilder.shared.task.TaskPropertyRef;
import org.jbpm.formbuilder.shared.task.TaskRef;
import org.jbpm.formbuilder.shared.task.TaskServiceException;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class EmbedingServlet extends HttpServlet {

    private static final long serialVersionUID = -5943196576708424978L;

    @Override
    public void init(ServletConfig config) throws ServletException {
        FormEncodingFactory.register(FormEncodingServerFactory.getEncoder(), FormEncodingServerFactory.getDecoder());
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String profile = request.getParameter("profile");
        String usr = request.getParameter("usr");
        String pwd = request.getParameter("pwd");
        TaskDefinitionService taskService = createTaskService(request, usr, pwd);
        FormDefinitionService formService = createFormService(request, usr, pwd);
        FormRepresentationEncoder encoder = FormEncodingFactory.getEncoder();
        JsonObject json = new JsonObject();
        json.addProperty("embedded", profile);
        try {
            if (profile != null && "guvnor".equals(profile)) {
                String uuid = request.getParameter("uuid");
                String packageName = taskService.getContainingPackage(uuid);
                FormRepresentation form = formService.getFormByUUID(packageName, uuid);
                json.addProperty("uuid", uuid);
                json.addProperty("packageName", packageName);
                if (form != null) {
                    json.addProperty("formjson", encoder.encode(form));
                }
            }else {
                throw new Exception("Unknown profile for GET: " + profile);
            }
            request.setAttribute("jsonData", new Gson().toJson(json));
            request.getRequestDispatcher("/FormBuilder.jsp").forward(request, response);
        } catch (TaskServiceException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Problem getting task from guvnor");
        } catch (FormServiceException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Problem reading form from guvnor");
        } catch (FormEncodingException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Problem encoding form");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String profile = request.getParameter("profile");
        String usr = request.getParameter("usr");
        String pwd = request.getParameter("pwd");
        TaskDefinitionService taskService = createTaskService(request, usr, pwd);
        FormDefinitionService formService = createFormService(request, usr, pwd);
        FormRepresentationEncoder encoder = FormEncodingFactory.getEncoder();
        JsonObject json = new JsonObject();
        json.addProperty("embedded", profile);
        try {
            if ( profile != null && "designer".equals(profile)) {
                String userTask = request.getParameter("userTask");
                String processName = request.getParameter("processName");
                String bpmn2Process = IOUtils.toString(request.getReader());
                TaskRef task = taskService.getBPMN2Task(bpmn2Process, processName, userTask);
                if (task != null) {
                    //get associated form if it exists
                    FormRepresentation form = formService.getAssociatedForm(task.getPackageName(), task);
                    if (form != null) {
                        json.addProperty("formjson", encoder.encode(form));
                    }
                    json.add("task", toJsonObject(task));
                    json.addProperty("packageName", task.getPackageName());
                }
            } else {
                throw new Exception("Unknown profile for POST: " + profile);
            }
            request.setAttribute("jsonData", new Gson().toJson(json));
            request.getRequestDispatcher("/FormBuilder.jsp").forward(request, response);
        } catch (TaskServiceException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Problem getting task from guvnor");
        } catch (FormServiceException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Problem reading form from guvnor");
        } catch (FormEncodingException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Problem encoding form");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    protected FormDefinitionService createFormService(HttpServletRequest request, String usr, String pwd) {
        GuvnorFormDefinitionService service = (GuvnorFormDefinitionService) WebApplicationContextUtils.
            getWebApplicationContext(request.getSession().getServletContext()).getBean("guvnorFormService");
        if (usr != null && pwd != null) {
            service.setUser(usr);
            service.setPassword(pwd);
            try {
                service.afterPropertiesSet();
            } catch (Exception e) { }
        }
        return service;
    }

    protected TaskDefinitionService createTaskService(HttpServletRequest request, String usr, String pwd) {
        GuvnorTaskDefinitionService service = (GuvnorTaskDefinitionService) WebApplicationContextUtils.
            getWebApplicationContext(request.getSession().getServletContext()).getBean("guvnorTaskService");
        if (usr != null && pwd != null) {
            service.setUser(usr);
            service.setPassword(pwd);
            try {
                service.afterPropertiesSet();
            } catch (Exception e) { }
        }
        return service;
    }

    private JsonObject toJsonObject(TaskRef task) {
        JsonObject jsonTask = new JsonObject();
        jsonTask.addProperty("processId", task.getProcessId());
        jsonTask.addProperty("taskId", task.getTaskId());
        jsonTask.addProperty("taskName", task.getTaskName());
        JsonArray jsonInputs = new JsonArray();
        List<TaskPropertyRef> inputs = task.getInputs();
        if (inputs != null) {
            for (TaskPropertyRef input : inputs) {
                JsonObject jsonInput = new JsonObject();
                jsonInput.addProperty("name", input.getName());
                jsonInput.addProperty("sourceExpression", input.getSourceExpresion());
                jsonInputs.add(jsonInput);
            }
        }
        jsonTask.add("inputs", jsonInputs);
        JsonArray jsonOutputs = new JsonArray();
        List<TaskPropertyRef> outputs = task.getOutputs();
        if (outputs != null) {
            for (TaskPropertyRef output : outputs) {
                JsonObject jsonOutput = new JsonObject();
                jsonOutput.addProperty("name", output.getName());
                jsonOutput.addProperty("sourceExpression", output.getSourceExpresion());
                jsonOutputs.add(jsonOutput);
            }
        }
        jsonTask.add("outputs", jsonOutputs);
        JsonObject jsonMetaData = new JsonObject();
        Map<String, String> metaData = task.getMetaData();
        if (metaData != null) {
            for (Map.Entry<String, String> entry : metaData.entrySet()) {
                jsonMetaData.addProperty(entry.getKey(), entry.getValue());
            }
        }
        jsonTask.add("metaData", jsonMetaData);
        return jsonTask;
    }
}
