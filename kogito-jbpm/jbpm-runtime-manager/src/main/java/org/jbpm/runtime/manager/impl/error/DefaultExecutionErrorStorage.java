/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.runtime.manager.impl.error;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerFactory;
import org.jbpm.runtime.manager.impl.jpa.ExecutionErrorInfo;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.runtime.error.ExecutionError;
import org.kie.internal.runtime.error.ExecutionErrorStorage;


public class DefaultExecutionErrorStorage implements ExecutionErrorStorage {
  
    private static String SPRING_TM_CLASSNAME = "org.springframework.transaction.support.AbstractPlatformTransactionManager";
    
    private static String KIE_SPRING_TM_CLASSNAME = "org.kie.spring.persistence.KieSpringTransactionManager";
    
    private EntityManagerFactory emf;       
    private TransactionManager txm;
    
    public DefaultExecutionErrorStorage(Environment environment) {
        
        this.emf = (EntityManagerFactory) environment.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        // if there is no entity manager factory, running with in memory settings so error handling is deactivated
        if (this.emf != null) {
            this.txm = getTransactionManager(environment);
        }
    }

    @Override
    public ExecutionError store(ExecutionError error) {
        if (!isActive()) {
            return error;
        }
        return call((EntityManager em) -> {
            
            ExecutionErrorInfo errorEntity = new ExecutionErrorInfo(
                    error.getErrorId(),
                    error.getType(),
                    error.getDeploymentId(),
                    error.getProcessInstanceId(),
                    error.getProcessId(),
                    error.getActivityId(),
                    error.getActivityName(),
                    error.getJobId(),
                    error.getErrorMessage(),
                    error.getError(),
                    error.getErrorDate(),
                    error.getInitActivityId()
                    );
            
            em.persist(errorEntity);
            return error;
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ExecutionError> list(Integer page, Integer pageSize) {
        if (!isActive()) {
            return Collections.EMPTY_LIST;
        }
        int startPosition = page * pageSize;
        return call((EntityManager em) -> {
          
            return em.createQuery("from ExecutionErrorInfo")
                .setFirstResult(startPosition)
                .setMaxResults(pageSize)
                .getResultList();
        });

    }

    @Override
    public ExecutionError get(String errorId) {
        if (!isActive()) {
            return null;
        }
        return (ExecutionError) call((EntityManager em) -> {
            
            return em.createQuery("from ExecutionErrorInfo where errorId =:errorId")
                .setParameter("errorId", errorId)
                .getSingleResult();
        });
    }

    @Override
    public void acknowledge(final String user, final String...errorIds) {
        if (!isActive()) {
            return;
        }
        call((EntityManager em) -> {
            
            for (String errorId : errorIds) {
                ExecutionError error = (ExecutionError) em.createQuery("from ExecutionErrorInfo where errorId =:errorId")
                    .setParameter("errorId", errorId)
                    .getSingleResult();
                
                error.setAcknowledged(true);
                error.setAcknowledgedBy(user);
                error.setAcknowledgedAt(new Date());
                
                em.merge(error);
                
            }
            
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ExecutionError> listByProcessInstance(Long processInstanceId, Integer page, Integer pageSize) {
        if (!isActive()) {
            return Collections.EMPTY_LIST;
        }
        int startPosition = page * pageSize;
        return call((EntityManager em) -> {
          
            return em.createQuery("from ExecutionErrorInfo where processInstanceId =:processInstanceId")
                .setParameter("processInstanceId", processInstanceId)
                .setFirstResult(startPosition)
                .setMaxResults(pageSize)
                .getResultList();
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ExecutionError> listByActivity(String activityName, Integer page, Integer pageSize) {
        if (!isActive()) {
            return Collections.EMPTY_LIST;
        }
        int startPosition = page * pageSize;
        return call((EntityManager em) -> {
          
            return em.createQuery("from ExecutionErrorInfo where activityName =:activityName")
                .setParameter("activityName", activityName)
                .setFirstResult(startPosition)
                .setMaxResults(pageSize)
                .getResultList();
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ExecutionError> listByDeployment(String deploymentId, Integer page, Integer pageSize) {
        if (!isActive()) {
            return Collections.EMPTY_LIST;
        }
        int startPosition = page * pageSize;
        return call((EntityManager em) -> {
          
            return em.createQuery("from ExecutionErrorInfo where deploymentId =:deploymentId")
                .setParameter("deploymentId", deploymentId)
                .setFirstResult(startPosition)
                .setMaxResults(pageSize)
                .getResultList();
        });
    }
    
   /*
    * Helper methods
    */
    
    protected <R> R call(Function<EntityManager, R> function) {
        
        boolean transactionOwner = false;    
        try {
            transactionOwner = txm.begin();
            EntityManager em = emf.createEntityManager();
            R result = function.apply(em);
            txm.commit( transactionOwner );
            em.close();
            return result;
        } catch (Exception e) {
            txm.rollback(transactionOwner);
            throw new RuntimeException( "Exception when persisting error information", e);
        }       
    }
    

    protected TransactionManager getTransactionManager(Environment environment) {
        Object tx = environment.get(EnvironmentName.TRANSACTION_MANAGER);
        if (tx != null) {
            if (tx instanceof TransactionManager) {
                return (TransactionManager) tx;
            } if (isSpringTransactionManager(tx.getClass())) {
                try {
                    Class< ? > cls = Class.forName(KIE_SPRING_TM_CLASSNAME);
                    Constructor< ? > con = cls.getConstructors()[0];
                    return (TransactionManager) con.newInstance( tx );
                } catch (Exception e) {
                    throw new RuntimeException("Not possible to create spring transaction manager", e);
                }
            }
        }
        return TransactionManagerFactory.get().newTransactionManager(environment);
        
    }
    
    protected boolean isSpringTransactionManager( Class<?> clazz ) {
        if ( SPRING_TM_CLASSNAME.equals(clazz.getName()) ) {
            return true;
        }
        // Try to find from the ancestors
        if (clazz.getSuperclass() != null)
        {
            return isSpringTransactionManager(clazz.getSuperclass());
        }
        return false;
    }
    
    protected boolean isActive() {
        if (this.emf != null && this.txm != null) {
            return true;
        }
        
        return false;
    }
}
