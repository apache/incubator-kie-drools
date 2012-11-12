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
package org.jbpm.form.builder.services.tasks;

import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.form.builder.services.impl.base.TaskRepoHelper;

public class TaskDefinitionsSemanticModule extends BPMNSemanticModule {

    public static final String URI = "http://www.jboss.org/jbpm-form-builder";

    public TaskDefinitionsSemanticModule(TaskRepoHelper repo) {
        super();
        addHandler("userTask", new HumanTaskGetInformationHandler(repo));
        addHandler("process", new ProcessGetInformationHandler(repo));
        addHandler("property", new ProcessGetInputHandler(repo));
    }
}
