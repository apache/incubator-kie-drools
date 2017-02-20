package org.drools.persistence.mapdb;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.PersistentSession;
import org.drools.persistence.PersistentWorkItem;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionManagerHelper;
import org.drools.persistence.processinstance.mapdb.MapDBWorkItem;
import org.kie.api.persistence.ObjectStoringStrategy;
import org.mapdb.Atomic;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.serializer.GroupSerializer;
import org.mapdb.serializer.SerializerLong;

public class MapDBPersistenceContext implements PersistenceContext {

	protected DB db;
	protected TransactionManager txm;
	protected ObjectStoringStrategy[] strategies;
	private GroupSerializer<PersistentSession> sessionSerializer = new PersistentSessionSerializer();
	private GroupSerializer<Long> idSerializer = new SerializerLong();
	private GroupSerializer<PersistentWorkItem> workItemSerializer = new PersistentWorkItemSerializer();
	private BTreeMap<Long, PersistentWorkItem> workItemMap;
	private BTreeMap<Long, PersistentSession> sessionMap;
	private Atomic.Long nextSessionId;
	private Atomic.Long nextWorkItemId;

	public MapDBPersistenceContext(DB db, TransactionManager txm, ObjectStoringStrategy[] strategies) {
		this.db = db;
		this.txm = txm;
		this.strategies = strategies;
		this.workItemMap = db.treeMap(new MapDBWorkItem().getMapKey(), idSerializer, workItemSerializer).createOrOpen();
		this.sessionMap = db.treeMap(new MapDBSession().getMapKey(), idSerializer, sessionSerializer).createOrOpen();
		this.nextSessionId = db.atomicLong("sessionId").createOrOpen();
		this.nextWorkItemId = db.atomicLong("workItemId").createOrOpen();
    }
	
	@Override
	public PersistentSession persist(PersistentSession session) {
		long id;
		if (session.getId() == null || session.getId() == -1) {
			id = nextSessionId.incrementAndGet();
			session.setId(id);
		} else {
			id = session.getId();
		}
		TransactionManagerHelper.addToUpdatableSet(txm, session);
		sessionMap.put(id, session); //to be placed in map by triggerupdatesync
		return session;
	}

	@Override
	public PersistentSession findSession(Long id) {
		PersistentSession session = sessionMap.getOrDefault(id, null);
		if (session != null) {
			TransactionManagerHelper.addToUpdatableSet(txm, session);
		}
		return session;
	}

	@Override
	public void remove(PersistentSession session) {
		sessionMap.remove(session.getId());
	}

	@Override
	public boolean isOpen() {
		return !db.getStore().isClosed();
	}

	@Override
	public void joinTransaction() {
	}

	@Override
	public void close() {
		//db.getStore().close();
	}

	@Override
	public PersistentWorkItem persist(PersistentWorkItem workItem) {
		long id;
		if (workItem.getId() == null || workItem.getId() == -1) {
			id = nextWorkItemId.incrementAndGet();
			workItem.setId(id);
		} else {
			id = workItem.getId();
		}
		TransactionManagerHelper.addToUpdatableSet(txm, workItem);
		workItemMap.put(id, workItem);
		return workItem;
	}

	@Override
	public PersistentWorkItem findWorkItem(Long id) {
		PersistentWorkItem workItem = workItemMap.getOrDefault(id, null);
		if (workItem != null) {
			TransactionManagerHelper.addToUpdatableSet(txm, workItem);
		}
		return workItem;
	}

	@Override
	public void remove(PersistentWorkItem workItem) {
		workItemMap.remove(workItem.getId());
	}

	@Override
	public void lock(PersistentWorkItem workItem) {
	}

	@Override
	public PersistentWorkItem merge(PersistentWorkItem workItem) {
		workItemMap.put(workItem.getId(), workItem);
		TransactionManagerHelper.addToUpdatableSet(txm, workItem);
		return workItem;
	}
}
