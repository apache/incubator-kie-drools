package org.jbpm.shared.services.impl;

import javax.persistence.EntityManagerFactory;

import org.drools.core.command.CommandService;
import org.drools.core.command.impl.GenericCommand;
import org.drools.persistence.jta.JtaTransactionManager;
import org.kie.api.command.Command;
import org.kie.internal.command.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionalCommandService implements CommandService {
	
	private static final Logger logger = LoggerFactory.getLogger(TransactionalCommandService.class);
	
	private EntityManagerFactory emf;	
    private Context context;
    private JtaTransactionManager txm;
    
	public TransactionalCommandService(EntityManagerFactory emf) {
		this.emf = emf;
		this.txm = new JtaTransactionManager(null, null, null);
	}

    public Context getContext() {
        return context;
    }

    protected void setEmf(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public <T> T execute(Command<T> command) {
    	boolean transactionOwner = false;
		T result = null;
		
        try {
            transactionOwner = txm.begin();
            JpaPersistenceContext context = new JpaPersistenceContext(emf.createEntityManager());
            context.joinTransaction();
            result = ((GenericCommand<T>)command).execute(context);
            txm.commit( transactionOwner );
            context.close();
            return result;

        } catch ( RuntimeException re ) {
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

}
