package org.drools.persistence.mapdb;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.ObjectHashMap;
import org.drools.persistence.PersistentSession;
import org.drools.persistence.processinstance.InternalWorkItemManager;
import org.kie.api.persistence.ObjectStoringStrategy;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.Serializer;

public class SessionIndexService {

	private DB db;
	private ObjectStoringStrategy[] strategies;

	public SessionIndexService(DB db, ObjectStoringStrategy[] strategies) {
		this.db = db;
		this.strategies = strategies;
	}

	public void update(MapDBSession session) {
		BTreeMap<Long, PersistentSession> map = db.treeMap(
				session.getMapKey(), 
				Serializer.LONG, 
				new PersistentSessionSerializer()).open();
		map.put(session.getId(), session);
		KieSession kruntime = session.getKieSession();
		if (kruntime != null) {
			for (EntryPoint entryPoint : kruntime.getEntryPoints()) {
				storeBeliefSet(entryPoint);
				storeFactHandles(entryPoint);
			}
			Set<WorkItem> workItems = ((InternalWorkItemManager) kruntime.getWorkItemManager()).getWorkItems();
			if (workItems != null) {
				for (WorkItem item : workItems) {
					storeWorkItemData(item);
				}
			}
		}
	}

	private void storeWorkItemData(WorkItem workItem) {
		Map<String, Object> parameters = workItem.getParameters();
		if (parameters != null) {
			for ( Map.Entry<String, Object> entry : parameters.entrySet() ) {
				strategyStore(entry.getValue());
			}
		}
		Map<String, Object> results = workItem.getResults();
		if (results != null) {
			for ( Map.Entry<String, Object> entry : results.entrySet() ) {
				strategyStore(entry.getValue());
			}
		}
	}

	private void storeFactHandles(EntryPoint entryPoint) {
		ObjectStore objectStore = ((NamedEntryPoint) entryPoint).getObjectStore();
        for ( Iterator<InternalFactHandle> it = objectStore.iterateFactHandles(); it.hasNext(); ) {
            InternalFactHandle handle = it.next();
            Object object = handle.getObject();
            if ( object != null ) {
            	strategyStore(object);
            }
        }

	}

	private void storeBeliefSet(EntryPoint entryPoint) {
		TruthMaintenanceSystem tms = ((NamedEntryPoint) entryPoint).getTruthMaintenanceSystem();
		ObjectHashMap justifiedMap = tms.getEqualityKeyMap();
		org.drools.core.util.Iterator<?> it = justifiedMap.iterator();
		for ( ObjectHashMap.ObjectEntry entry = (org.drools.core.util.ObjectHashMap.ObjectEntry) it.next(); entry != null; entry = (org.drools.core.util.ObjectHashMap.ObjectEntry) it.next() ) {
			EqualityKey key = (EqualityKey) entry.getKey();
			BeliefSet<?> beliefSet = key.getBeliefSet();
			if (beliefSet != null) {
				FastIterator it2 =  beliefSet.iterator();
			    for ( LinkedListEntry<?,?> node = (LinkedListEntry<?,?>) beliefSet.getFirst(); node != null; node = (LinkedListEntry<?,?>) it2.next(node) ) {
			        LogicalDependency<?> belief = (LogicalDependency<?>) node.getObject();
			        if ( belief.getObject() != null ) {
			        	strategyStore(belief.getMode());
			        }
			        if ( belief.getMode() != null ) {
			        	strategyStore(belief.getMode());
			        }
			    }
			}
		}
	}

	private void strategyStore(Object object) {
		if (strategies != null) {
			for (ObjectStoringStrategy strategy : strategies) {
				if (strategy.accept(object)) {
					strategy.persist(object);
					break;
				}
			}
		}
	}

}
