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
package org.jbpm.formbuilder.server.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formbuilder.shared.task.TaskPropertyRef;
import org.jbpm.formbuilder.shared.task.TaskRef;

public class TaskRepoHelper {

    Map<String, TaskRef> tasksMap = new HashMap<String, TaskRef>();
    
    List<TaskRef> tasks = new ArrayList<TaskRef>();
    String procId = null;
    String procName = null;
    String pkgName = null;
    
    public void clear() {
        tasks.clear();
        tasksMap.clear();
        procId = null;
        procName = null;
        pkgName = null;
    }
    
    public void addTask(TaskRef task) {
        TaskRef oldTask = tasksMap.get(task.getTaskName());
        if (oldTask != null) {
            tasks.remove(oldTask);
            for (TaskPropertyRef input : task.getInputs()) {
                oldTask.addInput(input.getName(), input.getSourceExpresion());
            }
            for (TaskPropertyRef output : task.getOutputs()) {
                oldTask.addOutput(output.getName(), output.getSourceExpresion());
            }
            Map<String, String> metaData = oldTask.getMetaData();
            metaData.putAll(task.getMetaData());
            oldTask.setMetaData(metaData);
            task = oldTask;
        }
        task.setProcessId(this.procId);
        task.setPackageName(this.pkgName);
        tasks.add(task);
        tasksMap.put(task.getTaskName(), task);
    }
    
    public List<TaskRef> getTasks() {
        return tasks;
    }

    public void addOutput(String processInputName, String id) {
        for (TaskRef task : tasks) {
            if (task.getTaskName().equals(processInputName)) {
                TaskPropertyRef prop = new TaskPropertyRef();
                prop.setName(id);
                prop.setSourceExpresion("${" + id + "}");
                if (!task.getOutputs().contains(prop)) {
                    task.addOutput(id, "${" + id + "}");
                }
                return;
            }
        }
        TaskRef ref = new TaskRef();
        ref.setTaskId(processInputName);
        ref.setPackageName(this.pkgName);
        ref.setProcessId(this.procId);
        ref.addOutput(id, "${" + id + "}");
        tasks.add(ref);
        tasksMap.put(ref.getTaskName(), ref);
    }

    public void setDefaultProcessId(String processId) {
        this.procId = processId;
        for (TaskRef task : tasks) {
            task.setProcessId(this.procId);
        }
    }

    public void setDefaultProcessName(String processName) {
        this.procName = processName;
        for (TaskRef task : tasks) {
            task.setProcessName(this.procName);
        }
    }

    public void setDefaultPackageName(String packageName) {
        this.pkgName = packageName;
        for (TaskRef task : tasks) {
            task.setPackageName(this.pkgName);
        }
    }
}
