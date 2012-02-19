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
package org.jbpm.formapi.shared.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class FormRepresentation implements Serializable {

    private static final long serialVersionUID = 6207901499318773670L;
    
    private String name;
    private String taskId;
    private String processName;
    private String action;
    private String method;
    private String enctype;
    private List<FBValidation> formValidations = new ArrayList<FBValidation>();
    private List<FormItemRepresentation> formItems = new ArrayList<FormItemRepresentation>();
    private Map<String, OutputData> outputs = new HashMap<String, OutputData>();
    private Map<String, InputData> inputs = new HashMap<String, InputData>();
    private List<FBScript> onLoadScripts = new ArrayList<FBScript>();
    private List<FBScript> onSubmitScripts = new ArrayList<FBScript>();
    
    private long lastModified = System.currentTimeMillis();
    private String documentation;
    private boolean saved = false;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public String getProcessName() {
        return processName;
    }
    
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public List<FBValidation> getFormValidations() {
        return formValidations;
    }

    public void setFormValidations(List<FBValidation> formValidations) {
        this.formValidations = formValidations;
    }
    
    public void addFormValidation(FBValidation formValidation) {
        this.formValidations.add(formValidation);
    }

    public Map<String, InputData> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, InputData> inputs) {
        this.inputs = inputs;
    }
    
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        if (action == null) {
            action = "complete";
        }
        this.action = action;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        if (method == null) {
            method = "POST";
        }
        this.method = method;
    }

    public String getEnctype() {
        return enctype;
    }

    public void setEnctype(String enctype) {
        if (enctype == null) {
            enctype = "multipart/form-data";
        }
        this.enctype = enctype;
    }

    public Map<String, OutputData> getOutputs() {
        return outputs;
    }

    public void setOutputs(Map<String, OutputData> outputs) {
        this.outputs = outputs;
    }
    
    public List<FormItemRepresentation> getFormItems() {
        return formItems;
    }
    
    public void setFormItems(List<FormItemRepresentation> formItems) {
        this.formItems = formItems;
    }
    
    public void addFormItem(FormItemRepresentation formItem) {
        this.formItems.add(formItem);
    }
    
    public List<FBScript> getOnLoadScripts() {
        return onLoadScripts;
    }
    
    public void setOnLoadScripts(List<FBScript> onLoadScripts) {
        this.onLoadScripts = onLoadScripts;
    }
    
    public List<FBScript> getOnSubmitScripts() {
        return onSubmitScripts;
    }
    
    public void setOnSubmitScripts(List<FBScript> onSubmitScripts) {
        this.onSubmitScripts = onSubmitScripts;
    }
    
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
    
    public long getLastModified() {
        return lastModified;
    }
    
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
    
    public String getDocumentation() {
        return documentation;
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    public void setSaved(boolean saved) {
        this.saved = saved;
    }
    
    public void populate(String name, String taskId, String processName, 
            String action, String method, String enctype, String documentation) {
        this.name = name;
        this.taskId = taskId;
        this.processName = processName;
        this.action = action;
        this.method = method;
        this.enctype = enctype;
        this.documentation = documentation;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof FormRepresentation)) return false;
        FormRepresentation other = (FormRepresentation) obj;
        boolean equals = (this.name == null && other.name == null) || (this.name != null && this.name.equals(other.name));
        if (!equals) return equals;
        equals = (this.taskId == null && other.taskId == null) || (this.taskId != null && this.taskId.equals(other.taskId));
        if (!equals) return equals;
        equals = (this.processName == null && other.processName == null) || (this.processName != null && this.processName.equals(other.processName));
        if (!equals) return equals;
        equals = (this.action == null && other.action == null) || (this.action != null && this.action.equals(other.action));
        if (!equals) return equals;
        equals = (this.method == null && other.method == null) || (this.method != null && this.method.equals(other.method));
        if (!equals) return equals;
        equals = (this.enctype == null && other.enctype == null) || (this.enctype != null && this.enctype.equals(other.enctype));
        if (!equals) return equals;
        /*equals = (this.lastModified == other.lastModified);
        if (!equals) return equals;*/
        equals = (this.documentation == null && other.documentation == null) || 
            (this.documentation != null && this.documentation.equals(other.documentation));
        if (!equals) return equals;
        equals = (this.formValidations == null && other.formValidations == null) || 
            (this.formValidations != null && this.formValidations.equals(other.formValidations));
        if (!equals) return equals;
        equals = (this.formItems == null && other.formItems == null) || 
            (this.formItems != null && this.formItems.equals(other.formItems));
        if (!equals) return equals;
        equals = (this.outputs == null && other.outputs == null) || 
            (this.outputs != null && other.outputs != null && this.outputs.entrySet().equals(other.outputs.entrySet()));
        if (!equals) return equals;
        equals = (this.inputs == null && other.inputs == null) || 
            (this.inputs != null && other.inputs != null && this.inputs.entrySet().equals(other.inputs.entrySet()));
        if (!equals) return equals;
        equals = (this.onLoadScripts == null && other.onLoadScripts == null) ||
            (this.onLoadScripts != null && this.onLoadScripts.equals(other.onLoadScripts));
        if (!equals) return equals;
        equals = (this.onSubmitScripts == null && other.onSubmitScripts == null) ||
            (this.onSubmitScripts != null && this.onSubmitScripts.equals(other.onSubmitScripts));
        if (!equals) return equals;
        equals = (this.saved == other.saved);
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.name == null ? 0 : this.name.hashCode();
        result = 37 * result + aux;
        aux = this.taskId == null ? 0 : this.taskId.hashCode();
        result = 37 * result + aux;
        aux = this.processName == null ? 0 : this.processName.hashCode();
        result = 37 * result + aux;
        aux = this.action == null ? 0 : this.action.hashCode();
        result = 37 * result + aux;
        aux = this.method == null ? 0 : this.method.hashCode();
        result = 37 * result + aux;
        aux = this.enctype == null ? 0 : this.enctype.hashCode();
        result = 37 * result + aux;
        aux = this.enctype == null ? 0 : this.enctype.hashCode();
        result = 37 * result + aux;
        /*aux = (int) (this.lastModified ^ this.lastModified);
        result = 37 * result + aux;*/
        aux = this.documentation == null ? 0 : this.documentation.hashCode();
        result = 37 * result + aux;
        aux = this.formValidations == null ? 0 : this.formValidations.hashCode();
        result = 37 * result + aux;
        aux = this.formItems == null ? 0 : this.formItems.hashCode();
        result = 37 * result + aux;
        aux = this.outputs == null ? 0 : this.outputs.hashCode();
        result = 37 * result + aux;
        aux = this.inputs == null ? 0 : this.inputs.hashCode();
        result = 37 * result + aux;
        aux = this.onLoadScripts == null ? 0 : this.onLoadScripts.hashCode();
        result = 37 * result + aux;
        aux = this.onSubmitScripts == null ? 0 : this.onSubmitScripts.hashCode();
        result = 37 * result + aux;
        aux = this.saved ? 1 : 0;
        result = 37 * result + aux;
        return result;
    }
}
