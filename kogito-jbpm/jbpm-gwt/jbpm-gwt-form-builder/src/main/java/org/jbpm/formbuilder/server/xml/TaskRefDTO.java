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
package org.jbpm.formbuilder.server.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.jbpm.formbuilder.shared.task.TaskPropertyRef;
import org.jbpm.formbuilder.shared.task.TaskRef;

public class TaskRefDTO {

    private String _processId;
    private String _taskName;
    private String _taskId;
    private List<PropertyDTO> _input = new ArrayList<PropertyDTO>();
    private List<PropertyDTO> _output = new ArrayList<PropertyDTO>();
    private List<MetaData2DTO> _metaData = new ArrayList<MetaData2DTO>();
    
    public TaskRefDTO() {
        // jaxb needs a default constructor
    }
    
    public TaskRefDTO(TaskRef task) {
        this._processId = task.getProcessId();
        this._taskName = task.getTaskName();
        this._taskId = task.getTaskId();
        List<TaskPropertyRef> allInputs = task.getInputs();
        List<TaskPropertyRef> uniqueInputs = new ArrayList<TaskPropertyRef>();
        List<String> inputNames = new ArrayList<String>();
        for (TaskPropertyRef ref : allInputs) {
            if (!inputNames.contains(ref.getName())) {
                inputNames.add(ref.getName());
                uniqueInputs.add(ref);
            }
        }
        for (TaskPropertyRef ref : uniqueInputs) {
            _input.add(new PropertyDTO(ref));
        }
        
        List<TaskPropertyRef> allOutputs = task.getOutputs();
        List<TaskPropertyRef> uniqueOutputs = new ArrayList<TaskPropertyRef>();
        List<String> outputNames = new ArrayList<String>();
        for (TaskPropertyRef ref : allOutputs) {
            if (!outputNames.contains(ref.getName())) {
                outputNames.add(ref.getName());
                uniqueOutputs.add(ref);
            }
        }
        for (TaskPropertyRef ref : uniqueOutputs) {
            _output.add(new PropertyDTO(ref));
        }
        for (Map.Entry<String, String> entry : task.getMetaData().entrySet()) {
            _metaData.add(new MetaData2DTO(entry));
        }
    }

    @XmlAttribute 
    public String getProcessId() {
        return _processId;
    }

    public void setProcessId(String processId) {
        this._processId = processId;
    }
    
    @XmlAttribute 
    public String getTaskName() {
        return _taskName;
    }

    public void setTaskName(String taskName) {
        this._taskName = taskName;
    }

    @XmlAttribute 
    public String getTaskId() {
        return _taskId;
    }

    public void setTaskId(String taskId) {
        this._taskId = taskId;
    }

    @XmlElement 
    public List<PropertyDTO> getInput() {
        return _input;
    }

    public void setInput(List<PropertyDTO> input) {
        this._input = input;
    }

    @XmlElement 
    public List<PropertyDTO> getOutput() {
        return _output;
    }

    public void setOutput(List<PropertyDTO> output) {
        this._output = output;
    }

    @XmlElement 
    public List<MetaData2DTO> getMetaData() {
        return _metaData;
    }

    public void setMetaData(List<MetaData2DTO> metaData) {
        this._metaData = metaData;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_input == null) ? 0 : _input.hashCode());
        result = prime * result
                + ((_metaData == null) ? 0 : _metaData.hashCode());
        result = prime * result + ((_output == null) ? 0 : _output.hashCode());
        result = prime * result
                + ((_processId == null) ? 0 : _processId.hashCode());
        result = prime * result + ((_taskId == null) ? 0 : _taskId.hashCode());
        result = prime * result
                + ((_taskName == null) ? 0 : _taskName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskRefDTO other = (TaskRefDTO) obj;
        if (_input == null) {
            if (other._input != null)
                return false;
        } else if (!_input.equals(other._input))
            return false;
        if (_metaData == null) {
            if (other._metaData != null)
                return false;
        } else if (!_metaData.equals(other._metaData))
            return false;
        if (_output == null) {
            if (other._output != null)
                return false;
        } else if (!_output.equals(other._output))
            return false;
        if (_processId == null) {
            if (other._processId != null)
                return false;
        } else if (!_processId.equals(other._processId))
            return false;
        if (_taskId == null) {
            if (other._taskId != null)
                return false;
        } else if (!_taskId.equals(other._taskId))
            return false;
        if (_taskName == null) {
            if (other._taskName != null)
                return false;
        } else if (!_taskName.equals(other._taskName))
            return false;
        return true;
    }
    
    
}
