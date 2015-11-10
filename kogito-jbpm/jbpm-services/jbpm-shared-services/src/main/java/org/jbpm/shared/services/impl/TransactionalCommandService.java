/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.shared.services.impl;

import javax.persistence.EntityManagerFactory;

import org.drools.core.command.CommandService;
import org.drools.core.command.impl.GenericCommand;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionManagerFactory;
import org.kie.api.command.Command;
import org.kie.internal.command.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionalCommandService implements CommandService {
	
	private static final Logger logger = LoggerFactory.getLogger(TransactionalCommandService.class);
	
	private EntityManagerFactory emf;	
    private Context context;
    private TransactionManager txm;
    
    public TransactionalCommandService(EntityManagerFactory emf, TransactionManager txm) {
        this.emf = emf;
        this.txm = txm;
    }

	public TransactionalCommandService(EntityManagerFactory emf) {
	    this(emf, TransactionManagerFactory.get().newTransactionManager());
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
            context.close(transactionOwner);
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
