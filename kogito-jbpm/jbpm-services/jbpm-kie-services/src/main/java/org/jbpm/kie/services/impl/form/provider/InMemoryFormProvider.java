/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.kie.services.impl.form.provider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.model.InternalTask;


public class InMemoryFormProvider extends FreemakerFormProvider {

    private static final String DEFAULT_PROCESS = "DefaultProcess";
    private static final String DEFAULT_TASK = "DefaultTask";

    @Override
    public String render(String name, ProcessDefinition process, Map<String, Object> renderContext) {
    	ProcessAssetDesc asset = null;
    	if (!(process instanceof ProcessAssetDesc)) {
    		return null;
    	}     	
    	asset = (ProcessAssetDesc) process;
    	
        InputStream template = null;
        if (asset.getForms().containsKey(process.getId())) {
            template = new ByteArrayInputStream(asset.getForms().get(process.getId()).getBytes());
        } else if (asset.getForms().containsKey(process.getId() + "-taskform")) {
            template = new ByteArrayInputStream(asset.getForms().get(process.getId() + "-taskform").getBytes());
        } else if (asset.getForms().containsKey(DEFAULT_PROCESS)) {
            template = new ByteArrayInputStream(asset.getForms().get(DEFAULT_PROCESS).getBytes());
        }

        if (template == null) return null;

        return render(name, template, renderContext);
    }

    @Override
    public String render(String name, Task task, ProcessDefinition process, Map<String, Object> renderContext) {
    	ProcessAssetDesc asset = null;
    	if (!(process instanceof ProcessAssetDesc)) {
    		return null;
    	}     	
    	asset = (ProcessAssetDesc) process;
    	
        InputStream template = null;
        if(task != null && process != null){
            String lookupName = "";
            String formName = ((InternalTask)task).getFormName();
            if(formName != null && !formName.equals("")){
                lookupName = formName;
            }else{
                lookupName = task.getNames().get(0).getText();
                
            }
            if (asset.getForms().containsKey(lookupName)) {
                template = new ByteArrayInputStream(asset.getForms().get(lookupName).getBytes());
            } else if (asset.getForms().containsKey(lookupName.replace(" ", "")+ "-taskform")) {
                template = new ByteArrayInputStream(asset.getForms().get(lookupName.replace(" ", "") + "-taskform").getBytes());
            } else if (asset.getForms().containsKey(DEFAULT_TASK)) {
                template = new ByteArrayInputStream(asset.getForms().get(DEFAULT_TASK).getBytes());
            }
        }

        if (template == null) return null;

        return render(name, template, renderContext);
    }

    @Override
    public int getPriority() {
        return 3;
    }

}