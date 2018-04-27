/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.kie.services.impl.form;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.core.util.StringUtils;
import org.jbpm.kie.services.api.FormProviderService;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.task.commands.GetUserTaskCommand;
import org.jbpm.services.task.impl.TaskContentRegistry;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FormProviderServiceImpl implements FormProviderService {

    private static Logger logger = LoggerFactory.getLogger(FormProviderServiceImpl.class);


    private TaskService taskService;

    private DefinitionService bpmn2Service;

    private RuntimeDataService dataService;

    private DeploymentService deploymentService;

    private IdentityProvider identityProvider;

    public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public void setBpmn2Service(DefinitionService bpmn2Service) {
		this.bpmn2Service = bpmn2Service;
	}

	public void setDataService(RuntimeDataService dataService) {
		this.dataService = dataService;
	}

	public void setDeploymentService(DeploymentService deploymentService) {
		this.deploymentService = deploymentService;
	}

	public void setProviders(Set<FormProvider> providers) {
		this.providers = providers;
	}

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    private Set<FormProvider> providers;

    @Override
    public String getFormDisplayProcess(String deploymentId, String processId) {
    	ProcessDefinition processDesc = dataService.getProcessesByDeploymentIdProcessId(deploymentId, processId);
        Map<String, String> processData = bpmn2Service.getProcessVariables(deploymentId, processId);

        if (processData == null) {
            processData = new HashMap<String, String>();
        }

        Map<String, Object> renderContext = new HashMap<String, Object>();
        renderContext.put("process", processDesc);
        renderContext.put("outputs", processData);
        renderContext.put("marshallerContext", getMarshallerContext(deploymentId, processId));

        for (FormProvider provider : providers) {
            String template = provider.render(processDesc.getName(), processDesc, renderContext);
            if (!StringUtils.isEmpty(template)) {
                return template;
            }
        }

        logger.warn("Unable to find form to render for process '{}'", processDesc.getName());
        return "";
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getFormDisplayTask(long taskId) {
        Task task = taskService.execute(new GetUserTaskCommand(identityProvider.getName(), taskId));
        if (task == null) {
            return "";
        }
        String name = task.getName();
        final String deploymentId = task.getTaskData().getDeploymentId();
        final String processId = task.getTaskData().getProcessId();
        ProcessDefinition processDesc = null;
        if(deploymentId != null && processId != null) {
            processDesc = dataService.getProcessesByDeploymentIdProcessId(deploymentId, processId);
        }
        Map<String, Object> renderContext = new HashMap<String, Object>();

        ContentMarshallerContext marshallerContext = getMarshallerContext(task);
        // read task variables
        Object input = null;
        long inputContentId = task.getTaskData().getDocumentContentId();
        if (inputContentId != -1) {
            Content content = taskService.getContentById(inputContentId);
            input = ContentMarshallerHelper.unmarshall(content.getContent(), marshallerContext.getEnvironment(), marshallerContext.getClassloader());
        }
        if (input == null) {
            input = new HashMap<String, Object>();
        }

        Object output = null;
        long outputContentId = task.getTaskData().getOutputContentId();
        if (outputContentId != -1) {
            Content content = taskService.getContentById(outputContentId);
            output = ContentMarshallerHelper.unmarshall(content.getContent(), marshallerContext.getEnvironment(), marshallerContext.getClassloader());
        }
        if (output == null) {
            output = new HashMap<String, Object>();
        }

        // prepare task variables for rendering
        Map<String, Object> finalOutput = new HashMap<String, Object>();

        if (processId != null && !processId.equals("")) {
            // If task has an associated process let's merge the outputs
            Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings(deploymentId, processId, task.getName());
            if (taskOutputMappings == null) {
                taskOutputMappings = new HashMap<String, String>();
            }

            // I need to replace the value that comes from the
            //process mappings with the value that can be stored in the output Content
            for (String key : taskOutputMappings.keySet()) {
                Object value = ((Map<String, Object>) output).get(key);
                if (value == null) {
                    value = "";
                }
                finalOutput.put(key, value);
            }

        } else if (output instanceof Map && !((Map)output).isEmpty()) {
            // If the task doesn't belongs to any project BUT it has outputs let's add them directly to the rendering context.
            finalOutput.putAll( (Map<String, Object>) output );
        }

        // merge template with process variables
        renderContext.put("task", task);
        renderContext.put("marshallerContext", marshallerContext);

        // add all inputs as direct entries
        if (input instanceof Map) {
            renderContext.put("inputs", input);
            for (Map.Entry<String, Object> inputVar : ((Map<String, Object>) input).entrySet()) {
                renderContext.put(inputVar.getKey(), inputVar.getValue());
            }
        } else {
            renderContext.put("input", input);
        }

        // add all outputs as direct entries
        renderContext.put("outputs", finalOutput);
        for (Map.Entry<String, Object> outputVar : ((Map<String, Object>) finalOutput).entrySet()) {
            renderContext.put(outputVar.getKey(), outputVar.getValue());
        }

        // find form
        for (FormProvider provider : providers) {
            String template = provider.render(name, task, processDesc, renderContext);
            if (!StringUtils.isEmpty(template)) {
                return template;
            }
        }

        logger.warn("Unable to find form to render for task '{}' on process '{}'", name, processDesc == null ? "" : processDesc.getName());
        return "";
    }

    protected ContentMarshallerContext getMarshallerContext(String deploymentId, String processId) {
        DeployedUnit deployedUnit = deploymentService.getDeployedUnit(deploymentId);
        if (deployedUnit == null) {
            return new ContentMarshallerContext();
        }
        InternalRuntimeManager manager = (InternalRuntimeManager) deployedUnit.getRuntimeManager();
        return new ContentMarshallerContext(manager.getEnvironment().getEnvironment(), manager.getEnvironment().getClassLoader());
    }

    protected ContentMarshallerContext getMarshallerContext(Task task) {

        if (task == null) {
            return new ContentMarshallerContext();
        }

        return TaskContentRegistry.get().getMarshallerContext(task);
    }
}
