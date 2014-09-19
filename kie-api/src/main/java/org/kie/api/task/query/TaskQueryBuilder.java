package org.kie.api.task.query;

import java.util.List;

import org.kie.api.query.ParametrizedQuery;
import org.kie.api.query.ParametrizedQueryBuilder;
import org.kie.api.runtime.manager.audit.query.NodeInstanceLogQueryBuilder;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;

/**
 * An instance of this class is used to dynamically
 * create a query to retrieve {@link TaskSummary} instances. 
 * </p>
 * One of the main motivations behind this class is that 
 * adding new methods to this method provides a (factorial) 
 * increase in ways to query for {@link TaskSummary} instances 
 * without unnecessarily cluttering up the interface, unlike 
 * the deprecated "get*" method signatures, 
 */
public interface TaskQueryBuilder extends ParametrizedQueryBuilder<TaskQueryBuilder> {

    /**
     * Add one or more work item ids as a criteria to the query
     * @param workItemId one or more 
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder workItemId(long... workItemId); 
    
    /**
     * Add one or more task ids as a criteria to the query
     * @param taskId
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder taskId(long... taskId); 
    
    /**
     * Add one or more process instance ids as a criteria to the query
     * @param processInstanceId
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder processInstanceId(long... processInstanceId); 
    
    /**
     * Add one or more initiator ids as a criteria to the query
     * @param createdById
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder initiator(String... createdById); 
    
    /**
     * Add one or more stake holder ids as a criteria to the query
     * @param stakeHolderId
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder stakeHolder(String... stakeHolderId); 
    
    /**
     * Add one or more potential owner ids as a criteria to the query
     * @param potentialOwnerId
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder potentialOwner(String... potentialOwnerId); 
    
    /**
     * Add one or more (actual) task owner ids as a criteria to the query
     * @param taskOwnerId
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder taskOwner(String... taskOwnerId); 
    
    /**
     * Add one or more business administrator ids as a criteria to the query
     * @param businessAdminId
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder businessAdmin(String... businessAdminId); 
    
    /**
     * Add one or more statuses as a criteria to the query
     * @param status
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder status(Status... status); 
    
    /**
     * Add one or more deployment ids as a criteria to the query
     * @param deploymentId
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder deploymentId(String... deploymentId); 
    
    /**
     * Add a language as a criteria to the query
     * @param language
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder language(String language); 
    
    /**
     * Limit the number of results returned by the query
     * @param maxResults
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder maxResults(int maxResults);
    
    /**
     * Retrieves results starting at the offset specified
     * @param offset
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder offset(int offset);
    
    /**
     * Order the results retrieved by the given parameter
     * </p>
     * results are ordered by default by task id.
     * @param orderBy
     * @return the current {@link TaskQueryBuilder} instance
     */
    public TaskQueryBuilder orderBy(OrderBy orderBy);
    
    /**
     * An enum used to specify the criteria for ordering the results of the query
     */
    public static enum OrderBy { 
        taskId, processInstanceId,
        taskName, taskStatus, 
        createdOn, createdBy;
    }
    
    /**
     * Create the {@link ParametrizedQuery} instance that can be used
     * to retrieve the results, a {@link List<TaskSummary>} instance.
     * </p>
     * Further modifications to the {@link TaskQueryBuilder} instance
     * will <em>not</em> affect the query criteria used in the {@link ParametrizedQuery} 
     * produced by this method.
     * @return The results of the query
     */
    public ParametrizedQuery<TaskSummary> buildQuery();
}
