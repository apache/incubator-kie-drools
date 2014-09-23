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
