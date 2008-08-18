package org.drools.persistence;

import javax.transaction.xa.XAResource;


public interface PersistenceManager {

	XAResource getXAResource();

	Transaction getTransaction();

	void save();

	void load();

}