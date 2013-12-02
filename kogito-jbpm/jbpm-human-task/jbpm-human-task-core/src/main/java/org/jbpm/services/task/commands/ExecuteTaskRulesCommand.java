package org.jbpm.services.task.commands;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.xml.jaxb.util.JaxbMapAdapter;
import org.kie.api.task.model.Task;
import org.kie.internal.command.Context;

@XmlRootElement(name="execute-task-rules-command")
@XmlAccessorType(XmlAccessType.NONE)
public class ExecuteTaskRulesCommand extends TaskCommand<Void> {
	
	private static final long serialVersionUID = 1852525453931482868L;
	
	@XmlElement
	@XmlJavaTypeAdapter(JaxbMapAdapter.class)
	protected Map<String, Object> data;
	@XmlElement
	@XmlSchemaType(name="string")
    protected String scope;
    
    public ExecuteTaskRulesCommand() {
    }

    public ExecuteTaskRulesCommand(long taskId, String userId, Map<String, Object> data, String scope) {
        this.taskId = taskId;
        this.userId = userId;
        this.data = data;
        this.scope = scope;
    }

    public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	@Override
	public Void execute(Context ctx) {
		
		TaskContext context = (TaskContext) ctx;
		Task task = context.getTaskQueryService().getTaskInstanceById(taskId);
		
		context.getTaskRuleService().executeRules(task, userId, data, scope);
		return null;
	}

}
