package org.drools.persistence;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class Transaction {
    private Xid xid;

    XAResource  xaResource;

    public Transaction(Xid xid,
                       XAResource xaResource) {
        this.xid = xid;
        this.xaResource = xaResource;
    }

    public Xid getXid() {
        return xid;
    }

    public void start() throws XAException {
        this.xaResource.start( xid,
                               XAResource.TMNOFLAGS );
    }

    public void rollback() throws XAException {
        this.xaResource.rollback( xid );
    }

    public void commit() throws XAException {
        this.xaResource.commit( xid,
                                true );
    }

    public int hashCode() {
        return xid.hashCode();
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Transaction other = (Transaction) obj;
        if ( xid == null ) {
            if ( other.xid != null ) return false;
        } else if ( !xid.equals( other.xid ) ) return false;
        return true;
    }

}
