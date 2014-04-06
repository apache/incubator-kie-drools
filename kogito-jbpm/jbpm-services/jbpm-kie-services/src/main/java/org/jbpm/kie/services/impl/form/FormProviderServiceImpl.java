/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.kie.services.impl.form;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.drools.core.util.StringUtils;
import org.jbpm.kie.services.api.FormProviderService;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.api.bpmn2.BPMN2DataService;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.services.task.impl.TaskContentRegistry;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.internal.deployment.DeployedUnit;
import org.kie.internal.deployment.DeploymentService;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class FormProviderServiceImpl implements FormProviderService {

    private static Logger logger = LoggerFactory.getLogger(FormProviderServiceImpl.class);

    @Inject
    private TaskService taskService;
    @Inject
    private BPMN2DataService bpmn2Service;
    @Inject
    private RuntimeDataService dataService;
    @Inject
    private DeploymentService deploymentService;
    @Inject
    @Any
    private Instance<FormProvider> providersInjected;
    private Set<FormProvider> providers;

    @PostConstruct
    public void prepare() {
        providers = new TreeSet<FormProvider>(new Comparator<FormProvider>() {

            @Override
            public int compare(FormProvider o1, FormProvider o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });
        for (FormProvider p : providersInjected) {
            providers.add(p);
        }
    }

    @Override
    public String getFormDisplayProcess(String deploymentId, String processId) {
        ProcessAssetDesc processDesc = dataService.getProcessesByDeploymentIdProcessId(deploymentId, processId);
        Map<String, String> processData = bpmn2Service.getProcessData(processId);

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
        Task task = taskService.getTaskById(taskId);
        if (task == null) {
            return "";
        }
        String name = task.getNames().get(0).getText();
        ProcessAssetDesc processDesc = dataService.getProcessesByDeploymentIdProcessId(task.getTaskData().getDeploymentId(), task.getTaskData().getProcessId());
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
        String processId = task.getTaskData().getProcessId();
        Map<String, String> taskOutputMappings = null;
        if (processId != null && !processId.equals("")) {

            taskOutputMappings = bpmn2Service.getTaskOutputMappings(processId, task.getNames().iterator().next().getText());

        }
        if (taskOutputMappings == null) {
            taskOutputMappings = new HashMap<String, String>();
        }

        // I need to replace the value that comes from the 
        //process mappings with the value that can be stored in the output Content
        Map<String, Object> finalOutput = new HashMap<String, Object>();
        for (String key : taskOutputMappings.values()) {

            Object value = ((Map<String, Object>) output).get(key);
            if (value == null) {
                value = "";
            }
            finalOutput.put(key, value);
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

        logger.warn("Unable to find form to render for task '{}' on process '{}'", name, processDesc.getName());
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
