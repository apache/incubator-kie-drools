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
package org.droolsjbpm.services.impl.bpmn2;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.BPMN2ProcessProvider;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.io.impl.ByteArrayResource;
import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.model.ProcessDesc;
import org.jbpm.task.TaskDef;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.definition.KnowledgePackage;
import org.kie.io.ResourceType;

/**
 *
 * @author salaboy
 */
@ApplicationScoped
public class BPMN2DataServiceImpl implements BPMN2DataService {

    @Inject
    private TaskServiceEntryPoint taskService;
    @Inject
    private KnowledgeDomainService knolwedgeService;
    @Inject
    private BPMN2DataServiceSemanticModule module;
    private BPMN2ProcessProvider provider;
    @Inject
    private ProcessDescriptionRepository repo;
    
    public BPMN2DataServiceImpl() {
    }

    @PostConstruct
    public void init() {
        provider = new BPMN2ProcessProvider() {
            @Override
            public void configurePackageBuilder(PackageBuilder packageBuilder) {
                PackageBuilderConfiguration conf = packageBuilder.getPackageBuilderConfiguration();
                if (conf.getSemanticModules().getSemanticModule("http://www.jboss.org/bpmn2-data-services") == null) {
                    conf.addSemanticModule(module);
                }
            }
        };
    }

    public Map<String, String> getTaskInputMappings(String processId, String taskName){
        if (processId == null || "".equals(processId)) {
            throw new IllegalStateException("The Process id cannot be Empty!");
        }
        ProcessDescRepoHelper helper = repo.getProcessDesc(processId);
        if (helper == null) {
            throw new IllegalStateException("No process available with given id : " + processId);
        }
        return helper.getTaskInputMappings().get(taskName);
    }
    
     public Map<String, String> getTaskOutputMappings(String processId, String taskName){
        if (processId == null || "".equals(processId)) {
            throw new IllegalStateException("The Process id cannot be Empty!");
        }
        
        ProcessDescRepoHelper helper = repo.getProcessDesc(processId);
        if (helper == null) {
            throw new IllegalStateException("No process available with given id : " + processId);
        }
        return helper.getTaskOutputMappings().get(taskName);
    }


    public Collection<TaskDef> getAllTasksDef(String processId){
        if (processId == null || "".equals(processId)) {
            throw new IllegalStateException("The Process id cannot be Empty!");
        }
    
        ProcessDescRepoHelper helper = repo.getProcessDesc(processId);
        if (helper == null) {
            throw new IllegalStateException("No process available with given id : " + processId);
        }
        return helper.getTasks().values();
    }

    public Map<String, String> getAssociatedEntities(String processId) {
        if (processId == null || "".equals(processId)) {
            throw new IllegalStateException("The Process id cannot be Empty!");
        }
        
        ProcessDescRepoHelper helper = repo.getProcessDesc(processId);
        if (helper == null) {
            throw new IllegalStateException("No process available with given id : " + processId);
        }
        return helper.getTaskAssignments();
    }

    public List<String> getAssociatedDomainObjects(String bpmn2Content) {
         throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Map<String, String> getProcessData(String processId) {
        if (processId == null || "".equals(processId)) {
            throw new IllegalStateException("The Process id cannot be Empty!");
        }
        ProcessDescRepoHelper helper = repo.getProcessDesc(processId);
        if (helper == null) {
            throw new IllegalStateException("No process available with given id : " + processId);
        }
        return helper.getInputs();
    }

    public List<String> getAssociatedForms(String processId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ProcessDesc getProcessDesc(String processId){
        if (processId == null || "".equals(processId)) {
            throw new IllegalStateException("The Process id cannot be Empty!");
        }
        
        ProcessDescRepoHelper helper = repo.getProcessDesc(processId);
        if (helper == null) {
            throw new IllegalStateException("No process available with given id : " + processId);
        }
        return helper.getProcess();
    }

    @Override
    public Collection<String> getReusableSubProcesses(String processId) {
        if (processId == null || "".equals(processId)) {
            throw new IllegalStateException("The Process id cannot be Empty!");
        }
        ProcessDescRepoHelper helper = repo.getProcessDesc(processId);
        if (helper == null) {
            throw new IllegalStateException("No process available with given id : " + processId);
        }
        return helper.getReusableSubProcesses();
    }

    @Override
    public String findProcessId(final String bpmn2Content) {
        if (bpmn2Content == null || "".equals(bpmn2Content)) {
            return "";
        }
        BPMN2ProcessProvider originalProvider = BPMN2ProcessFactory.getBPMN2ProcessProvider();
        if (originalProvider != provider) {
            BPMN2ProcessFactory.setBPMN2ProcessProvider(provider);
        }

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
       
        kbuilder.add(new ByteArrayResource(bpmn2Content.getBytes()), ResourceType.BPMN2);
        if (kbuilder.hasErrors()) {
            for(KnowledgeBuilderError error: kbuilder.getErrors()){
                System.out.println("Error: "+error.getMessage());
            }
            throw new IllegalStateException("Process Cannot be Parsed! \n"+bpmn2Content);
        }
        
        BPMN2ProcessFactory.setBPMN2ProcessProvider(originalProvider);
        
        KnowledgePackage pckg = kbuilder.getKnowledgePackages().iterator().next();
        return pckg.getProcesses().iterator().next().getId();
    }
}
