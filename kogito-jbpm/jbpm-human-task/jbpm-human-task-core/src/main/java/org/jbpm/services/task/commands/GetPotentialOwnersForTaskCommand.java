package org.jbpm.services.task.commands;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-pot-owners-for-task-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetPotentialOwnersForTaskCommand extends TaskCommand<Map<Long, List<OrganizationalEntity>>> {

	private static final long serialVersionUID = 6296898155907765061L;

	@XmlElement
	private List<Long> taskIds;
	
	public GetPotentialOwnersForTaskCommand() {
	}
	
	public GetPotentialOwnersForTaskCommand(List<Long> taskIds) {
		this.taskIds = taskIds;
    }
	
    public List<Long> getTaskIds() {
		return taskIds;
	}

	public void setTaskIds(List<Long> taskIds) {
		this.taskIds = taskIds;
	}

	public Map<Long, List<OrganizationalEntity>> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;

        return context.getTaskQueryService().getPotentialOwnersForTaskIds(taskIds);
    }

}
