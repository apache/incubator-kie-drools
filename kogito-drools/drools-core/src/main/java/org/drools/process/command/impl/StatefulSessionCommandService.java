package org.drools.process.command.impl;

import javax.transaction.xa.XAException;

import org.drools.StatefulSession;
import org.drools.persistence.Persister;
import org.drools.persistence.Transaction;
import org.drools.persistence.memory.MemoryPersister;
import org.drools.persistence.session.StatefulSessionSnapshotter;
import org.drools.process.command.Command;
import org.drools.process.command.CommandService;

public class StatefulSessionCommandService implements CommandService {

	private StatefulSession session;
	
	public StatefulSessionCommandService(StatefulSession session) {
		this.session = session;
	}
	
	public Object execute(Command command) {
		Persister<StatefulSession> persister =
			new MemoryPersister<StatefulSession>(new StatefulSessionSnapshotter(session));
		persister.save();
		Transaction transaction = persister.getTransaction();
		try {
			transaction.start();
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
