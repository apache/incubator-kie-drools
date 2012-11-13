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
package org.droolsjbpm.services.impl.form;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.droolsjbpm.services.api.FormProviderService;
import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.jbpm.form.builder.services.api.FormDefinitionService;
import org.jbpm.form.builder.services.model.FormRepresentation;
import org.jbpm.form.builder.services.model.Formatter;
import org.jbpm.form.builder.services.model.InputData;
import org.jbpm.form.builder.services.model.OutputData;
import org.jbpm.form.builder.services.model.items.CompleteButtonRepresentation;
import org.jbpm.form.builder.services.model.items.HeaderRepresentation;
import org.jbpm.form.builder.services.model.items.LabelRepresentation;
import org.jbpm.form.builder.services.model.items.TableRepresentation;
import org.jbpm.form.builder.services.model.items.TextFieldRepresentation;
import org.jbpm.task.Content;
import org.jbpm.task.I18NText;
import org.jbpm.task.Task;
import org.jbpm.task.TaskDef;
import org.jbpm.task.api.TaskContentService;
import org.jbpm.task.api.TaskInstanceService;
import org.jbpm.task.api.TaskQueryService;
import org.jbpm.task.utils.ContentMarshallerHelper;

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
    private KnowledgeDomainService domainService;
    @Inject
    private FormDefinitionService formService;

    private Map<String /*className*/, List<String>> effectsForItem = new HashMap<String, List<String>>();
    private Map<String /*className*/, List<String>> actionsForItem = new HashMap<String, List<String>>();

    public void putEffectsForItem(String className, List<String> effectClassNames) {
        this.effectsForItem.put(className, effectClassNames);
    }
    
    public void putActionsForItem(String className, List<String> actionClassNames) {
        this.actionsForItem.put(className, actionClassNames);
    }
    
    public String getFormDisplay(long taskId) {
        Task task = queryService.getTaskInstanceById(taskId);


        Object input = null;
        long contentId = task.getTaskData().getDocumentContentId();
        if (contentId != -1) {
            Content content = contentService.getContentById(contentId);
            input = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        }
        if(input == null){
            input = new HashMap<String, String>();
        }

        // check if a template exists
        String name = null;
        List<I18NText> names = task.getNames();
        for (I18NText text : names) {
            if ("en-UK".equals(text.getLanguage())) {
                name = text.getText();
            }
        }


        InputStream template = getClass().getResourceAsStream("/ftl/DefaultTask.ftl");
        String processId = task.getTaskData().getProcessId();
        Map<String, String> taskOutputMappings = null;
        if (processId != null && !processId.equals("")) {
            String processDef = domainService.getAvailableProcesses().get(processId);
            
            if (processDef != null && !processDef.equals("")) {
   
                taskOutputMappings = bpmn2Service.getTaskOutputMappings(processDef, task.getNames().iterator().next().getText());
            }
        }
        if(taskOutputMappings == null){
             taskOutputMappings = new HashMap<String, String>();
        }

        // merge template with process variables
        Map<String, Object> renderContext = new HashMap<String, Object>();
        renderContext.put("task", task);
        renderContext.put("inputs", input);
        renderContext.put("outputs", taskOutputMappings);

        return render(name, template, renderContext);


    }
    
    public String getProcessFormDisplay(String bpmn2) {
        ProcessDesc desc = bpmn2Service.getProcessDesc(bpmn2);
	String processName = desc.getId();
        Map<String, String> processData = bpmn2Service.getProcessData(bpmn2);


        


        InputStream template = getClass().getResourceAsStream("/ftl/"+processName+".ftl");
        

        // merge template with process variables
        Map<String, Object> renderContext = new HashMap<String, Object>();
        
        renderContext.put("processData", processData);
        

        return render(processName, template, renderContext);


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
    
    @Override
    public FormRepresentation getAssociatedForm(String bpmn2, String taskName) {
    	try {
    		ProcessDesc desc = bpmn2Service.getProcessDesc(bpmn2);
			String processName = desc.getId();
			TaskDef actualTask = null;
    		List<FormRepresentation> forms = formService.getForms();
	        FormRepresentation retval = null;
	        for (FormRepresentation form : forms) {
	        	boolean sameTask = (taskName == null && form.getTaskId() == null) && 
	        			(form.getTaskId() != null && form.getTaskId().equals(taskName));
	        	boolean sameProcess = form.getProcessName() != null && form.getProcessName().equals(processName);
	            if (sameTask && sameProcess) {
	                retval = form;
	                break;
	            }
	        }
	        return retval;
    	} catch (Exception e) {
    		throw new RuntimeException("Failed to get associated form for taskName " + taskName, e);
    	}
    }
    
    @Override
    public FormRepresentation createFormFromTask(Map<String,String> inputs, Map<String,String> outputs, TaskDef task) {
        FormRepresentation form = new FormRepresentation();

        /*        
        List<String> headerEffects = get(this.effectsForItem, "HeaderMenuItem");
        List<String> tableEffects = get(this.effectsForItem, "TableLayoutMenuItem");
        List<String> labelEffects = get(this.effectsForItem, "LabelMenuItem");
        List<String> textfieldEffects = get(this.effectsForItem, "TextFieldMenuItem");
        List<String> completeButtonEffects = get(this.effectsForItem, "CompleteButtonMenuItem");
        
        List<String> headerActions = get(this.actionsForItem, "HeaderMenuItem");
        List<String> tableActions = get(this.actionsForItem, "TableLayoutMenuItem");
        List<String> labelActions = get(this.actionsForItem, "LabelMenuItem");
        List<String> textfieldActions = get(this.actionsForItem, "TextFieldMenuItem");
        List<String> completeButtonActions = get(this.actionsForItem, "CompleteButtonMenuItem");
        */
        
        form.setInputs(toInputDataMap(inputs));
        form.setOutputs(toOutputDataMap(outputs));
        if (task.getName() != null) {
            HeaderRepresentation header = new HeaderRepresentation();
            header.setValue("Task: " + task.getName());
            //header.setEffectClasses(headerEffects); TODO
            //header.setEventActions(asMapOfNull(headerActions, ScriptRepresentation.class)); TODO
            form.addFormItem(header);
        }
        if (inputs != null && !inputs.isEmpty()) {
            TableRepresentation tableOfInputs = new TableRepresentation();
            tableOfInputs.setRows(inputs.size());
            tableOfInputs.setColumns(2);
            tableOfInputs.setHeight("" + (inputs.size() * 30) + "px");
            //tableOfInputs.setEffectClasses(tableEffects); TODO
            //tableOfInputs.setEventActions(asMapOfNull(tableActions, ScriptRepresentation.class)); TODO
            List<String> keys = new ArrayList<String>(inputs.keySet());
            for (int index = 0; index < inputs.size(); index++) {
                String key = keys.get(index);
                LabelRepresentation labelName = new LabelRepresentation();
                //labelName.setEventActions(asMapOfNull(labelActions, ScriptRepresentation.class)); TODO
                //labelName.setEffectClasses(labelEffects); TODO
                //labelName.setValue(key);
                labelName.setWidth("100px");
                tableOfInputs.setElement(index, 0, labelName);
                LabelRepresentation labelValue = new LabelRepresentation();
                //labelValue.setEventActions(asMapOfNull(labelActions, ScriptRepresentation.class)); TODO
                //labelValue.setEffectClasses(labelEffects); TODO
                labelValue.setWidth("200px");
                InputData data = new InputData();
                data.setName(key);
                data.setValue(inputs.get(key));
                data.setMimeType("multipart/form-data");
                data.setFormatter(new Formatter() {
                    @Override
                    public Object format(Object object) {
                        return object;
                    }
                    @Override
                    public Map<String, Object> getDataMap() {
                        return new HashMap<String, Object>();
                    }
                });
                labelValue.setInput(data);
                labelValue.setValue("{variable}");
                tableOfInputs.setElement(index, 1, labelValue);
            }
            LabelRepresentation labelInputs = new LabelRepresentation();
            //labelInputs.setEventActions(asMapOfNull(labelActions, ScriptRepresentation.class)); TODO
            //labelInputs.setEffectClasses(labelEffects); TODO
            labelInputs.setValue("Inputs:");
            form.addFormItem(labelInputs);
            form.addFormItem(tableOfInputs);
        }
        if (outputs != null && !outputs.isEmpty()) {
        	List<String> keys = new ArrayList<String>(outputs.keySet());
            TableRepresentation tableOfOutputs = new TableRepresentation();
            tableOfOutputs.setRows(outputs.size());
            tableOfOutputs.setColumns(2);
            tableOfOutputs.setHeight("" + (outputs.size() * 30) + "px");
            //tableOfOutputs.setEffectClasses(tableEffects); TODO
            //tableOfOutputs.setEventActions(asMapOfNull(tableActions, ScriptRepresentation.class)); TODO
            for (int index = 0; index < outputs.size(); index++) {
            	String key = keys.get(index);
                LabelRepresentation labelName = new LabelRepresentation();
                //labelName.setEventActions(asMapOfNull(labelActions, ScriptRepresentation.class)); TODO
                //labelName.setEffectClasses(labelEffects); TODO
                labelName.setValue(key);
                labelName.setWidth("100px");
                tableOfOutputs.setElement(index, 0, labelName);
                TextFieldRepresentation textField = new TextFieldRepresentation();
                //textField.setEventActions(asMapOfNull(textfieldActions, ScriptRepresentation.class)); TODO
                textField.setWidth("200px");
                //textField.setEffectClasses(textfieldEffects); TODO
                OutputData data = new OutputData();
                data.setName(key);
                data.setValue(outputs.get(key));
                data.setMimeType("multipart/form-data");
                data.setFormatter(new Formatter() {
                    @Override
                    public Object format(Object object) {
                        return object;
                    }
                    @Override
                    public Map<String, Object> getDataMap() {
                        return new HashMap<String, Object>();
                    }
                });
                textField.setOutput(data);
                tableOfOutputs.setElement(index, 1, textField);
            }
            LabelRepresentation labelOutputs = new LabelRepresentation();
            //labelOutputs.setEventActions(asMapOfNull(labelActions, ScriptRepresentation.class)); TODO
            //labelOutputs.setEffectClasses(labelEffects); TODO
            labelOutputs.setValue("Outputs:");
            form.addFormItem(labelOutputs);
            form.addFormItem(tableOfOutputs);
        }
        CompleteButtonRepresentation completeButton = new CompleteButtonRepresentation();
        completeButton.setText("Complete");
        //completeButton.setEffectClasses(completeButtonEffects); TODO
        //completeButton.setEventActions(asMapOfNull(completeButtonActions, ScriptRepresentation.class)); TODO
        form.addFormItem(completeButton);
        form.setAction("complete");
        form.setEnctype("multipart/form-data");
        form.setMethod("POST");
        form.setName(task.getName() + "AutoForm");
        //form.setProcessName(task.getProcessId()); TODO
        form.setTaskId(task.getName());
        return form;
    }
     
    protected <T> Map<String, T> asMapOfNull(List<String> keys, Class<T> type) {
        Map<String, T> retval = new HashMap<String, T>();
        if (keys != null) {
            for (String key : keys) {
                retval.put(key, null);
            }
        }
        return retval;
    }
    
    public List<String> get(Map<String, List<String>> map, String keyPart) {
        for (String key : map.keySet()) {
            if (key.contains(keyPart)) {
                return map.get(key);
            }
        }
        return null;
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
