package org.jbpm.services.task.persistence;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.drools.core.command.CommandService;
import org.drools.core.command.Interceptor;
import org.drools.core.command.impl.AbstractInterceptor;
import org.drools.persistence.OrderedTransactionSynchronization;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionManagerHelper;
import org.drools.persistence.jta.JtaTransactionManager;
import org.kie.api.command.Command;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.Task;
import org.kie.internal.command.Context;
import org.kie.internal.command.World;
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
	
	private CommandService             commandService;
    private TransactionManager         txm;
    private TaskPersistenceContextManager  tpm;
    private boolean eagerDisabled = true;
    
    public TaskTransactionInterceptor(Environment environment) {
    	this.eagerDisabled = Boolean.getBoolean("jbpm.ht.eager.disabled");
    	initTransactionManager(environment);
    }
	
	@Override
	public synchronized <T> T execute(Command<T> command) {
		boolean transactionOwner = false;
		T result = null;
		
        try {
            transactionOwner = txm.begin();
            tpm.beginCommandScopedEntityManager();
            TransactionManagerHelper.registerTransactionSyncInContainer(this.txm, new TaskSynchronizationImpl( this ));
                
            result = executeNext((Command<T>) command);
            postInit(result);
            txm.commit( transactionOwner );

            return result;

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
	
	public void addInterceptor(Interceptor interceptor) {
        interceptor.setNext( this.commandService == null ? this : this.commandService );
        this.commandService = interceptor;
    }
	
	@Override
	public Context getContext() {
		
		final TaskPersistenceContext persistenceContext = tpm.getPersistenceContext(); 
		persistenceContext.joinTransaction();
	
        return new TaskContext() {
			
			@Override
			public void set(String identifier, Object value) {	
				txm.putResource(identifier, value);
			}
			
			@Override
			public void remove(String identifier) {
			}
			
			@Override
			public String getName() {
				return null;
			}
			
			@Override
			public World getContextManager() {
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
		};
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
                this.txm = new JtaTransactionManager( env.get( EnvironmentName.TRANSACTION ),
                                                      env.get( EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY ),
                                                      tm );
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

			this.service.tpm.endCommandScopedEntityManager();

		}

		public void beforeCompletion() {
			// not used
		}

	}

}
