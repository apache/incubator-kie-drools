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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.droolsjbpm.services.api.FormProviderService;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.jbpm.form.builder.services.model.InputData;
import org.jbpm.form.builder.services.model.OutputData;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.kie.commons.java.nio.file.Path;
import org.kie.internal.task.api.TaskContentService;
import org.kie.internal.task.api.TaskInstanceService;
import org.kie.internal.task.api.TaskQueryService;
import org.kie.internal.task.api.model.Content;
import org.kie.internal.task.api.model.I18NText;
import org.kie.internal.task.api.model.Task;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.droolsjbpm.services.api.RuntimeDataService;

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
    private FileService fileService;
    private Map<String /*className*/, List<String>> effectsForItem = new HashMap<String, List<String>>();
    private Map<String /*className*/, List<String>> actionsForItem = new HashMap<String, List<String>>();

    public void putEffectsForItem(String className, List<String> effectClassNames) {
        this.effectsForItem.put(className, effectClassNames);
    }

    public void putActionsForItem(String className, List<String> actionClassNames) {
        this.actionsForItem.put(className, actionClassNames);
    }

    @Override
    public String getFormDisplayProcess(String processId) {
        String processAssetPath = dataService.getProcessById(processId).getOriginalPath();
        Iterable<Path> availableForms = null;
        Path processPath = fileService.getPath(processAssetPath);
        Path formsPath = fileService.getPath(processPath.getParent().toUri().toString() + "/forms/");
        try {
           
            if(fileService.exists(formsPath)){
                availableForms = fileService.loadFilesByType(formsPath, "ftl");
            }
        } catch (FileException ex) {
            Logger.getLogger(FormProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        Path selectedForm = null;
        if(availableForms != null){
            for (Path p : availableForms) {
                if (p.getFileName().toString().contains(processId)) {
                    selectedForm = p;
                }
            }
        }
        InputStream template = null;
        try {
            if (selectedForm == null) {
                String rootPath = processPath.getRoot().toUri().toString();
                if (!rootPath.endsWith(processPath.getFileSystem().getSeparator())) {
                    rootPath +=processPath.getFileSystem().getSeparator();
                }
                
                Path defaultFormPath = fileService.getPath(rootPath +"globals/forms/DefaultProcess.ftl"); 
                template = new ByteArrayInputStream(fileService.loadFile(defaultFormPath));

            } else {

                template = new ByteArrayInputStream(fileService.loadFile(selectedForm));

            }
        } catch (FileException ex) {
            Logger.getLogger(FormProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String processString = "";
        try{
            processString = new String(fileService.loadFile(processPath));
        }catch(Exception e){
        
        }
                
        Map<String, String> processData = bpmn2Service.getProcessData(processId);
        if (processData == null) {
            processData = new HashMap<String, String>();
        }
        ProcessDesc processDesc = bpmn2Service.getProcessDesc(processId);
        Map<String, Object> renderContext = new HashMap<String, Object>();
        renderContext.put("process", processDesc);
        renderContext.put("outputs", processData);
        return render(processDesc.getName(), template, renderContext);

    }

    @Override
    public String getFormDisplayTask(long taskId) {
        Task task = queryService.getTaskInstanceById(taskId);
        Map<String, Object> renderContext = new HashMap<String, Object>();
        String processAssetPath = "";
        Path processPath = null;
        if(task.getTaskData().getProcessId() != null && !task.getTaskData().getProcessId().equals("") ){
            processAssetPath = dataService.getProcessById(task.getTaskData().getProcessId()).getOriginalPath();
        }
        
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

        // check if a template exists
        String name = null;
        List<I18NText> names = task.getNames();
        for (I18NText text : names) {
            if ("en-UK".equals(text.getLanguage())) {
                name = text.getText();
            }
        }
        Iterable<Path> availableForms = null;
        try {
            if(processAssetPath != null && !processAssetPath.equals("")){
                processPath = fileService.getPath(processAssetPath);
                Path formsPath = fileService.getPath(processPath.getParent().toUri().toString() + "/forms/");
                if(fileService.exists(formsPath)){
                    availableForms = fileService.loadFilesByType(formsPath, "ftl");
                }
            }
        } catch (FileException ex) {
            Logger.getLogger(FormProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        Path selectedForm = null;
        if(availableForms != null){
            for (Path p : availableForms) {
                if (p.getFileName().toString().contains(task.getNames().get(0).getText())) {
                    selectedForm = p;
                }
            }
        }
        InputStream template = null;
        try {
            if (selectedForm == null) {
                String rootPath = processPath.getRoot().toUri().toString();
                if (!rootPath.endsWith(processPath.getFileSystem().getSeparator())) {
                    rootPath +=processPath.getFileSystem().getSeparator();
                }
                // since we use default task that lists all inputs there needs to be complete map available
                renderContext.put("inputs", input);
                Path defaultFormPath = fileService.getPath(rootPath +"globals/forms/DefaultTask.ftl");
                template = new ByteArrayInputStream(fileService.loadFile(defaultFormPath));

            } else {

                template = new ByteArrayInputStream(fileService.loadFile(selectedForm));

            }
        } catch (FileException ex) {
            Logger.getLogger(FormProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

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
            for (Map.Entry<String, Object> inputVar : ((Map<String, Object>)input).entrySet()) {
                renderContext.put(inputVar.getKey(), inputVar.getValue());
            }
        } else {
            renderContext.put("input", input);
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

//    @Override
//    public FormRepresentation getAssociatedForm(String processName, String taskName) {
//    	try {
//			TaskDef actualTask = null;
//    		List<FormRepresentation> forms = formService.getForms();
//	        FormRepresentation retval = null;
//	        for (FormRepresentation form : forms) {
//	        	boolean sameTask = (taskName == null && form.getTaskId() == null) && 
//	        			(form.getTaskId() != null && form.getTaskId().equals(taskName));
//	        	boolean sameProcess = form.getProcessName() != null && form.getProcessName().equals(processName);
//	            if (sameTask && sameProcess) {
//	                retval = form;
//	                break;
//	            }
//	        }
//	        return retval;
//    	} catch (Exception e) {
//    		throw new RuntimeException("Failed to get associated form for taskName " + taskName, e);
//    	}
//    }
//    @Override
//    public FormRepresentation createFormFromTask(Map<String,String> inputs, Map<String,String> outputs, TaskDef task) {
//        FormRepresentation form = new FormRepresentation();
//
//        /*        
//        List<String> headerEffects = get(this.effectsForItem, "HeaderMenuItem");
//        List<String> tableEffects = get(this.effectsForItem, "TableLayoutMenuItem");
//        List<String> labelEffects = get(this.effectsForItem, "LabelMenuItem");
//        List<String> textfieldEffects = get(this.effectsForItem, "TextFieldMenuItem");
//        List<String> completeButtonEffects = get(this.effectsForItem, "CompleteButtonMenuItem");
//        
//        List<String> headerActions = get(this.actionsForItem, "HeaderMenuItem");
//        List<String> tableActions = get(this.actionsForItem, "TableLayoutMenuItem");
//        List<String> labelActions = get(this.actionsForItem, "LabelMenuItem");
//        List<String> textfieldActions = get(this.actionsForItem, "TextFieldMenuItem");
//        List<String> completeButtonActions = get(this.actionsForItem, "CompleteButtonMenuItem");
//        */
//        
//        form.setInputs(toInputDataMap(inputs));
//        form.setOutputs(toOutputDataMap(outputs));
//        if (task.getName() != null) {
//            HeaderRepresentation header = new HeaderRepresentation();
//            header.setValue("Task: " + task.getName());
//            //header.setEffectClasses(headerEffects); TODO
//            //header.setEventActions(asMapOfNull(headerActions, ScriptRepresentation.class)); TODO
//            form.addFormItem(header);
//        }
//        if (inputs != null && !inputs.isEmpty()) {
//            TableRepresentation tableOfInputs = new TableRepresentation();
//            tableOfInputs.setRows(inputs.size());
//            tableOfInputs.setColumns(2);
//            tableOfInputs.setHeight("" + (inputs.size() * 30) + "px");
//            //tableOfInputs.setEffectClasses(tableEffects); TODO
//            //tableOfInputs.setEventActions(asMapOfNull(tableActions, ScriptRepresentation.class)); TODO
//            List<String> keys = new ArrayList<String>(inputs.keySet());
//            for (int index = 0; index < inputs.size(); index++) {
//                String key = keys.get(index);
//                LabelRepresentation labelName = new LabelRepresentation();
//                //labelName.setEventActions(asMapOfNull(labelActions, ScriptRepresentation.class)); TODO
//                //labelName.setEffectClasses(labelEffects); TODO
//                //labelName.setValue(key);
//                labelName.setWidth("100px");
//                tableOfInputs.setElement(index, 0, labelName);
//                LabelRepresentation labelValue = new LabelRepresentation();
//                //labelValue.setEventActions(asMapOfNull(labelActions, ScriptRepresentation.class)); TODO
//                //labelValue.setEffectClasses(labelEffects); TODO
//                labelValue.setWidth("200px");
//                InputData data = new InputData();
//                data.setName(key);
//                data.setValue(inputs.get(key));
//                data.setMimeType("multipart/form-data");
//                data.setFormatter(new Formatter() {
//                    @Override
//                    public Object format(Object object) {
//                        return object;
//                    }
//                    @Override
//                    public Map<String, Object> getDataMap() {
//                        return new HashMap<String, Object>();
//                    }
//                });
//                labelValue.setInput(data);
//                labelValue.setValue("{variable}");
//                tableOfInputs.setElement(index, 1, labelValue);
//            }
//            LabelRepresentation labelInputs = new LabelRepresentation();
//            //labelInputs.setEventActions(asMapOfNull(labelActions, ScriptRepresentation.class)); TODO
//            //labelInputs.setEffectClasses(labelEffects); TODO
//            labelInputs.setValue("Inputs:");
//            form.addFormItem(labelInputs);
//            form.addFormItem(tableOfInputs);
//        }
//        if (outputs != null && !outputs.isEmpty()) {
//        	List<String> keys = new ArrayList<String>(outputs.keySet());
//            TableRepresentation tableOfOutputs = new TableRepresentation();
//            tableOfOutputs.setRows(outputs.size());
//            tableOfOutputs.setColumns(2);
//            tableOfOutputs.setHeight("" + (outputs.size() * 30) + "px");
//            //tableOfOutputs.setEffectClasses(tableEffects); TODO
//            //tableOfOutputs.setEventActions(asMapOfNull(tableActions, ScriptRepresentation.class)); TODO
//            for (int index = 0; index < outputs.size(); index++) {
//            	String key = keys.get(index);
//                LabelRepresentation labelName = new LabelRepresentation();
//                //labelName.setEventActions(asMapOfNull(labelActions, ScriptRepresentation.class)); TODO
//                //labelName.setEffectClasses(labelEffects); TODO
//                labelName.setValue(key);
//                labelName.setWidth("100px");
//                tableOfOutputs.setElement(index, 0, labelName);
//                TextFieldRepresentation textField = new TextFieldRepresentation();
//                //textField.setEventActions(asMapOfNull(textfieldActions, ScriptRepresentation.class)); TODO
//                textField.setWidth("200px");
//                //textField.setEffectClasses(textfieldEffects); TODO
//                OutputData data = new OutputData();
//                data.setName(key);
//                data.setValue(outputs.get(key));
//                data.setMimeType("multipart/form-data");
//                data.setFormatter(new Formatter() {
//                    @Override
//                    public Object format(Object object) {
//                        return object;
//                    }
//                    @Override
//                    public Map<String, Object> getDataMap() {
//                        return new HashMap<String, Object>();
//                    }
//                });
//                textField.setOutput(data);
//                tableOfOutputs.setElement(index, 1, textField);
//            }
//            LabelRepresentation labelOutputs = new LabelRepresentation();
//            //labelOutputs.setEventActions(asMapOfNull(labelActions, ScriptRepresentation.class)); TODO
//            //labelOutputs.setEffectClasses(labelEffects); TODO
//            labelOutputs.setValue("Outputs:");
//            form.addFormItem(labelOutputs);
//            form.addFormItem(tableOfOutputs);
//        }
//        CompleteButtonRepresentation completeButton = new CompleteButtonRepresentation();
//        completeButton.setText("Complete");
//        //completeButton.setEffectClasses(completeButtonEffects); TODO
//        //completeButton.setEventActions(asMapOfNull(completeButtonActions, ScriptRepresentation.class)); TODO
//        form.addFormItem(completeButton);
//        form.setAction("complete");
//        form.setEnctype("multipart/form-data");
//        form.setMethod("POST");
//        form.setName(task.getName() + "AutoForm");
//        //form.setProcessName(task.getProcessId()); TODO
//        form.setTaskId(task.getName());
//        return form;
//    }
//     
//    protected <T> Map<String, T> asMapOfNull(List<String> keys, Class<T> type) {
//        Map<String, T> retval = new HashMap<String, T>();
//        if (keys != null) {
//            for (String key : keys) {
//                retval.put(key, null);
//            }
//        }
//        return retval;
//    }
//    
//    public List<String> get(Map<String, List<String>> map, String keyPart) {
//        for (String key : map.keySet()) {
//            if (key.contains(keyPart)) {
//                return map.get(key);
//            }
//        }
//        return null;
//    }
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
