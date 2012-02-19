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
package org.jbpm.formbuilder.shared.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskRef {

    private String packageName;
    private String processId;
    private String processName;
    private String taskId;
    private List<TaskPropertyRef> inputs = new ArrayList<TaskPropertyRef>();
    private List<TaskPropertyRef> outputs = new ArrayList<TaskPropertyRef>();
    private Map<String, String> metaData = new HashMap<String, String>();
    
    public List<TaskPropertyRef> getInputs() {
        return inputs;
    }
    
    public void setInputs(List<TaskPropertyRef> inputs) {
        this.inputs = inputs;
    }
    
    public List<TaskPropertyRef> getOutputs() {
        return outputs;
    }
    
    public void setOutputs(List<TaskPropertyRef> outputs) {
        this.outputs = outputs;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public boolean addInput(String key, String value) {
        if (getInput(key) != null) {
            return false;
        }
        TaskPropertyRef tpRef = new TaskPropertyRef();
        tpRef.setName(key);
        tpRef.setSourceExpresion(value);
        return this.inputs.add(tpRef);
    }
    
    public TaskPropertyRef getInput(String key) {
        for (TaskPropertyRef ref : inputs) {
            if (key != null && key.equals(ref.getName())) {
                return ref;
            }
        }
        return null;
    }
    
    public Object removeInput(String key) {
        return this.inputs.remove(getInput(key));
    }
    
    public boolean addOutput(String key, String value) {
        if (getOutput(key) != null) {
            return false;
        }
        TaskPropertyRef tpRef = new TaskPropertyRef();
        tpRef.setName(key);
        tpRef.setSourceExpresion(value);
            return this.outputs.add(tpRef);
    }
    
    public TaskPropertyRef getOutput(String key) {
        for (TaskPropertyRef ref : outputs) {
            if (key != null && key.equals(ref.getName())) {
                return ref;
            }
        }
        return null;
    }
    
    public Object removeOutput(String key) {
        return this.outputs.remove(getOutput(key));
    }

    public String getTaskName() {
        return this.taskId;
    }
    
    public void setProcessId(String processId) {
        this.processId = processId;
    }
    
    public String getProcessId() {
        return processId;
    }
    
    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }
    
    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public String getPackageName() {
        return packageName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
    
    public String getProcessName() {
        return processName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof TaskRef)) return false;
        TaskRef other = (TaskRef) obj;
        boolean equals = (this.packageName == null && other.packageName == null) || 
            (this.packageName != null && this.packageName.equals(other.packageName));
        if (!equals) return equals;
        equals = (this.processId == null && other.processId == null) || 
            (this.processId != null && this.processId.equals(other.processId));
        if (!equals) return equals;
        equals = (this.processName == null && other.processName == null) || 
            (this.processName != null && this.processName.equals(other.processName));
        if (!equals) return equals;
        equals = (this.taskId == null && other.taskId == null) || 
            (this.taskId != null && this.taskId.equals(other.taskId));
        if (!equals) return equals;
        equals = (this.inputs == null && other.inputs == null) || 
            (this.inputs != null && this.inputs.equals(other.inputs));
        if (!equals) return equals;
        equals = (this.outputs == null && other.outputs == null) || 
            (this.outputs != null && this.outputs.equals(other.outputs));
        if (!equals) return equals;
        equals = (this.metaData == null && other.metaData == null) 
            || (this.metaData != null && this.metaData.entrySet().equals(other.metaData.entrySet()));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.packageName == null ? 0 : this.packageName.hashCode();
        result = 37 * result + aux;
        aux = this.processId == null ? 0 : this.processId.hashCode();
        result = 37 * result + aux;
        aux = this.processName == null ? 0 : this.processName.hashCode();
        result = 37 * result + aux;
        aux = this.taskId == null ? 0 : this.taskId.hashCode();
        result = 37 * result + aux;
        aux = this.inputs == null ? 0 : this.inputs.hashCode();
        result = 37 * result + aux;
        aux = this.outputs == null ? 0 : this.outputs.hashCode();
        result = 37 * result + aux;
        aux = this.metaData == null ? 0 : this.metaData.hashCode();
        result = 37 * result + aux;
        return result;
    }
    
    @Override
    public String toString() {
        return new StringBuilder("TaskRef[package=").append(this.packageName).
            append(";processId=").append(this.processId).
            append(";processName=").append(this.processName).
            append(";taskId=").append(this.taskId).
            append(";inputs=").append(this.inputs).
            append(";outputs=").append(this.outputs).
            append(";metaData=").append(this.metaData).
        append("]").toString();
    }
}
