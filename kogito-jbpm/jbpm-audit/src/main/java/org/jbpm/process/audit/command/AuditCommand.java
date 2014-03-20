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
package org.jbpm.process.audit.command;

import org.drools.core.command.impl.FixedKnowledgeCommandContext;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.process.audit.AuditLogService;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.strategy.PersistenceStrategyType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.Context;

public abstract class AuditCommand<T> implements GenericCommand<T> {

    protected AuditLogService auditLogService;
    
    public AuditCommand() {
	}
    
    protected void setLogEnvironment(Context cntxt) { 
        if( ! (cntxt instanceof KnowledgeCommandContext) ) { 
            throw new UnsupportedOperationException("This command must be executed by a " + KieSession.class.getSimpleName() + " instance!");
        }
        KnowledgeCommandContext realContext = (FixedKnowledgeCommandContext) cntxt;
        this.auditLogService = new JPAAuditLogService(realContext.getKieSession().getEnvironment(), PersistenceStrategyType.KIE_SESSION);
    }
    
}
