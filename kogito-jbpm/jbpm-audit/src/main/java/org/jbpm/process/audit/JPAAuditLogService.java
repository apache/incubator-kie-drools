/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.audit;

import static org.kie.internal.query.QueryParameterIdentifiers.ASCENDING_VALUE;
import static org.kie.internal.query.QueryParameterIdentifiers.DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.DESCENDING_VALUE;
import static org.kie.internal.query.QueryParameterIdentifiers.DURATION_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.END_DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.EXTERNAL_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.FILTER;
import static org.kie.internal.query.QueryParameterIdentifiers.FIRST_RESULT;
import static org.kie.internal.query.QueryParameterIdentifiers.FLUSH_MODE;
import static org.kie.internal.query.QueryParameterIdentifiers.IDENTITY_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.LAST_VARIABLE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.MAX_RESULTS;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.NODE_TYPE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.OLD_VALUE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.ORDER_BY;
import static org.kie.internal.query.QueryParameterIdentifiers.ORDER_TYPE;
import static org.kie.internal.query.QueryParameterIdentifiers.OUTCOME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_INSTANCE_STATUS_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_NAME_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.PROCESS_VERSION_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.START_DATE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.VALUE_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.VARIABLE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.VARIABLE_INSTANCE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.VAR_VALUE_ID_LIST;
import static org.kie.internal.query.QueryParameterIdentifiers.VAR_VAL_SEPARATOR;
import static org.kie.internal.query.QueryParameterIdentifiers.WORK_ITEM_ID_LIST;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.jbpm.process.audit.query.NodeInstLogQueryBuilderImpl;
import org.jbpm.process.audit.query.NodeInstanceLogDeleteBuilderImpl;
import org.jbpm.process.audit.query.ProcInstLogQueryBuilderImpl;
import org.jbpm.process.audit.query.ProcessInstanceLogDeleteBuilderImpl;
import org.jbpm.process.audit.query.VarInstLogQueryBuilderImpl;
import org.jbpm.process.audit.query.VarInstanceLogDeleteBuilderImpl;
import org.jbpm.process.audit.strategy.PersistenceStrategy;
import org.jbpm.process.audit.strategy.PersistenceStrategyType;
import org.jbpm.process.audit.strategy.StandaloneJtaStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.query.QueryAndParameterAppender;
import org.kie.internal.query.QueryModificationService;
import org.kie.internal.query.data.QueryData;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.NodeInstanceLogQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.VariableInstanceLogQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * </p>
 * The idea here is that we have a entity manager factory (similar to a session factory) 
 * that we repeatedly use to generate an entity manager (which is a persistence context) 
 * for the specific service command. 
 * </p>
 * While ProcessInstanceLog (and other *Log) entities do not contain LOB's 
 * (which sometimes necessitate the use of tx's even in <i>read</i> situations), 
 * we use transactions here none-the-less, just to be safe. Obviously, if 
 * there is already a running transaction present, we don't do anything
 * to it. 
 * </p>
 * At the end of every command, we make sure to close the entity manager
 * we've been using -- which also means that we detach any entities that
 * might be associated with the entity manager/persistence context. 
 * After all, this is a <i>service</i> which means our philosophy here 
 * is to provide a real interface, and not a leaky one. 
 */
public class JPAAuditLogService implements AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(JPAAuditLogService.class);
    
    protected PersistenceStrategy persistenceStrategy;
    
    private String persistenceUnitName = "org.jbpm.persistence.jpa";
    
    public JPAAuditLogService() {
        EntityManagerFactory emf = null;
        try { 
           emf = Persistence.createEntityManagerFactory(persistenceUnitName); 
        } catch( Exception e ) { 
           logger.info("The '" + persistenceUnitName + "' peristence unit is not available, no persistence strategy set for " + this.getClass().getSimpleName());
        }
        if( emf != null ) { 
            persistenceStrategy = new StandaloneJtaStrategy(emf);
        }
    }
    
    public JPAAuditLogService(Environment env, PersistenceStrategyType type) {
        persistenceStrategy = PersistenceStrategyType.getPersistenceStrategy(type, env);
    }
    
    public JPAAuditLogService(Environment env) {
        EntityManagerFactory emf = (EntityManagerFactory) env.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        if( emf != null ) { 
            persistenceStrategy = new StandaloneJtaStrategy(emf);
        } else { 
            persistenceStrategy = new StandaloneJtaStrategy(Persistence.createEntityManagerFactory(persistenceUnitName));
        } 
    }
    
    public JPAAuditLogService(EntityManagerFactory emf){
        persistenceStrategy = new StandaloneJtaStrategy(emf);
    }
    
    public JPAAuditLogService(EntityManagerFactory emf, PersistenceStrategyType type){
        persistenceStrategy = PersistenceStrategyType.getPersistenceStrategy(type, emf);
    }
    
    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#setPersistenceUnitName(java.lang.String)
     */
    public void setPersistenceUnitName(String persistenceUnitName) {
        persistenceStrategy = new StandaloneJtaStrategy(Persistence.createEntityManagerFactory(persistenceUnitName));
        this.persistenceUnitName = persistenceUnitName;
    }

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findProcessInstances()
     */
    
    @Override
    public List<ProcessInstanceLog> findProcessInstances() {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("FROM ProcessInstanceLog");
        return executeQuery(query, em, ProcessInstanceLog.class);
    }

    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findActiveProcessInstances(java.lang.String)
     */
    @Override
    public List<ProcessInstanceLog> findActiveProcessInstances() {
        EntityManager em = getEntityManager();
        Query query = em
                .createQuery("FROM ProcessInstanceLog p WHERE p.end is null");
        return executeQuery(query, em, ProcessInstanceLog.class);
    }
    
    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findProcessInstances(java.lang.String)
     */
    @Override
    public List<ProcessInstanceLog> findProcessInstances(String processId) {
        EntityManager em = getEntityManager();
            Query query = em
                    .createQuery("FROM ProcessInstanceLog p WHERE p.processId = :processId")
                    .setParameter("processId", processId);
        return executeQuery(query, em, ProcessInstanceLog.class);
    }

    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findActiveProcessInstances(java.lang.String)
     */
    @Override
    public List<ProcessInstanceLog> findActiveProcessInstances(String processId) {
        EntityManager em = getEntityManager();
        Query query = em
                .createQuery("FROM ProcessInstanceLog p WHERE p.processId = :processId AND p.end is null")
                .setParameter("processId", processId);
        return executeQuery(query, em, ProcessInstanceLog.class);
    }

    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findProcessInstance(long)
     */
    @Override
    public ProcessInstanceLog findProcessInstance(long processInstanceId) {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        try {
        	return (ProcessInstanceLog) em
        	        .createQuery("FROM ProcessInstanceLog p WHERE p.processInstanceId = :processInstanceId")
        	        .setParameter("processInstanceId", processInstanceId).getSingleResult();
        } catch (NoResultException e) {
        	return null;
        } finally {
        	closeEntityManager(em, newTx);
        }
    }
    
    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findSubProcessInstances(long)
     */
    @Override
    public List<ProcessInstanceLog> findSubProcessInstances(long processInstanceId) {
        EntityManager em = getEntityManager();
        Query query = em
            .createQuery("FROM ProcessInstanceLog p WHERE p.parentProcessInstanceId = :processInstanceId")
                .setParameter("processInstanceId", processInstanceId);
        return executeQuery(query, em, ProcessInstanceLog.class);
    }
    
    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findNodeInstances(long)
     */
    @Override
    public List<NodeInstanceLog> findNodeInstances(long processInstanceId) {
        EntityManager em = getEntityManager();
        Query query = em
            .createQuery("FROM NodeInstanceLog n WHERE n.processInstanceId = :processInstanceId ORDER BY date,id")
                .setParameter("processInstanceId", processInstanceId);
        return executeQuery(query, em, NodeInstanceLog.class);
    }

    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findNodeInstances(long, java.lang.String)
     */
    @Override
    public List<NodeInstanceLog> findNodeInstances(long processInstanceId, String nodeId) {
        EntityManager em = getEntityManager();
        Query query = em
            .createQuery("FROM NodeInstanceLog n WHERE n.processInstanceId = :processInstanceId AND n.nodeId = :nodeId ORDER BY date,id")
                .setParameter("processInstanceId", processInstanceId)
                .setParameter("nodeId", nodeId);
        return executeQuery(query, em, NodeInstanceLog.class);
    }

    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findVariableInstances(long)
     */
    @Override
    public List<VariableInstanceLog> findVariableInstances(long processInstanceId) {
        EntityManager em = getEntityManager();
        Query query = em
            .createQuery("FROM VariableInstanceLog v WHERE v.processInstanceId = :processInstanceId ORDER BY date")
                .setParameter("processInstanceId", processInstanceId);
        return executeQuery(query, em, VariableInstanceLog.class);
    }

    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findVariableInstances(long, java.lang.String)
     */
    @Override
    public List<VariableInstanceLog> findVariableInstances(long processInstanceId, String variableId) {
        EntityManager em = getEntityManager();
        Query query = em
            .createQuery("FROM VariableInstanceLog v WHERE v.processInstanceId = :processInstanceId AND v.variableId = :variableId ORDER BY date")
                .setParameter("processInstanceId", processInstanceId)
                .setParameter("variableId", variableId);
        return executeQuery(query, em, VariableInstanceLog.class);
    }


    @Override
    public List<VariableInstanceLog> findVariableInstancesByName(String variableId, boolean onlyActiveProcesses) {
        EntityManager em = getEntityManager();
        Query query;
        if( ! onlyActiveProcesses ) { 
             query = em.createQuery("FROM VariableInstanceLog v WHERE v.variableId = :variableId ORDER BY date");
        } else { 
            query = em.createQuery(
                    "SELECT v "
                    + "FROM VariableInstanceLog v, ProcessInstanceLog p "
                    + "WHERE v.processInstanceId = p.processInstanceId "
                    + "AND v.variableId = :variableId "
                    + "AND p.end is null "
                    + "ORDER BY v.date");
        }
        query.setParameter("variableId", variableId);
        return executeQuery(query, em, VariableInstanceLog.class);
    }

    @Override
    public List<VariableInstanceLog> findVariableInstancesByNameAndValue(String variableId, String value, boolean onlyActiveProcesses) {
        EntityManager em = getEntityManager();
        Query query;
        if( ! onlyActiveProcesses ) { 
             query = em.createQuery("FROM VariableInstanceLog v WHERE v.variableId = :variableId AND v.value = :value ORDER BY date");
        } else { 
            query = em.createQuery(
                    "SELECT v "
                    + "FROM VariableInstanceLog v, ProcessInstanceLog p "
                    + "WHERE v.processInstanceId = p.processInstanceId "
                    + "AND v.variableId = :variableId "
                    + "AND v.value = :value "
                    + "AND p.end is null "
                    + "ORDER BY v.date");
        }
        query.setParameter("variableId", variableId).setParameter("value", value);
        
        return executeQuery(query, em, VariableInstanceLog.class);
    }
    
    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#clear()
     */
    @Override
    public void clear() {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        
        List<ProcessInstanceLog> processInstances = em.createQuery("FROM ProcessInstanceLog").getResultList();
        for (ProcessInstanceLog processInstance: processInstances) {
            em.remove(processInstance);
        }
        List<NodeInstanceLog> nodeInstances = em.createQuery("FROM NodeInstanceLog").getResultList();
        for (NodeInstanceLog nodeInstance: nodeInstances) {
            em.remove(nodeInstance);
        }
        List<VariableInstanceLog> variableInstances = em.createQuery("FROM VariableInstanceLog").getResultList();
        for (VariableInstanceLog variableInstance: variableInstances) {
            em.remove(variableInstance);
        }           
        closeEntityManager(em, newTx);
    }

    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#dispose()
     */
    @Override
    public void dispose() {
        persistenceStrategy.dispose();
    }

    private EntityManager getEntityManager() {
        return persistenceStrategy.getEntityManager();
    }

    private Object joinTransaction(EntityManager em) {
        return persistenceStrategy.joinTransaction(em);
    }

    private void closeEntityManager(EntityManager em, Object transaction) {
       persistenceStrategy.leaveTransaction(em, transaction);
    }

    private <T> List<T> executeQuery(Query query, EntityManager em, Class<T> type) { 
        Object newTx = joinTransaction(em);
        List<T> result;
        try { 
            result = query.getResultList();
        } finally { 
            closeEntityManager(em, newTx);
        }
        return result;
    }
    
    // query methods
  
    @Override
    public NodeInstanceLogQueryBuilder nodeInstanceLogQuery() {
        return new NodeInstLogQueryBuilderImpl(this);
    }

    @Override
    public VariableInstanceLogQueryBuilder variableInstanceLogQuery() {
        return new VarInstLogQueryBuilderImpl(this);
    }

    @Override
    public ProcessInstanceLogQueryBuilder processInstanceLogQuery() {
        return new ProcInstLogQueryBuilderImpl(this);
    }
    
	@Override
	public ProcessInstanceLogDeleteBuilder processInstanceLogDelete() {
		return new ProcessInstanceLogDeleteBuilderImpl(this);
	} 
	
	@Override
    public NodeInstanceLogDeleteBuilder nodeInstanceLogDelete() {
        return new NodeInstanceLogDeleteBuilderImpl(this);
    }
	
	@Override
    public VariableInstanceLogDeleteBuilder variableInstanceLogDelete() {
        return new VarInstanceLogDeleteBuilderImpl(this);
    }
    
    // internal query methods/logic
   
    @Override
    public List<org.kie.api.runtime.manager.audit.NodeInstanceLog> queryNodeInstanceLogs(QueryData queryData) {
        List<NodeInstanceLog> results = doQuery(queryData, NodeInstanceLog.class);
        return convertListToInterfaceList(results, org.kie.api.runtime.manager.audit.NodeInstanceLog.class);
    }

    @Override
    public List<org.kie.api.runtime.manager.audit.VariableInstanceLog> queryVariableInstanceLogs(QueryData queryData) { 
        List<VariableInstanceLog> results =  doQuery(queryData, VariableInstanceLog.class);
        return convertListToInterfaceList(results, org.kie.api.runtime.manager.audit.VariableInstanceLog.class);
    }

    @Override
    public List<org.kie.api.runtime.manager.audit.ProcessInstanceLog> queryProcessInstanceLogs(QueryData queryData) {
        List<ProcessInstanceLog> results = doQuery(queryData, ProcessInstanceLog.class);
        return convertListToInterfaceList(results, org.kie.api.runtime.manager.audit.ProcessInstanceLog.class);
    }

    @SuppressWarnings("unchecked")
    protected <C,I> List<I> convertListToInterfaceList( List<C>internalResult, Class<I> interfaceType ) {
        List<I> result = new ArrayList<I>(internalResult.size());
        for( C element : internalResult ) { 
           result.add((I) element);
        }
        return result;
    }

    public static String NODE_INSTANCE_LOG_QUERY = 
                    "SELECT l "
                    + "FROM NodeInstanceLog l\n";
    
    public static String VARIABLE_INSTANCE_LOG_QUERY = 
                    "SELECT l "
                    + "FROM VariableInstanceLog l\n";
    
    public static String PROCESS_INSTANCE_LOG_QUERY = 
                    "SELECT l "
                    + "FROM ProcessInstanceLog l\n";
    
    public static String NODE_INSTANCE_LOG_DELETE = 
            "DELETE "
            + "FROM NodeInstanceLog l\n";

	public static String VARIABLE_INSTANCE_LOG_DELETE = 
	            "DELETE "
	            + "FROM VariableInstanceLog l\n";
	
	public static String PROCESS_INSTANCE_LOG_DELETE = 
	            "DELETE "
	            + "FROM ProcessInstanceLog l\n";
	 
   
    public static Map<String, String> criteriaFields = new ConcurrentHashMap<String, String>();
    public static Map<String, Class<?>> criteriaFieldClasses = new ConcurrentHashMap<String, Class<?>>();
    
    static { 
        addCriteria(PROCESS_INSTANCE_ID_LIST, "l.processInstanceId", Long.class);
        addCriteria(PROCESS_ID_LIST, "l.processId", String.class);
        addCriteria(WORK_ITEM_ID_LIST, "l.workItemId", Long.class);
        addCriteria(EXTERNAL_ID_LIST, "l.externalId", String.class);
        
        // process instance log
        addCriteria(START_DATE_LIST, "l.start", Date.class);
        addCriteria(DURATION_LIST, "l.duration", Long.class);
        addCriteria(END_DATE_LIST, "l.end", Date.class);
        addCriteria(IDENTITY_LIST, "l.identity", String.class);
        addCriteria(PROCESS_NAME_LIST, "l.processName", String.class);
        addCriteria(PROCESS_VERSION_LIST, "l.processVersion", String.class);
        addCriteria(PROCESS_INSTANCE_STATUS_LIST, "l.status", Integer.class);
        addCriteria(OUTCOME_LIST, "l.outcome", String.class);
        
        // node instance log
        addCriteria(NODE_ID_LIST, "l.nodeId", String.class);
        addCriteria(NODE_INSTANCE_ID_LIST, "l.nodeInstanceId", String.class);
        addCriteria(NODE_NAME_LIST, "l.nodeName", String.class);
        addCriteria(NODE_TYPE_LIST, "l.nodeType", String.class);
        
        // variable instance log
        addCriteria(DATE_LIST, "l.date", Date.class);
        addCriteria(OLD_VALUE_LIST, "l.oldValue", String.class);
        addCriteria(VALUE_LIST, "l.value", String.class);
        addCriteria(VARIABLE_ID_LIST, "l.variableId", String.class);
        addCriteria(VARIABLE_INSTANCE_ID_LIST, "l.variableInstanceId", String.class);
       
    }
   
    protected static void addCriteria( String listId, String fieldName, Class type ) { 
        criteriaFields.put(listId, fieldName);
        criteriaFieldClasses.put(listId, type );
    }
    
    public <T> List<T> doQuery(QueryData queryData, Class<T> resultType) { 
        // create query
        String queryBase;
        if( ProcessInstanceLog.class.equals(resultType) ) { 
            queryBase = PROCESS_INSTANCE_LOG_QUERY;
        } else if( VariableInstanceLog.class.equals(resultType) ) { 
            queryBase = VARIABLE_INSTANCE_LOG_QUERY;
        } else if( NodeInstanceLog.class.equals(resultType) ) { 
            queryBase = NODE_INSTANCE_LOG_QUERY;
        } else { 
            throw new IllegalStateException("Unsupported result type: " + resultType.getName() );
        }
        Map<String, Object> queryParams = new HashMap<String, Object>();
        String queryString = createQuery(queryBase, queryData, queryParams);
        
        // logging
        logger.debug("QUERY:\n {}", queryString);
        if( logger.isDebugEnabled() ) {
            StringBuilder paramsStr = new StringBuilder("PARAMS:");
            Map<String, Object> orderedParams = new TreeMap<String, Object>(queryParams);
            for( Entry<String, Object> entry : orderedParams.entrySet() ) { 
                paramsStr.append("\n " + entry.getKey() + " : '" + entry.getValue() + "'");
            }
            logger.debug(paramsStr.toString());
        }
        
    
        // execute query
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        Query query = em.createQuery(queryString);
    
        List<T> result = queryWithParameters(queryParams, LockModeType.NONE, resultType, query);
        
        closeEntityManager(em, newTx);
        
        return result;
    }
    
    public int doDelete(QueryData queryData, Class<?> resultType) { 
    	String queryBase;
        if( ProcessInstanceLog.class.equals(resultType) ) { 
            queryBase = PROCESS_INSTANCE_LOG_DELETE;
        } else if( VariableInstanceLog.class.equals(resultType) ) { 
            queryBase = VARIABLE_INSTANCE_LOG_DELETE;
        } else if( NodeInstanceLog.class.equals(resultType) ) { 
            queryBase = NODE_INSTANCE_LOG_DELETE;
        } else { 
            throw new IllegalStateException("Unsupported entity type: " + resultType.getName() );
        }
        return doDelete(queryBase, queryData, resultType);
    }
    
    public int doDelete(String queryBase, QueryData queryData, Class<?> resultType) { 
        // create query
        
        Map<String, Object> queryParams = new HashMap<String, Object>();
        String queryString = createQuery(queryBase, queryData, queryParams, true);
        
        // logging
        logger.debug("DELETE statement:\n {}", queryString);
        if( logger.isDebugEnabled() ) {
            StringBuilder paramsStr = new StringBuilder("PARAMS:");
            Map<String, Object> orderedParams = new TreeMap<String, Object>(queryParams);
            for( Entry<String, Object> entry : orderedParams.entrySet() ) { 
                paramsStr.append("\n " + entry.getKey() + " : '" + entry.getValue() + "'");
            }
            logger.debug(paramsStr.toString());
        }
        
    
        // execute query
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        Query query = em.createQuery(queryString);
    
        int result = executeWithParameters(queryParams, query);
        
        closeEntityManager(em, newTx);
        
        return result;
    }

    private static String createQuery(String queryBase, QueryData queryData, Map<String, Object> queryParams) {
    	return createQuery(queryBase, queryData, queryParams, false);
    }
    
    private static String createQuery(String queryBase, QueryData queryData, Map<String, Object> queryParams, boolean skipMetaParams) { 
        // setup
        StringBuilder queryBuilder = new StringBuilder(queryBase);
        QueryAndParameterAppender queryAppender = new QueryAndParameterAppender(queryBuilder, queryParams, true);

        // 1. add other tables (used in kie-remote-services to cross-query on variables, etc.. )
        ServiceLoader<QueryModificationService> queryModServiceLdr = ServiceLoader.load(QueryModificationService.class);
        for( QueryModificationService queryModService : queryModServiceLdr ) { 
           queryModService.addTablesToQuery(queryBuilder, queryData);
        }
      
        // 2. add extended criteria 
        for( QueryModificationService queryModService : queryModServiceLdr ) { 
           queryModService.addCriteriaToQuery(queryBuilder, queryData, queryAppender);
        }
        
        boolean addLastCriteria = false;
        List<Object[]> varValCriteriaList = new ArrayList<Object[]>();
        
        // 3. apply normal query parameters
        if( ! queryData.unionParametersAreEmpty() ) { 
            checkVarValCriteria((List<String>) queryData.getUnionParameters().remove(VAR_VALUE_ID_LIST), true, false, varValCriteriaList);
            if( queryData.getUnionParameters().remove(LAST_VARIABLE_LIST) != null ) { 
                addLastCriteria = true;
            }
            for( Entry<String, List<? extends Object>> paramsEntry : queryData.getUnionParameters().entrySet() ) { 
                String listId = paramsEntry.getKey();
                queryAppender.addQueryParameters(
                        paramsEntry.getValue(),
                        listId, criteriaFieldClasses.get(listId), criteriaFields.get(listId), 
                        true);
            }
        }
        if( ! queryData.intersectParametersAreEmpty() ) { 
            checkVarValCriteria((List<String>) queryData.getIntersectParameters().remove(VAR_VALUE_ID_LIST), false, false, varValCriteriaList);
            if( queryData.getIntersectParameters().remove(LAST_VARIABLE_LIST) != null ) { 
                addLastCriteria = true;
            }
            for( Entry<String, List<? extends Object>> paramsEntry : queryData.getIntersectParameters().entrySet() ) { 
                String listId = paramsEntry.getKey();
                queryAppender.addQueryParameters(
                        paramsEntry.getValue(),
                        listId, criteriaFieldClasses.get(listId), criteriaFields.get(listId), 
                        false);
            }
        }
        // 4. apply range query parameters
        if( ! queryData.unionRangeParametersAreEmpty() ) { 
            for( Entry<String, List<? extends Object>> paramsEntry : queryData.getUnionRangeParameters().entrySet() ) { 
                String listId = paramsEntry.getKey();
                queryAppender.addRangeQueryParameters(
                        paramsEntry.getValue(),
                        listId, criteriaFieldClasses.get(listId), criteriaFields.get(listId), 
                        true);
            }
        }
        if( ! queryData.intersectRangeParametersAreEmpty() ) { 
            for( Entry<String, List<? extends Object>> paramsEntry : queryData.getIntersectRangeParameters().entrySet() ) { 
                String listId = paramsEntry.getKey();
                queryAppender.addRangeQueryParameters(
                        paramsEntry.getValue(),
                        listId, criteriaFieldClasses.get(listId), criteriaFields.get(listId), 
                        false);
            }
        }
        // 5. apply regex query parameters
        if( ! queryData.unionRegexParametersAreEmpty() ) { 
            checkVarValCriteria(queryData.getUnionRegexParameters().remove(VAR_VALUE_ID_LIST), true, true, varValCriteriaList);
            for( Entry<String, List<String>> paramsEntry : queryData.getUnionRegexParameters().entrySet() ) { 
                String listId = paramsEntry.getKey();
                queryAppender.addRegexQueryParameters(
                        paramsEntry.getValue(),
                        listId, criteriaFields.get(listId), 
                        true);
            }
        }
        if( ! queryData.intersectRegexParametersAreEmpty() ) { 
            checkVarValCriteria((List<String>) queryData.getIntersectRegexParameters().remove(VAR_VALUE_ID_LIST), false, true, varValCriteriaList);
            for( Entry<String, List<String>> paramsEntry : queryData.getIntersectRegexParameters().entrySet() ) { 
                String listId = paramsEntry.getKey();
                queryAppender.addRegexQueryParameters(
                        paramsEntry.getValue(),
                        listId, criteriaFields.get(listId), 
                        false);
            }
        }
        
        if( queryAppender.hasBeenUsed() ) { 
            queryBuilder.append(")"); 
        }
        
        // 6. Add special criteria 
        boolean addWhereClause = ! queryAppender.hasBeenUsed();
        if( ! varValCriteriaList.isEmpty() ) { 
            addVarValCriteria(addWhereClause, queryBuilder, queryAppender, "l", varValCriteriaList);
            addWhereClause = false;
        }
        if( addLastCriteria ) { 
            addLastInstanceCriteria(addWhereClause, queryBuilder);
        }
       if (!skipMetaParams) {
	        // 7. apply filter, ordering, etc.. 
	        applyMetaCriteria(queryBuilder, queryData);
       }
        // 8. return query
        return queryBuilder.toString();
    }

    public static void checkVarValCriteria(List<String> varValList, boolean union, boolean regex, List<Object[]> varValCriteriaList) { 
        if( varValList == null || varValList.isEmpty() ) { 
            return;
        }
        for( Object varVal : varValList ) { 
            String [] parts = ((String) varVal).split(VAR_VAL_SEPARATOR, 2);
            String varId = parts[1].substring(0,Integer.parseInt(parts[0]));
            String val = parts[1].substring(Integer.parseInt(parts[0])+1);
            int type = ( union ? 0 : 1 ) + ( regex ? 2 : 0);
            Object [] varValCrit = { type, varId, val };
            varValCriteriaList.add(varValCrit);
        }
    }
    
    public static void addVarValCriteria(
            boolean addWhereClause, 
            StringBuilder queryBuilder, 
            QueryAndParameterAppender queryAppender, 
            String tableId,
            List<Object []> varValCriteriaList) { 
        if( ! addWhereClause ) { 
            queryBuilder.append("\n");
        }
      
       // for each var/val criteria
       for( Object [] varValCriteria : varValCriteriaList ) { 

           // Start with WHERE, OR, or AND?
           String andOr = null;
           if( addWhereClause ) { 
               queryBuilder.append( "WHERE" );
               addWhereClause = false;
           } else { 
               if( ((Integer) varValCriteria[0]) % 2 == 1 ) { 
                   andOr = "AND"; 
               } else { 
                   andOr = "OR";
               }
               queryBuilder.append( andOr );
           } 
          
           // var id: add query parameter
           String varIdQueryParamName = queryAppender.generateParamName();
           queryAppender.addNamedQueryParam(varIdQueryParamName, varValCriteria[1]);
           // var id: append to the query
           queryBuilder.append(" ( ").append(tableId).append(".variableId = :").append(varIdQueryParamName).append(" ");
           
           // val: append to the query
           queryBuilder.append("AND ").append(tableId).append(".value ");
           String valQueryParamName = queryAppender.generateParamName();
           String val;
           if( ((Integer) varValCriteria[0]) >= 2 ) { 
               val = ((String) varValCriteria[2]).replace('*', '%').replace('.', '_');
               queryBuilder.append("like :").append(valQueryParamName);
           } else { 
               val = (String) varValCriteria[2];
              queryBuilder.append("= :").append(valQueryParamName);
           }
           queryBuilder.append(" ) ");
           // val: add query parameter
           queryAppender.addNamedQueryParam(valQueryParamName, val);
       }
    }
    
    private static void addLastInstanceCriteria(boolean whereAnd, StringBuilder queryBuilder) { 
       queryBuilder.append("\n").append( (whereAnd ? "WHERE" : "AND" ) )
           .append(" (l.id IN (SELECT MAX(ll.id) FROM VariableInstanceLog ll ")
           .append("GROUP BY ll.variableId, ll.processInstanceId))");
    }
    
    private static void applyMetaCriteria(StringBuilder queryBuilder, QueryData queryData) { 
        queryBuilder
            .append(" \n ORDER by ")
            .append(adaptOrderBy(queryData.getQueryContext().getOrderBy()));
        Boolean ascending = queryData.getQueryContext().isAscending();
        if( ascending == null || ascending ) { 
            queryBuilder.append(" ").append(ASCENDING_VALUE);
        } else { 
            queryBuilder.append(" ").append(DESCENDING_VALUE);
        } 
    }
    
    private static String adaptOrderBy(String orderBy) {
        if("processInstanceId".equals(orderBy)) { 
            return "l.processInstanceId";
        } else if ("processId".equals(orderBy)) {
            return "l.processId";
        } else if( orderBy == null ) { 
            return "l.id";
        } else { 
            throw new IllegalArgumentException("Unknown order by parameter: '" + orderBy + "'");
        }
    }

    private <T> List<T> queryWithParameters(Map<String, Object> params, LockModeType lockMode, Class<T> clazz, Query query) {
        if (lockMode != null) {
            query.setLockMode(lockMode);
        }
        if (params != null && !params.isEmpty()) {
            for (String name : params.keySet()) {
                if (FIRST_RESULT.equals(name)) {
                    query.setFirstResult((Integer) params.get(name));
                    continue;
                }
                if (MAX_RESULTS.equals(name)) {
                    query.setMaxResults((Integer) params.get(name));
                    continue;
                }
                if (FLUSH_MODE.equals(name)) {
                    query.setFlushMode(FlushModeType.valueOf((String) params.get(name)));
                    continue;
                }// skip control parameters
                else if (ORDER_TYPE.equals(name) 
                        || ORDER_BY.equals(name)
                        || FILTER.equals(name)) {
                    continue;
                }
                query.setParameter(name, params.get(name));
            }
        }
        return query.getResultList();
    } 
    
    private int executeWithParameters(Map<String, Object> params, Query query) {

        if (params != null && !params.isEmpty()) {
            for (String name : params.keySet()) {
                if (FIRST_RESULT.equals(name)) {
                    query.setFirstResult((Integer) params.get(name));
                    continue;
                }
                if (MAX_RESULTS.equals(name)) {
                    query.setMaxResults((Integer) params.get(name));
                    continue;
                }
                if (FLUSH_MODE.equals(name)) {
                    query.setFlushMode(FlushModeType.valueOf((String) params.get(name)));
                    continue;
                }// skip control parameters
                else if (ORDER_TYPE.equals(name) 
                        || ORDER_BY.equals(name)
                        || FILTER.equals(name)) {
                    continue;
                }
                query.setParameter(name, params.get(name));
            }
        }
        return query.executeUpdate();
    }


}