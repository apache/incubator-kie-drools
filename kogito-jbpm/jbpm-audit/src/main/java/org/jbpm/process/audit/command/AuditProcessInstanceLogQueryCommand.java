package org.jbpm.process.audit.command;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.internal.command.Context;
import org.kie.internal.query.data.QueryData;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AuditProcessInstanceLogQueryCommand extends AuditCommand<List<ProcessInstanceLog>>{

    /** generated serial version UID */
    private static final long serialVersionUID = 7543632015198138915L;
    
    @XmlElement
    private QueryData queryData;
   
    public AuditProcessInstanceLogQueryCommand() {
        // JAXB constructor
    }
    
    public AuditProcessInstanceLogQueryCommand(QueryData queryData) {
       this.queryData = queryData; 
    }
    
    @Override
    public List<ProcessInstanceLog> execute( Context context ) {
        setLogEnvironment(context);
        return auditLogService.queryProcessInstanceLogs(queryData);
    }

}
