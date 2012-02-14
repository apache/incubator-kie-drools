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
package org.jbpm.formbuilder.shared.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.shared.api.FBScript;
import org.jbpm.formapi.shared.api.FormRepresentation;
import org.jbpm.formapi.shared.api.Formatter;
import org.jbpm.formapi.shared.api.InputData;
import org.jbpm.formapi.shared.api.OutputData;
import org.jbpm.formapi.shared.api.items.CompleteButtonRepresentation;
import org.jbpm.formapi.shared.api.items.HeaderRepresentation;
import org.jbpm.formapi.shared.api.items.LabelRepresentation;
import org.jbpm.formapi.shared.api.items.TableRepresentation;
import org.jbpm.formapi.shared.api.items.TextFieldRepresentation;
import org.jbpm.formbuilder.shared.task.TaskPropertyRef;
import org.jbpm.formbuilder.shared.task.TaskRef;

public abstract class AbstractBaseFormDefinitionService {

    private static final String FORM_ID_PREFIX = "formDefinition_";
    private static final String ITEM_ID_PREFIX = "formItemDefinition_";
    
    private Map<String /*className*/, List<String>> effectsForItem = new HashMap<String, List<String>>();
    private Map<String /*className*/, List<String>> actionsForItem = new HashMap<String, List<String>>();
    
    /**
     * @param form FormRepresentation with name to be changed
     * @return true if its an update, false if it is an insert
     */
    protected boolean updateFormName(FormRepresentation form) {
        if (form.getName() == null || "null".equals(form.getName()) || "".equals(form.getName())) {
            form.setName(FORM_ID_PREFIX + System.currentTimeMillis());
            return false;
        } else if (!form.getName().startsWith(FORM_ID_PREFIX)){
            form.setName(FORM_ID_PREFIX + form.getName());
            return false;
        }
        return true;
    }
    
    protected boolean updateItemName(String formItemName, StringBuilder returnName) {
        if (formItemName == null || "null".equals(formItemName) || "".equals(formItemName)) {
            returnName.append(ITEM_ID_PREFIX).append(System.currentTimeMillis());
            return false;
        } else if (!formItemName.startsWith(ITEM_ID_PREFIX)){
            returnName.append(ITEM_ID_PREFIX).append(formItemName);
            return false;
        }
        returnName.append(formItemName);
        return true;
    }
    
    protected boolean isItemName(String assetId) {
        return assetId.startsWith(ITEM_ID_PREFIX) && assetId.endsWith(".json");
    }
    
    protected boolean isFormName(String assetId) {
        return assetId.endsWith(".formdef");
    }
    
    public void putEffectsForItem(String className, List<String> effectClassNames) {
        this.effectsForItem.put(className, effectClassNames);
    }
    
    public void putActionsForItem(String className, List<String> actionClassNames) {
        this.actionsForItem.put(className, actionClassNames);
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
    
    public FormRepresentation createFormFromTask(TaskRef task) {
        if (task == null) {
            return null;
        }
        
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
        
        FormRepresentation form = new FormRepresentation();
        form.setInputs(toInputDataMap(task.getInputs()));
        form.setOutputs(toOutputDataMap(task.getOutputs()));
        if (task.getTaskId() != null) {
            HeaderRepresentation header = new HeaderRepresentation();
            header.setValue("Task: " + task.getTaskId());
            header.setEffectClasses(headerEffects);
            header.setEventActions(asMapOfNull(headerActions, FBScript.class));
            form.addFormItem(header);
        }
        List<TaskPropertyRef> inputs = task.getInputs();
        if (inputs != null && !inputs.isEmpty()) {
            TableRepresentation tableOfInputs = new TableRepresentation();
            tableOfInputs.setRows(inputs.size());
            tableOfInputs.setColumns(2);
            tableOfInputs.setHeight("" + (inputs.size() * 30) + "px");
            tableOfInputs.setEffectClasses(tableEffects);
            tableOfInputs.setEventActions(asMapOfNull(tableActions, FBScript.class));
            for (int index = 0; index < inputs.size(); index++) {
                TaskPropertyRef input = inputs.get(index);
                LabelRepresentation labelName = new LabelRepresentation();
                labelName.setEventActions(asMapOfNull(labelActions, FBScript.class));
                labelName.setEffectClasses(labelEffects);
                labelName.setValue(input.getName());
                labelName.setWidth("100px");
                tableOfInputs.setElement(index, 0, labelName);
                LabelRepresentation labelValue = new LabelRepresentation();
                labelValue.setEventActions(asMapOfNull(labelActions, FBScript.class));
                labelValue.setEffectClasses(labelEffects);
                labelValue.setWidth("200px");
                InputData data = new InputData();
                data.setName(input.getName());
                data.setValue(input.getSourceExpresion());
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
            labelInputs.setEventActions(asMapOfNull(labelActions, FBScript.class));
            labelInputs.setEffectClasses(labelEffects);
            labelInputs.setValue("Inputs:");
            form.addFormItem(labelInputs);
            form.addFormItem(tableOfInputs);
        }
        List<TaskPropertyRef> outputs = task.getOutputs();
        if (outputs != null && !outputs.isEmpty()) {
            TableRepresentation tableOfOutputs = new TableRepresentation();
            tableOfOutputs.setRows(outputs.size());
            tableOfOutputs.setColumns(2);
            tableOfOutputs.setHeight("" + (outputs.size() * 30) + "px");
            tableOfOutputs.setEffectClasses(tableEffects);
            tableOfOutputs.setEventActions(asMapOfNull(tableActions, FBScript.class));
            for (int index = 0; index < outputs.size(); index++) {
                TaskPropertyRef output = outputs.get(index);
                LabelRepresentation labelName = new LabelRepresentation();
                labelName.setEventActions(asMapOfNull(labelActions, FBScript.class));
                labelName.setEffectClasses(labelEffects);
                labelName.setValue(output.getName());
                labelName.setWidth("100px");
                tableOfOutputs.setElement(index, 0, labelName);
                TextFieldRepresentation textField = new TextFieldRepresentation();
                textField.setEventActions(asMapOfNull(textfieldActions, FBScript.class));
                textField.setWidth("200px");
                textField.setEffectClasses(textfieldEffects);
                OutputData data = new OutputData();
                data.setName(output.getName());
                data.setValue(output.getSourceExpresion());
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
            labelOutputs.setEventActions(asMapOfNull(labelActions, FBScript.class));
            labelOutputs.setEffectClasses(labelEffects);
            labelOutputs.setValue("Outputs:");
            form.addFormItem(labelOutputs);
            form.addFormItem(tableOfOutputs);
        }
        CompleteButtonRepresentation completeButton = new CompleteButtonRepresentation();
        completeButton.setText("Complete");
        completeButton.setEffectClasses(completeButtonEffects);
        completeButton.setEventActions(asMapOfNull(completeButtonActions, FBScript.class));
        form.addFormItem(completeButton);
        form.setAction("complete");
        form.setEnctype("multipart/form-data");
        form.setMethod("POST");
        form.setName(task.getTaskId() + "AutoForm");
        form.setProcessName(task.getProcessId());
        form.setTaskId(task.getTaskId());
        return form;
    }
    
    protected Map<String, InputData> toInputDataMap(List<TaskPropertyRef> inputs) {
        Map<String, InputData> retval = new HashMap<String, InputData>();
        for (TaskPropertyRef ref : inputs) {
            InputData in = new InputData();
            in.setName(ref.getName());
            retval.put(ref.getName(), in);
        }
        return retval;
    }
    
    protected Map<String, OutputData> toOutputDataMap(List<TaskPropertyRef> outputs) {
        Map<String, OutputData> retval = new HashMap<String, OutputData>();
        for (TaskPropertyRef ref : outputs) {
            OutputData out = new OutputData();
            out.setName(ref.getName());
            retval.put(ref.getName(), out);
        }
        return retval;
    }
}
