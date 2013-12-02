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
package org.jbpm.kie.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jbpm.kie.services.api.KnowledgeAdminDataService;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.UpdateStringCommand;


@ApplicationScoped
public class KnowledgeAdminDataServiceImpl implements KnowledgeAdminDataService{
    @Inject 
    private TransactionalCommandService commandService;

    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }
    
    public int removeAllData() {
        int deleted = 0;
        deleted += commandService.execute(new UpdateStringCommand("delete from  NodeInstanceLog nid"));
        deleted += commandService.execute(new UpdateStringCommand("delete from  ProcessInstanceLog pid"));        
        deleted += commandService.execute(new UpdateStringCommand("delete from  VariableInstanceLog vsd"));
        return deleted;
    }
    
}
