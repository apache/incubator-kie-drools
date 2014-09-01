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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.jbpm.process.audit.strategy.PersistenceStrategy;
import org.jbpm.process.audit.strategy.PersistenceStrategyType;
import org.jbpm.process.audit.strategy.StandaloneJtaStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
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
    
    private PersistenceStrategy persistenceStrategy;
    
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
    @SuppressWarnings("unchecked")
    public List<ProcessInstanceLog> findProcessInstances() {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        List<ProcessInstanceLog> result = em.createQuery("FROM ProcessInstanceLog").getResultList();
        closeEntityManager(em, newTx);
        return result;
    }

    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findProcessInstances(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ProcessInstanceLog> findProcessInstances(String processId) {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        List<ProcessInstanceLog> result = em
            .createQuery("FROM ProcessInstanceLog p WHERE p.processId = :processId")
                .setParameter("processId", processId).getResultList();
        closeEntityManager(em, newTx);
        return result;
    }

    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findActiveProcessInstances(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<ProcessInstanceLog> findActiveProcessInstances(String processId) {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        List<ProcessInstanceLog> result = em
            .createQuery("FROM ProcessInstanceLog p WHERE p.processId = :processId AND p.end is null")
                .setParameter("processId", processId).getResultList();
        closeEntityManager(em, newTx);
        return result;
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
    @SuppressWarnings("unchecked")
    public List<ProcessInstanceLog> findSubProcessInstances(long processInstanceId) {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        List<ProcessInstanceLog> result = em
            .createQuery("FROM ProcessInstanceLog p WHERE p.parentProcessInstanceId = :processInstanceId")
                .setParameter("processInstanceId", processInstanceId).getResultList();
        closeEntityManager(em, newTx);
        return result;
    }
    
    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findNodeInstances(long)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<NodeInstanceLog> findNodeInstances(long processInstanceId) {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        List<NodeInstanceLog> result = em
            .createQuery("FROM NodeInstanceLog n WHERE n.processInstanceId = :processInstanceId ORDER BY date,id")
                .setParameter("processInstanceId", processInstanceId).getResultList();
        closeEntityManager(em, newTx);
        return result;
    }

    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findNodeInstances(long, java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<NodeInstanceLog> findNodeInstances(long processInstanceId, String nodeId) {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        List<NodeInstanceLog> result = em
            .createQuery("FROM NodeInstanceLog n WHERE n.processInstanceId = :processInstanceId AND n.nodeId = :nodeId ORDER BY date,id")
                .setParameter("processInstanceId", processInstanceId)
                .setParameter("nodeId", nodeId).getResultList();
        closeEntityManager(em, newTx);
        return result;
    }

    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findVariableInstances(long)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VariableInstanceLog> findVariableInstances(long processInstanceId) {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        List<VariableInstanceLog> result = em
            .createQuery("FROM VariableInstanceLog v WHERE v.processInstanceId = :processInstanceId ORDER BY date")
                .setParameter("processInstanceId", processInstanceId).getResultList();
        closeEntityManager(em, newTx);
        return result;
    }

    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#findVariableInstances(long, java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<VariableInstanceLog> findVariableInstances(long processInstanceId, String variableId) {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        List<VariableInstanceLog> result = em
            .createQuery("FROM VariableInstanceLog v WHERE v.processInstanceId = :processInstanceId AND v.variableId = :variableId ORDER BY date")
                .setParameter("processInstanceId", processInstanceId)
                .setParameter("variableId", variableId).getResultList();
        closeEntityManager(em, newTx);
        return result;
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<VariableInstanceLog> findVariableInstancesByName(String variableId, boolean onlyActiveProcesses) {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
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
        List<VariableInstanceLog> result = query.setParameter("variableId", variableId).getResultList();
        closeEntityManager(em, newTx);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<VariableInstanceLog> findVariableInstancesByNameAndValue(String variableId, String value, boolean onlyActiveProcesses) {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
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
        List<VariableInstanceLog> result = query
                .setParameter("variableId", variableId)
                .setParameter("value", value)
                .getResultList();
        closeEntityManager(em, newTx);
        return result;
    }
    
    /* (non-Javadoc)
     * @see org.jbpm.process.audit.AuditLogService#clear()
     */
    @Override
    @SuppressWarnings("unchecked")
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


}
