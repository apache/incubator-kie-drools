package org.drools.persistence.mapdb;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.mapdb.DB;

public class MapDBUserTransaction implements UserTransaction {

	private final DB db;

	public MapDBUserTransaction(DB db) {
		this.db = db;
	}

	@Override
	public void begin() throws NotSupportedException, SystemException {
		findUserTransaction().begin();
	}

	@Override
	public void commit() throws RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SecurityException,
			IllegalStateException, SystemException {
		db.commit();
		findUserTransaction().commit();
	}

	@Override
	public int getStatus() throws SystemException {
		return findUserTransaction().getStatus();
	}

	@Override
	public void rollback() throws IllegalStateException, SecurityException,
			SystemException {
		db.rollback();
		findUserTransaction().rollback();
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		findUserTransaction().setRollbackOnly();
	}

	@Override
	public void setTransactionTimeout(int timeout) throws SystemException {
		findUserTransaction().setTransactionTimeout(timeout);
	}

	protected UserTransaction findUserTransaction() throws SystemException {
		try {
			return (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
		} catch (NamingException e) {
			throw new SystemException("Invalid JNDI name: " + e.getMessage());
		}
	}
}
