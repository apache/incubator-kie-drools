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

import java.util.Map;

import org.jbpm.services.task.impl.TaskContentRegistry;
import org.jbpm.services.task.rule.RuleContextProvider;
import org.jbpm.services.task.rule.TaskRuleService;
import org.jbpm.services.task.rule.TaskServiceRequest;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.exception.TaskException;

public class TaskRuleServiceImpl implements TaskRuleService {
    
    private RuleContextProvider ruleContextProvider;
    
    public TaskRuleServiceImpl() {
    	
    }
    
    public TaskRuleServiceImpl(RuleContextProvider ruleContextProvider) {
    	this.ruleContextProvider = ruleContextProvider;
    }   
    
    @Override
    public void executeRules(Task task, String userId, Object params, String scope) throws TaskException {
        KieBase ruleBase = ruleContextProvider.getKieBase(scope);
        if (ruleBase != null) {
            KieSession session = ruleBase.newKieSession();
            Map<String, Object> globals = ruleContextProvider.getGlobals(scope);
            if (globals != null) {
                for (Map.Entry<String, Object> entry : globals.entrySet()) {
                    session.setGlobal(entry.getKey(), entry.getValue());
                }
            }
            User user = TaskModelProvider.getFactory().newUser();
            ((InternalOrganizationalEntity) user).setId(userId);
            TaskServiceRequest request = new TaskServiceRequest(scope, user, null);
            session.setGlobal("request", request);
            session.insert(task);
            if (params != null) {
            	if (params instanceof ContentData) {
            		ContentMarshallerContext ctx = TaskContentRegistry.get().getMarshallerContext(task);
            		params = ContentMarshallerHelper.unmarshall(((ContentData) params).getContent(), ctx.getEnvironment(), ctx.getClassloader());
            	}
            	
                session.insert(params);
            }
            session.fireAllRules();
            session.dispose();

            if (!request.isAllowed()) {
                StringBuilder error = new StringBuilder("Cannot perform operation " + scope + " :\n");
                if (request.getReasons() != null) {
                    for (String reason : request.getReasons()) {
                        error.append( reason).append('\n');
                    }
                }

                throw request.getException(error.toString());
            }
        }
    }

    public RuleContextProvider getRuleContextProvider() {
        return ruleContextProvider;
    }

    public void setRuleContextProvider(RuleContextProvider ruleContextProvider) {
        this.ruleContextProvider = ruleContextProvider;
    }

}
