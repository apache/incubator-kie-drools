package org.jbpm.services.task.commands;

import static org.kie.internal.task.api.TaskQueryService.ACTUAL_OWNER_ID_LIST;
import static org.kie.internal.task.api.TaskQueryService.BUSINESS_ADMIN_ID_LIST;
import static org.kie.internal.task.api.TaskQueryService.LANGUAGE;
import static org.kie.internal.task.api.TaskQueryService.POTENTIAL_OWNER_ID_LIST;
import static org.kie.internal.task.api.TaskQueryService.PROCESS_INST_ID_LIST;
import static org.kie.internal.task.api.TaskQueryService.STATUS_LIST;
import static org.kie.internal.task.api.TaskQueryService.TASK_ID_LIST;
import static org.kie.internal.task.api.TaskQueryService.WORK_ITEM_ID_LIST;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.command.Context;

@XmlRootElement(name="get-task-by-various-fields-command")
@XmlAccessorType(XmlAccessType.NONE)
public class GetTasksByVariousFieldsCommand extends UserGroupCallbackTaskCommand<List<TaskSummary>> {

	private static final long serialVersionUID = -4894264083829084547L;

	@XmlElement
    private List<Long> workItemIds;
    
    @XmlElement
    private List<Long> taskIds;
    
    @XmlElement(name="process-instance-ids")
    private List<Long> procInstIds;
    
    @XmlElement(name="business-admin")
    private List<String> busAdmins;
    
    @XmlElement(name="potential-owner")
    private List<String> potOwners;
    
    @XmlElement(name="task-owner")
    private List<String> taskOwners;
    
    @XmlElement(name="status")
    private List<Status> statuses;
    
    @XmlElement
    @XmlSchemaType(name="boolean")
    private Boolean union;
    
    @XmlElement
    private List<String> language;
    
    
	public GetTasksByVariousFieldsCommand() {
	}
	
	public GetTasksByVariousFieldsCommand(List<Long> workItemIds, List<Long> taskIds, List<Long> procInstIds,
	        List<String> busAdmins, List<String> potOwners, List<String> taskOwners, List<Status> statuses, 
	        boolean union) { 
	    this(workItemIds, taskIds, procInstIds, busAdmins, potOwners, taskOwners, statuses, null, union);
	}
	
	public GetTasksByVariousFieldsCommand(List<Long> workItemIds, List<Long> taskIds, List<Long> procInstIds,
	        List<String> busAdmins, List<String> potOwners, List<String> taskOwners, List<Status> statuses, 
	        List<String> language, boolean union) { 
		this.workItemIds = workItemIds;
		this.taskIds = taskIds;
		this.procInstIds = procInstIds;
		this.busAdmins = busAdmins;
		this.potOwners = potOwners;
		this.taskOwners = taskOwners;
		this.statuses = statuses;
		this.union = union;
		this.language = language;
    }
	
	@SuppressWarnings("unchecked")
	public GetTasksByVariousFieldsCommand(Map<String, List<?>> params, boolean union) { 
	    this.union = union;
	    
	    if( params == null ) { 
	        return;
	    }
	    this.workItemIds = (List<Long>) params.get(WORK_ITEM_ID_LIST);
	    this.taskIds = (List<Long>) params.get(TASK_ID_LIST);
	    this.procInstIds = (List<Long>) params.get(PROCESS_INST_ID_LIST);
	    this.busAdmins = (List<String>) params.get(BUSINESS_ADMIN_ID_LIST);
	    this.potOwners = (List<String>) params.get(POTENTIAL_OWNER_ID_LIST);
	    this.taskOwners = (List<String>) params.get(ACTUAL_OWNER_ID_LIST);
	    this.statuses = (List<Status>) params.get(STATUS_LIST);
	    this.language = (List<String>) params.get(LANGUAGE);
	}

	public List<TaskSummary> execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        
        potOwners = populateOrganizationalEntityWithGroupInfo(potOwners, context);
    	busAdmins = populateOrganizationalEntityWithGroupInfo(busAdmins, context);
        return context.getTaskQueryService().getTasksByVariousFields(workItemIds, taskIds, procInstIds, busAdmins, potOwners, taskOwners, statuses, language, union);
    }

    public List<Long> getWorkItemIds() {
        return workItemIds;
    }

    public void setWorkItemIds(List<Long> workItemIds) {
        this.workItemIds = workItemIds;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public List<Long> getProcInstIds() {
        return procInstIds;
    }

    public void setProcInstIds(List<Long> procInstIds) {
        this.procInstIds = procInstIds;
    }

    public List<String> getBusAdmins() {
        return busAdmins;
    }

    public void setBusAdmins(List<String> busAdmins) {
        this.busAdmins = busAdmins;
    }

    public List<String> getPotOwners() {
        return potOwners;
    }

    public void setPotOwners(List<String> potOwners) {
        this.potOwners = potOwners;
    }

    public List<String> getTaskOwners() {
        return taskOwners;
    }

    public void setTaskOwners(List<String> taskOwners) {
        this.taskOwners = taskOwners;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public Boolean getUnion() {
        return union;
    }

    public void setUnion(Boolean union) {
        this.union = union;
    }
    
    /**
     * Populates given list with group information taken from UserGroupCallback implementation
     * to allow proper query for tasks based on user assignments.
     * @param entities - "raw" list of organizational entities 
     * @return if list is not null and not empty returns list of org entities populated with group info, otherwise same as argument
     */
    protected List<String> populateOrganizationalEntityWithGroupInfo(List<String> entities, TaskContext context) {
    	if (entities != null && entities.size() > 0) {
    		Set<String> groupIds = new HashSet<String>();
    		for (String userId : entities) {
    			List<String> tmp = doUserGroupCallbackOperation(userId, null, context);
    			if (tmp != null) {
    				groupIds.addAll(tmp);
    			}
    		}
    		groupIds.addAll(entities);
    		return new ArrayList<String>(groupIds);
    	}
    	
    	return entities;
    }

}