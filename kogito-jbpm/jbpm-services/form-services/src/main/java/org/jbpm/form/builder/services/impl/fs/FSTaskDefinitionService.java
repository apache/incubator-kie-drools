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
package org.jbpm.form.builder.services.impl.fs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.BPMN2ProcessProvider;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.definition.process.Process;
import org.drools.io.impl.ByteArrayResource;
import org.jbpm.form.builder.services.api.TaskServiceException;
import org.jbpm.form.builder.services.impl.base.TaskRepoHelper;
import org.jbpm.form.builder.services.tasks.TaskDefinitionService;
import org.jbpm.form.builder.services.tasks.TaskDefinitionsSemanticModule;
import org.jbpm.form.builder.services.tasks.TaskRef;


/**
 *
 * @author salaboy
 */
public class FSTaskDefinitionService implements TaskDefinitionService {

    private final TaskRepoHelper repo = new TaskRepoHelper();
    private final TaskDefinitionsSemanticModule module = new TaskDefinitionsSemanticModule(repo);
    private final BPMN2ProcessProvider provider = new BPMN2ProcessProvider() {

        @Override
        public void configurePackageBuilder(PackageBuilder packageBuilder) {
            PackageBuilderConfiguration conf = packageBuilder.getPackageBuilderConfiguration();
            if (conf.getSemanticModules().getSemanticModule(TaskDefinitionsSemanticModule.URI) == null) {
                conf.addSemanticModule(module);
            }
        }
    };
    private String baseUrl;
    private String fileSeparator = System.getProperty("file.separator");
    
    public List<TaskRef> query(String pkgName, String filter) throws TaskServiceException {
        List<TaskRef> tasks = new ArrayList<TaskRef>();
        Collection<File> listFiles = FileUtils.listFiles(new File(baseUrl+fileSeparator+pkgName), new String[]{"bpmn2", "rf"}, true);
        for (File key : listFiles) {
            String assetId = key.getName();
            if (assetId.endsWith(ResourceType.BPMN2.getDefaultExtension())
                    || assetId.endsWith(ResourceType.DRF.getDefaultExtension())
                    || assetId.endsWith("bpmn2")) {
                try {
                    String content = getTaskDefinitionContent(pkgName, assetId);
                    if (content != null && !"".equals(content)) {
                        List<TaskRef> processTasks = getProcessTasks(content, assetId);
                        if (processTasks != null) {
                            for (TaskRef ref : processTasks) {
                                if (filter == null || "".equals(filter)) {
                                    tasks.add(ref);
                                } else if (ref.getProcessId().contains(assetId) || ref.getTaskName().contains(filter)) {
                                    tasks.add(ref);
                                }
                            }
                        }
                    }
                } catch (IOException ex) {
                    throw new TaskServiceException(ex.getMessage(), ex);
                }
            }
        }
        return tasks;
    }

    private String getTaskDefinitionContent(String pkgName, String itemName) throws IOException {

        if (itemName != null && !"".equals(itemName)) {

            String getUrl = baseUrl + fileSeparator + pkgName + fileSeparator
                    + itemName;
            return FileUtils.readFileToString(new File(getUrl));

        }
        return "";
    }

    public List<TaskRef> getTasksByName(String pkgName, String processName, String taskName) throws TaskServiceException {
        List<TaskRef> retval = new ArrayList<TaskRef>();
        try {
            File processFile = getProcessById(pkgName, processName);
            String processContent = FileUtils.readFileToString(processFile);
            List<TaskRef> tasks = getProcessTasks(processContent, "any.bpmn2");
            for (TaskRef task : tasks) {
                
                    if (task.getTaskId() != null && task.getTaskId().equals(taskName)) {
                        retval.add(task);
                    }
               
            }
        } catch (IOException ex) {
            throw new TaskServiceException(ex.getMessage(), ex);
        }
        return retval;


    }
    
    public File getProcessById(String pkgName, String processName) throws TaskServiceException{
        
        Collection<File> listFiles = FileUtils.listFiles(new File(baseUrl+fileSeparator+pkgName), new String[]{"bpmn2", "rf"}, true);
        
        for (File file : listFiles) {
            try {
                String processContent = FileUtils.readFileToString(file);
                KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
                kbuilder.add(new ByteArrayResource(processContent.getBytes()), ResourceType.BPMN2);
                if (!kbuilder.hasErrors()) {
                    KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
                    kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
                    Collection<Process> processes = kbase.getProcesses();
                    
                    for(Process process : processes){
                        if(process.getId().equals(processName)){
                            return file;
                        }
                    }
                }else{
                    throw new IllegalStateException(" Knowledge Could Not be Parsed "+kbuilder.getErrors());
                    
                    
                }
            } catch (Exception ex) {
                throw new TaskServiceException(ex.getMessage(), ex);
            }
        }    
        
        return null;
        
    }

    public TaskRef getTaskByUUID(String pkgName, String userTask, String uuid) throws TaskServiceException {
        throw new UnsupportedOperationException("Not supported for FS implementation.");
    }

    public String getContainingPackage(String uuid) throws TaskServiceException {
        throw new UnsupportedOperationException("Not supported yet for FS implementation.");
    }

    public TaskRef getBPMN2Task(String bpmn2ProcessContent, String processName, String userTask) throws TaskServiceException {
        TaskRef retval = null;
        List<TaskRef> tasks = getProcessTasks(bpmn2ProcessContent, processName);
        if (tasks != null) {
            for (TaskRef task : tasks) {
                if (task.getTaskName().equals(userTask)) {
                    retval = task;
                    break;
                }
            }
        }
        return retval;
    }

    public void afterPropertiesSet() throws Exception {
        // do nothing
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<TaskRef> getProcessTasks(String bpmn2Content, String processName) {
        if (bpmn2Content == null || "".equals(bpmn2Content)) {
            return new ArrayList<TaskRef>();
        }
        if (BPMN2ProcessFactory.getBPMN2ProcessProvider() != provider) {
            BPMN2ProcessFactory.setBPMN2ProcessProvider(provider);
        }
        repo.clear();
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        boolean isBPMN = processName.toLowerCase().endsWith("bpmn");
        boolean isBPMN2 = processName.toLowerCase().endsWith("bpmn2");
        ResourceType type = (isBPMN || isBPMN2) ? ResourceType.BPMN2 : ResourceType.DRF;
        kbuilder.add(new ByteArrayResource(bpmn2Content.getBytes()), type);
        if (!kbuilder.hasErrors()) {
            return new ArrayList<TaskRef>(repo.getTasks());
        } else {
            return new ArrayList<TaskRef>();
        }
    }
}
