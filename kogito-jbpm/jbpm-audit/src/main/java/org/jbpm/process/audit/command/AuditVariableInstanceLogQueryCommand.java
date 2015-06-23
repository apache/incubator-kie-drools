/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.internal.command.Context;
import org.kie.internal.query.data.QueryData;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AuditVariableInstanceLogQueryCommand extends AuditCommand<List<VariableInstanceLog>>{

    /** generated serial version UID */ 
    private static final long serialVersionUID = 403371489934741666L;
    
    @XmlElement
    private QueryData queryData;
   
    public AuditVariableInstanceLogQueryCommand() {
        // JAXB constructor
    }
    
    public AuditVariableInstanceLogQueryCommand(QueryData queryData) {
       this.queryData = queryData; 
    }
    
    @Override
    public List<VariableInstanceLog> execute( Context context ) {
        setLogEnvironment(context);
        return auditLogService.queryVariableInstanceLogs(queryData);
    }

}
