package org.drools.process.command.impl;

import javax.transaction.xa.XAException;

import org.drools.StatefulSession;
import org.drools.persistence.PersistenceManager;
import org.drools.persistence.StatefulSessionSnapshotter;
import org.drools.persistence.Transaction;
import org.drools.persistence.memory.MemoryPersistenceManager;
import org.drools.process.command.Command;
import org.drools.process.command.CommandService;

public class StatefulSessionCommandService implements CommandService {

	private StatefulSession session;
	
	public StatefulSessionCommandService(StatefulSession session) {
		this.session = session;
	}
	
	public Object execute(Command command) {
		PersistenceManager persistenceManager =
			new MemoryPersistenceManager(new StatefulSessionSnapshotter(session));
		persistenceManager.save();
		Transaction transaction = persistenceManager.getTransaction();
		try {
			Object result = command.execute(session);
			transaction.commit();
			return result;
		} catch (Throwable t) {
			try {
				transaction.rollback();
				throw new RuntimeException("Could not execute command", t);
			} catch (XAException e) {
				throw new RuntimeException("Could not rollback transaction", e);
			}
		}
	}

}
