/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task.rule.impl;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.services.task.rule.RuleContextProvider;
import org.jbpm.services.task.rule.TaskRuleService;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;


public class RuleContextProviderImpl implements RuleContextProvider {
	
	private static RuleContextProviderImpl INSTANCE = new RuleContextProviderImpl();
	
	private RuleContextProviderImpl() {		
		initialize();
	}
	
	public static RuleContextProviderImpl get() {
		return INSTANCE;
	}
    
    private static final String DEFAULT_ADD_TASK_RULES = "default-add-task.drl";
    private static final String DEFAULT_COMPLETE_TASK_RULES = "default-complete-task.drl";

    private Map<String, KieBase> kieBases = new HashMap<String, KieBase>();
    private Map<String, Map<String, Object>> globals = new HashMap<String, Map<String,Object>>();
    
    public void initialize() {
        try {
            Resource addTask = ResourceFactory.newClassPathResource(DEFAULT_ADD_TASK_RULES);
        
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(addTask, ResourceType.DRL);
            
            kieBases.put(TaskRuleService.ADD_TASK_SCOPE, kbuilder.newKieBase());
        } catch (Exception e) {
            
        }
        try {
            Resource completeTask = ResourceFactory.newClassPathResource(DEFAULT_COMPLETE_TASK_RULES);
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(completeTask, ResourceType.DRL);
            
            kieBases.put(TaskRuleService.COMPLETE_TASK_SCOPE, kbuilder.newKieBase());
        } catch (Exception e) {
            
        }
    }
    
    @Override
    public KieBase getKieBase(String scope) {
        return kieBases.get(scope);
    }

    @Override
    public Map<String, Object> getGlobals(String scope) {
        return globals.get(scope);
    }
    
    public void addGlobals(String scope, Map<String, Object> global) {
        this.globals.put(scope, global);
    }

    @Override
    public void addKieBase(String scope, KieBase kbase) {
        this.kieBases.put(scope, kbase);
        
    }
}
