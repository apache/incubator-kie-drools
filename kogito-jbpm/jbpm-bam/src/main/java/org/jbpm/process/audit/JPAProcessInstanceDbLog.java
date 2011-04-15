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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;

public class JPAProcessInstanceDbLog {
	
	private Environment env;
	private EntityManagerFactory emf;
	private EntityManager em;
		
	public JPAProcessInstanceDbLog(){
	}
    
	public JPAProcessInstanceDbLog(Environment env){
		this.env = env;
	}
    
    @SuppressWarnings("unchecked")
	public List<ProcessInstanceLog> findProcessInstances() {
    	List<ProcessInstanceLog> result = getEntityManager()
    		.createQuery("FROM ProcessInstanceLog").getResultList();
        return result;
    }

    @SuppressWarnings("unchecked")
	public List<ProcessInstanceLog> findProcessInstances(String processId) {
    	List<ProcessInstanceLog> result = getEntityManager()
    		.createQuery("FROM ProcessInstanceLog p WHERE p.processId = :processId")
    			.setParameter("processId", processId).getResultList();
    	return result;
    }

	@SuppressWarnings("unchecked")
	public List<ProcessInstanceLog> findActiveProcessInstances(String processId) {
		List<ProcessInstanceLog> result = getEntityManager()
			.createQuery("FROM ProcessInstanceLog p WHERE p.processId = :processId AND p.end is null")
				.setParameter("processId", processId).getResultList();
		return result;
	}

	public ProcessInstanceLog findProcessInstance(long processInstanceId) {
    	ProcessInstanceLog result = (ProcessInstanceLog) getEntityManager()
			.createQuery("FROM ProcessInstanceLog p WHERE p.processInstanceId = :processInstanceId")
				.setParameter("processInstanceId", processInstanceId).getSingleResult();
		return result;
    }
	
    @SuppressWarnings("unchecked")
	public List<NodeInstanceLog> findNodeInstances(long processInstanceId) {
    	List<NodeInstanceLog> result = getEntityManager()
			.createQuery("FROM NodeInstanceLog n WHERE n.processInstanceId = :processInstanceId ORDER BY date")
				.setParameter("processInstanceId", processInstanceId).getResultList();
		return result;
    }

    @SuppressWarnings("unchecked")
	public List<NodeInstanceLog> findNodeInstances(long processInstanceId, String nodeId) {
    	List<NodeInstanceLog> result = getEntityManager()
			.createQuery("FROM NodeInstanceLog n WHERE n.processInstanceId = :processInstanceId AND n.nodeId = :nodeId ORDER BY date")
				.setParameter("processInstanceId", processInstanceId)
				.setParameter("nodeId", nodeId).getResultList();
    	return result;
    }

    @SuppressWarnings("unchecked")
	public List<VariableInstanceLog> findVariableInstances(long processInstanceId) {
    	List<VariableInstanceLog> result = getEntityManager()
			.createQuery("FROM VariableInstanceLog v WHERE v.processInstanceId = :processInstanceId ORDER BY date")
				.setParameter("processInstanceId", processInstanceId).getResultList();
		return result;
    }

    @SuppressWarnings("unchecked")
	public List<VariableInstanceLog> findVariableInstances(long processInstanceId, String variableId) {
    	List<VariableInstanceLog> result = getEntityManager()
			.createQuery("FROM VariableInstanceLog v WHERE v.processInstanceId = :processInstanceId AND v.variableId = :variableId ORDER BY date")
				.setParameter("processInstanceId", processInstanceId)
				.setParameter("variableId", variableId).getResultList();
    	return result;
    }

	@SuppressWarnings("unchecked")
	public void clear() {
	    try {
	        UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
	        ut.begin();
	        List<ProcessInstanceLog> processInstances = getEntityManager()
				.createQuery("FROM ProcessInstanceLog").getResultList();
	        for (ProcessInstanceLog processInstance: processInstances) {
	        	getEntityManager().remove(processInstance);
	        }
	    	List<NodeInstanceLog> nodeInstances = getEntityManager()
				.createQuery("FROM NodeInstanceLog").getResultList();
		    for (NodeInstanceLog nodeInstance: nodeInstances) {
		    	getEntityManager().remove(nodeInstance);
		    }
		    em.joinTransaction();
			ut.commit();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (RollbackException e) {
			e.printStackTrace();
		} catch (HeuristicMixedException e) {
			e.printStackTrace();
		} catch (HeuristicRollbackException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (NotSupportedException e) {
			e.printStackTrace();
		}
    }
	
	protected EntityManager getEntityManager() {
		if(em == null){
			if (env == null) {
				if (emf == null) {
					emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
				}
			    em = emf.createEntityManager();
			} else {
				EntityManagerFactory emf = (EntityManagerFactory) env.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
				em = emf.createEntityManager();	
			}
		}	
		return em;
	}
	
	public void dispose() {
		em.close();
		em = null;
		if (emf != null) {
			emf.close();
		}
	}

}
