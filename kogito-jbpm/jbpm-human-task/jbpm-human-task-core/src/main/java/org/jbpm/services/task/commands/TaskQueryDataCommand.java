package org.jbpm.services.task.commands;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;
import org.kie.internal.query.data.QueryData;
import org.kie.internal.query.data.QueryDataCommand;

@XmlRootElement(name="task-query-data-command")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaskQueryDataCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> implements QueryDataCommand<List<TaskSummary>> {

    /** generated serial version UID */
    private static final long serialVersionUID = -6879337395030142688L;

    @XmlElement
    private QueryData queryData;
    
    public TaskQueryDataCommand() { 
        // JAXB constructor
    }
    
    public TaskQueryDataCommand(QueryData data) { 
        this.queryData = data;
    }
    
    @Override
    public QueryData getQueryData() {
        return queryData;
    }

    @Override
    public void setQueryData( QueryData queryData ) {
        this.queryData = queryData;
    }

    @Override
    public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        return context.getTaskQueryService().query(userId, queryData);
    }
   
}
