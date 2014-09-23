package org.jbpm.process.audit.command;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.internal.command.Context;
import org.kie.internal.query.data.QueryData;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AuditNodeInstanceLogQueryCommand extends AuditCommand<List<NodeInstanceLog>>{

    /** generated serial version UID */
    private static final long serialVersionUID = -5408224599858065532L;
    
    @XmlElement
    private QueryData queryData;
   
    public AuditNodeInstanceLogQueryCommand() {
        // JAXB constructor
    }
    
    public AuditNodeInstanceLogQueryCommand(QueryData queryData) {
       this.queryData = queryData; 
    }
    
    @Override
    public List<NodeInstanceLog> execute( Context context ) {
        setLogEnvironment(context);
        return auditLogService.queryNodeInstanceLogs(queryData);
    }

}
