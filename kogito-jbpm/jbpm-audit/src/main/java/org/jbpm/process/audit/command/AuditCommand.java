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
package org.jbpm.process.audit.command;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.drools.core.command.impl.RegistryContext;
import org.jbpm.process.audit.AuditLogService;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.strategy.PersistenceStrategyType;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class AuditCommand<T> implements ExecutableCommand<T> {

    @XmlTransient
    protected AuditLogService auditLogService = null;
    
    public AuditCommand() {
	}
   
    public void setAuditLogService(AuditLogService auditLogService) { 
        this.auditLogService = auditLogService;
    }
    
    protected void setLogEnvironment(Context cntxt) { 
        if( auditLogService != null ) { 
            return;
        }
        if( ! (cntxt instanceof RegistryContext ) ) {
            throw new UnsupportedOperationException("This command must be executed by a " + KieSession.class.getSimpleName() + " instance!");
        }
        this.auditLogService = new JPAAuditLogService( ((RegistryContext) cntxt).lookup( KieSession.class ).getEnvironment(), PersistenceStrategyType.KIE_SESSION);
    }
    
}
