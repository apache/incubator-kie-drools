package org.infinispan.transaction.lookup;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import bitronix.tm.TransactionManagerServices;

public class BitronixTransactionManagerLookup implements
		TransactionManagerLookup {

	@Override
	public TransactionManager getTransactionManager() throws Exception {
		return TransactionManagerServices.getTransactionManager();
	}
	
	public UserTransaction getUserTransaction() throws Exception {
		return TransactionManagerServices.getTransactionManager();
	}

}
