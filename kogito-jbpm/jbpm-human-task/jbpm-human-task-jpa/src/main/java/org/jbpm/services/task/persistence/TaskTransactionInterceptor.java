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

package org.jbpm.services.task.persistence;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.drools.core.command.impl.AbstractInterceptor;
import org.drools.core.runtime.ChainableRunner;
import org.drools.persistence.api.OrderedTransactionSynchronization;
import org.drools.persistence.api.TransactionAware;
import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerFactory;
import org.drools.persistence.api.TransactionManagerHelper;
import org.kie.api.KieBase;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.RequestContext;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskContext;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.TaskPersistenceContextManager;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.exception.TaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskTransactionInterceptor extends AbstractInterceptor {

	private static Logger logger = LoggerFactory.getLogger(TaskTransactionInterceptor.class);
    private static String SPRING_TM_CLASSNAME = "org.springframework.transaction.support.AbstractPlatformTransactionManager";
	
	private ChainableRunner            commandService;
    private TransactionManager         txm;
    private TaskPersistenceContextManager  tpm;
    private boolean eagerDisabled = false;
    
    private Environment environment; 
    
    public TaskTransactionInterceptor(Environment environment) {
    	this.eagerDisabled = Boolean.getBoolean("jbpm.ht.eager.disabled");
    	this.environment = environment;
    	initTransactionManager(environment);
    }
	
	@Override
	public synchronized RequestContext execute( Executable executable, RequestContext ctx ) {
		boolean transactionOwner = false;

        try {
            transactionOwner = txm.begin();
            tpm.beginCommandScopedEntityManager();
            TransactionManagerHelper.registerTransactionSyncInContainer(this.txm, new TaskSynchronizationImpl( this ));
            
            if (txm != null) {
                ObjectMarshallingStrategy[] strategies = (ObjectMarshallingStrategy[]) environment.get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES);
                if (strategies != null) {
                    for (ObjectMarshallingStrategy strategy : strategies) {
                        if (strategy instanceof TransactionAware) {
                            ((TransactionAware) strategy).onStart(txm);
                        }
                    }
                }
            }
            
            RequestContext context = createContext();
            executeNext(executable, context);
            
            ctx.setResult(context.getResult());
            postInit(ctx.getResult());
            txm.commit( transactionOwner );

            return ctx;

        } catch (TaskException e) {
        	// allow to handle TaskException as business exceptions on caller side
        	// if transaction is owned by other component like process engine
        	if (transactionOwner) {
        		rollbackTransaction( e, transactionOwner );
        		e.setRecoverable(false);
        		throw e;
        	} else {
        		throw e;
        	}
        }
        catch ( RuntimeException re ) {
            rollbackTransaction( re, transactionOwner );
            throw re;
        } catch ( Exception t1 ) {
            rollbackTransaction( t1,  transactionOwner );
            throw new RuntimeException( "Wrapped exception see cause", t1 );
        }
		
	}
	
	private void rollbackTransaction(Exception t1, boolean transactionOwner) {
		try {
			logger.warn("Could not commit session", t1);
			txm.rollback(transactionOwner);
		} catch (Exception t2) {
			logger.error("Could not rollback", t2);
			throw new RuntimeException("Could not commit session or rollback", t2);
		}
	}
	
	public void addInterceptor(ChainableRunner interceptor ) {
        interceptor.setNext( this.commandService == null ? this : this.commandService );
        this.commandService = interceptor;
    }
	
	@Override
	public RequestContext createContext() {
		
		final TaskPersistenceContext persistenceContext = tpm.getPersistenceContext();
		persistenceContext.joinTransaction();
	
        return new TransactionContext(persistenceContext);
	}

	public class TransactionContext implements TaskContext, RequestContext {
		private final TaskPersistenceContext persistenceContext;
		private String userId;
		
		public TransactionContext( TaskPersistenceContext persistenceContext ) {
			this.persistenceContext = persistenceContext;
		}

		@Override
		public void set(String identifier, Object value) {
			txm.putResource(identifier, value);
		}

		@Override
		public void remove(String identifier) {
		}

		@Override
		public boolean has( String identifier ) {
			return false;

		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public Object get(String identifier) {
			return txm.getResource(identifier);
		}

		@Override
		public void setPersistenceContext(TaskPersistenceContext context) {
		}

		@Override
		public TaskPersistenceContext getPersistenceContext() {
			return persistenceContext;
		}

		@Override
		public UserGroupCallback getUserGroupCallback() {
			return null;
		}

		@Override
		public Task loadTaskVariables(Task task) {
			return task;
		}

		@Override
		public Object getResult() {
			return get("Result");
		}

		@Override
		public void setResult( Object result ) {
			set("Result", result);
		}

		@Override
		public RequestContext with( KieBase kieBase ) {
			throw new UnsupportedOperationException( "org.jbpm.services.task.persistence.TaskTransactionInterceptor.TransactionContext.with -> TODO" );

		}

		@Override
		public RequestContext with( KieSession kieSession ) {
			throw new UnsupportedOperationException( "org.jbpm.services.task.persistence.TaskTransactionInterceptor.TransactionContext.with -> TODO" );

		}

		@Override
		public Context getConversationContext() {
			throw new UnsupportedOperationException( "org.jbpm.services.task.persistence.TaskTransactionInterceptor.TransactionContext.getConversationContext -> TODO" );
		}

		@Override
		public Context getApplicationContext() {
			throw new UnsupportedOperationException( "org.jbpm.services.task.persistence.TaskTransactionInterceptor.TransactionContext.getApplicationContext -> TODO" );

		}

	    @Override
	    public String getUserId() {
	        return userId;
	    }
	    
	    public void setUserId(String userId) {
	        this.userId = userId;
	    }
	}
	
	public void initTransactionManager(Environment env) {
        Object tm = env.get( EnvironmentName.TRANSACTION_MANAGER );
        if ( env.get( EnvironmentName.TASK_PERSISTENCE_CONTEXT_MANAGER ) != null &&
             env.get( EnvironmentName.TRANSACTION_MANAGER ) != null ) {
            this.txm = (TransactionManager) tm;
            this.tpm = (TaskPersistenceContextManager) env.get( EnvironmentName.TASK_PERSISTENCE_CONTEXT_MANAGER );
        } else {
            if ( tm != null && isSpringTransactionManager(tm.getClass()) ) {
                try {
                    logger.debug( "Instantiating KieSpringTransactionManager" );
                    Class< ? > cls = Class.forName( "org.kie.spring.persistence.KieSpringTransactionManager" );
                    Constructor< ? > con = cls.getConstructors()[0];
                    this.txm = (TransactionManager) con.newInstance( tm );
                    env.set( EnvironmentName.TRANSACTION_MANAGER, this.txm );
                    cls = Class.forName( "org.kie.spring.persistence.KieSpringTaskJpaManager" );
                    con = cls.getConstructors()[0];
                    this.tpm = (TaskPersistenceContextManager) con.newInstance( new Object[]{env} );
                } catch ( Exception e ) {
    
                    logger.warn( "Could not instantiate DroolsSpringTransactionManager" );
                    throw new RuntimeException( "Could not instantiate org.kie.container.spring.beans.persistence.DroolsSpringTransactionManager", e );
                }
            } else {
                logger.debug( "Instantiating JtaTransactionManager" );
                this.txm = TransactionManagerFactory.get().newTransactionManager(env);
                env.set( EnvironmentName.TRANSACTION_MANAGER, this.txm );
                try {
                     this.tpm = new JPATaskPersistenceContextManager( env );
                } catch ( Exception e ) {
                    throw new RuntimeException( "Error creating JPATaskPersistenceContextManager", e );
                }
            }
            env.set( EnvironmentName.TASK_PERSISTENCE_CONTEXT_MANAGER,
                     this.tpm );
            env.set( EnvironmentName.TRANSACTION_MANAGER,
                     this.txm );
        }
    }


    public boolean isSpringTransactionManager( Class<?> clazz ) {
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
    
    private void postInit(Object result) {
    	if (result instanceof Task) {
    		Task task = (Task) result;
    		if (task != null && !eagerDisabled) {
    			task.getNames().size();
    			task.getDescriptions().size();
    			task.getSubjects().size();
    			task.getPeopleAssignments().getBusinessAdministrators().size();
    			task.getPeopleAssignments().getPotentialOwners().size();
    			((InternalPeopleAssignments) task.getPeopleAssignments()).getRecipients().size();
    			((InternalPeopleAssignments) task.getPeopleAssignments()).getExcludedOwners().size();
    			((InternalPeopleAssignments) task.getPeopleAssignments()).getTaskStakeholders().size();
    			task.getTaskData().getAttachments().size();
    			task.getTaskData().getComments().size();
    			((InternalTask)task).getDeadlines().getStartDeadlines().size();
    			((InternalTask)task).getDeadlines().getEndDeadlines().size();
    			if (((InternalTask)task).getDelegation() != null) {
    			    ((InternalTask)task).getDelegation().getDelegates().size();
    			}
    		}
    	} else if (result instanceof Collection<?>) {
            ((Collection<?>) result).size();
    	}	
    }
    
	private static class TaskSynchronizationImpl extends
			OrderedTransactionSynchronization {

		TaskTransactionInterceptor service;

		public TaskSynchronizationImpl(TaskTransactionInterceptor service) {
			super(1, "TaskService-"+service.toString());
			this.service = service;
		}

		public void afterCompletion(int status) {
		    if (this.service.txm != null) {
                ObjectMarshallingStrategy[] strategies = (ObjectMarshallingStrategy[]) this.service.environment.get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES);

                if (strategies != null) {
                    for (ObjectMarshallingStrategy strategy : strategies) {
                        if (strategy instanceof TransactionAware) {
                            ((TransactionAware) strategy).onEnd(this.service.txm);
                        }
                    }
                }
            }
		    
			this.service.tpm.endCommandScopedEntityManager();

		}

		public void beforeCompletion() {
		}

	}

}
