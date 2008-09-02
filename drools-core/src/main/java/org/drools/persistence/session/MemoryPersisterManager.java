package org.drools.persistence.session;

import java.util.HashMap;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.persistence.Persister;
import org.drools.persistence.memory.HashMapMemoryObject;
import org.drools.persistence.memory.MemoryPersister;

public class MemoryPersisterManager {

	private HashMapMemoryObject map = new HashMapMemoryObject(new HashMap<String, byte[]>());
	
	public Persister<StatefulSession> getSessionPersister(StatefulSession session) {
		return new MemoryPersister<StatefulSession>(new StatefulSessionSnapshotter(session), map);
	}
	
	public Persister<StatefulSession> getSessionPersister(String uniqueId, RuleBase ruleBase) {
		Persister<StatefulSession> persister = new MemoryPersister<StatefulSession>(
			new StatefulSessionSnapshotter(ruleBase), map);
		persister.setUniqueId(uniqueId);
		persister.load();
		return persister;
	}
	
}
