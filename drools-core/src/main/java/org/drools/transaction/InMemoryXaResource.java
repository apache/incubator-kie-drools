package org.drools.transaction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * crude nested transaction manager, not written for performance
 * @author mproctor
 *
 */
public class InMemoryXaResource implements XAResource {
    private ByteArraySnapshotter byteArrayAccessorDelegate;
    
    //Map<Xid, Transaction> xidMapping = new HashMap<Xid, Transaction>();
    Map<Xid, byte[]> data = new HashMap<Xid, byte[]>();
    LinkedList<Xid> list = new LinkedList<Xid>();
    
    byte[] lastSave;        

    public InMemoryXaResource(ByteArraySnapshotter byteArrayAccessorDelegate) {
        this.byteArrayAccessorDelegate = byteArrayAccessorDelegate;
    }

    public void start(Xid xid,
                      int flags) throws XAException {
        byte[] bytes = byteArrayAccessorDelegate.getSnapshot();
        // The start of the first transaction is recorded as  save point, for HA.
        if ( this.list.isEmpty() ) {
            this.lastSave = bytes;
        }        
        
        this.list.add( xid );
        this.data.put( xid, bytes );
    }     
    
    public void rollback(Xid xid) throws XAException {   
        if ( this.list.isEmpty() ) {
            // nothing to rollback
            return;
        }
        byte[] bytes = this.data.get( xid );
        
        boolean remove = false;
        for(java.util.Iterator<Xid> it = this.list.iterator(); it.hasNext(); ) {
            Xid currentXid = it.next();
            if ( !remove && currentXid.equals( xid )) {
                remove = true;
            } 
            
            if ( remove ) {
                this.data.remove( currentXid );
                it.remove();
            }                        
        }
                        
        this.byteArrayAccessorDelegate.restoreSnapshot( bytes );
    }    
    
    public void commit(Xid xid,
                       boolean onePhase) throws XAException {
        if ( this.list.getFirst().equals( xid ) && this.list.size() > 1 ) {
            // first one has committed, so move save point to next transaction
            this.lastSave = this.data.get( this.list.get( 1 ) );                   
        } else if ( this.list.size() == 1 ) {
            // there will be no more transactions after this is removed, so create a new lastSave point
            this.lastSave = byteArrayAccessorDelegate.getSnapshot();
        }
        
        this.list.remove( xid );
        this.data.remove( xid );        
    }
    
    public void end(Xid xid,
                    int flags) throws XAException {    
    }        

    public void forget(Xid xid) throws XAException {        
    }

    public int getTransactionTimeout() throws XAException {
        return 0;
    }

    public boolean isSameRM(XAResource xares) throws XAException {
        return false;
    }

    public int prepare(Xid xid) throws XAException {
        return 0;
    }

    public Xid[] recover(int flag) throws XAException {
        return null;
    }

    public boolean setTransactionTimeout(int seconds) throws XAException {
        return false;
    }      

}
