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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
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

import org.jbpm.kie.services.api.FormProviderService;
import org.jbpm.kie.services.api.RuntimeDataService;
import org.jbpm.kie.services.api.bpmn2.BPMN2DataService;
import org.jbpm.kie.services.impl.model.ProcessDesc;
import org.jbpm.form.builder.services.model.InputData;
import org.jbpm.form.builder.services.model.OutputData;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskContentService;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.TaskQueryService;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

@ApplicationScoped
public class FormProviderServiceImpl implements FormProviderService {

    @Inject
    private TaskQueryService queryService;
    @Inject
    private TaskContentService contentService;
    @Inject
    private TaskInstanceService instanceService;
    @Inject
    private BPMN2DataService bpmn2Service;
    @Inject
    private RuntimeDataService dataService;    
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
    public String getFormDisplayProcess(String processId) {
        ProcessDesc processDesc = dataService.getProcessById(processId);
        InputStream template = null;
        
        for (FormProvider provider : providers) {
            template = provider.provideProcessForm(processDesc);
            if (template != null) {
                break;
            }
        }
        
        Map<String, String> processData = bpmn2Service.getProcessData(processId);
        if (processData == null) {
            processData = new HashMap<String, String>();
        }
        
        Map<String, Object> renderContext = new HashMap<String, Object>();
        renderContext.put("process", processDesc);
        renderContext.put("outputs", processData);
        return render(processDesc.getName(), template, renderContext);

    }

    @Override
    @SuppressWarnings("unchecked")
    public String getFormDisplayTask(long taskId) {
        Task task = queryService.getTaskInstanceById(taskId);
        String name = task.getNames().get(0).getText();
        ProcessDesc processDesc = dataService.getProcessById(task.getTaskData().getProcessId());
        Map<String, Object> renderContext = new HashMap<String, Object>();
        InputStream template = null;
        
        // read task variables
        Object input = null;
        long inputContentId = task.getTaskData().getDocumentContentId();
        if (inputContentId != -1) {
            Content content = contentService.getContentById(inputContentId);
            input = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        }
        if (input == null) {
            input = new HashMap<String, String>();
        }

        Object output = null;
        long outputContentId = task.getTaskData().getOutputContentId();
        if (outputContentId != -1) {
            Content content = contentService.getContentById(outputContentId);
            output = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        }
        if (output == null) {
            output = new HashMap<String, String>();
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
        Map<String, String> finalOutput = new HashMap<String, String>();
        for (String key : taskOutputMappings.values()) {
            
            String value = ((Map<String, String>) output).get(key);
            if (value == null) {
                value = "";
            }
            finalOutput.put(key, value);
        }


        // merge template with process variables        
        renderContext.put("task", task);        
        renderContext.put("outputs", finalOutput);

        // add all inputs as direct entries
        if (input instanceof Map) {
            renderContext.put("inputs", input);
            for (Map.Entry<String, Object> inputVar : ((Map<String, Object>)input).entrySet()) {
                renderContext.put(inputVar.getKey(), inputVar.getValue());
            }
        } else {
            renderContext.put("input", input);
        }
        
        // find form
        for (FormProvider provider : providers) {
            template = provider.provideTaskForm(task, processDesc);
            if (template != null) {
                break;
            }
        }
        return render(name, template, renderContext);
    }

    public String render(String name, InputStream src, Map<String, Object> renderContext) {
        String str = null;
        try {
            freemarker.template.Configuration cfg = new freemarker.template.Configuration();
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            cfg.setTemplateUpdateDelay(0);
            Template temp = new Template(name, new InputStreamReader(src), cfg);
            //final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            StringWriter out = new StringWriter();
            temp.process(renderContext, out);
            out.flush();
            str = out.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to process form template", e);
        }
        return str;
    }

    protected Map<String, InputData> toInputDataMap(Map<String, String> inputs) {
        Map<String, InputData> retval = new HashMap<String, InputData>();
        if (inputs != null) {
            for (String key : inputs.keySet()) {
                InputData in = new InputData();
                in.setName(key);
                retval.put(key, in);
            }
        }
        return retval;
    }

    protected Map<String, OutputData> toOutputDataMap(Map<String, String> outputs) {
        Map<String, OutputData> retval = new HashMap<String, OutputData>();
        if (outputs != null) {
            for (String key : outputs.keySet()) {
                OutputData out = new OutputData();
                out.setName(key);
                retval.put(key, out);
            }
        }
        return retval;
    }
}
