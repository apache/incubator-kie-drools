package org.drools.persistence.memory;

import org.drools.persistence.ByteArraySnapshotter;
import org.drools.persistence.PersistenceManager;
import org.drools.persistence.Transaction;

public class MemoryPersistenceManager implements ByteArraySnapshotter, PersistenceManager {  
    ByteArraySnapshotter snapshotter;
    byte[] lastSave;  
    MemoryXaResource xaResource;
	
	public MemoryPersistenceManager(ByteArraySnapshotter snapshotter) {
		this.snapshotter = snapshotter;
	}
	
	public MemoryXaResource getXAResource() {
		if ( xaResource == null ) {
			xaResource = new MemoryXaResource( this );
		}
		return xaResource;
	}
	
	public Transaction getTransaction() {
		return new Transaction( null, getXAResource() );
	}
	
	public void save() {
		if ( xaResource != null && xaResource.isInTransaction() ) {
			throw new RuntimeException("You cannot call a persistence save point while a transaction is open" );
		}
		lastSave = getSnapshot();
	}

	public void load() {
		if ( xaResource != null && xaResource.isInTransaction() ) {
			throw new RuntimeException("You cannot call a persistence save point while a transaction is open" );
		}		
		loadSnapshot( lastSave );
	}
	
	public boolean isInTransaction() {
		return false;
	}
	
	public void setLastSave(byte[] lastSave) {
		this.lastSave = lastSave;
	}
	
	public byte[] getLastSave() {
		return lastSave;
	}
	
    public byte[] getSnapshot() {
    	return snapshotter.getSnapshot();
    }
    
    public void loadSnapshot(byte[] bytes) {
    	this.snapshotter.loadSnapshot( bytes );
    }
}
