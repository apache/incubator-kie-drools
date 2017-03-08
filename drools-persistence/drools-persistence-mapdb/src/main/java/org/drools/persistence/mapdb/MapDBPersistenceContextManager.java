package org.drools.persistence.mapdb;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.PersistenceContextManager;
import org.drools.persistence.TransactionManager;
import org.kie.api.persistence.ObjectStoringStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.mapdb.DB;

public class MapDBPersistenceContextManager implements
		PersistenceContextManager {

	private Environment env;
	private DB db;
	private ObjectStoringStrategy[] strategies;
	private PersistenceContext cmdPersistenceContext;
	private PersistenceContext appPersistenceContext;
	private TransactionManager txm;

	public MapDBPersistenceContextManager(Environment env) {
		this.env = env;
		this.db = (DB) env.get(MapDBEnvironmentName.DB_OBJECT);
		this.txm = (TransactionManager) env.get(EnvironmentName.TRANSACTION_MANAGER);
		this.strategies = (ObjectStoringStrategy[]) env.get(MapDBEnvironmentName.OBJECT_STORING_STRATEGIES);
	}
	
	@Override
	public PersistenceContext getApplicationScopedPersistenceContext() {
		if (this.appPersistenceContext == null) {
			this.appPersistenceContext = new MapDBPersistenceContext(db, txm, strategies);
			this.env.set( EnvironmentName.APP_SCOPED_ENTITY_MANAGER, this.appPersistenceContext );
		}
		return this.appPersistenceContext;
	}

	@Override
	public PersistenceContext getCommandScopedPersistenceContext() {
		if (this.cmdPersistenceContext == null) {
			beginCommandScopedEntityManager();
		}
		return this.cmdPersistenceContext;
	}

	@Override
	public void beginCommandScopedEntityManager() {
		this.cmdPersistenceContext = new MapDBPersistenceContext(db, txm, strategies);
		this.env.set( EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, this.cmdPersistenceContext );
	}

	@Override
	public void endCommandScopedEntityManager() {
		if (this.cmdPersistenceContext != null) {
			this.cmdPersistenceContext.close();
			this.cmdPersistenceContext = null;
			this.env.set(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, null);
		}
	}

	@Override
	public void dispose() {
		if (this.appPersistenceContext != null) {
			this.appPersistenceContext.close();
			this.appPersistenceContext = null;
			this.env.set(EnvironmentName.APP_SCOPED_ENTITY_MANAGER, null);
		}
	}

	protected DB getDB() {
		return this.db;
	}
	
	protected TransactionManager getTXM() {
		return this.txm;
	}
	
	protected ObjectStoringStrategy[] getStrategies() {
		return strategies;
	}
}
