package org.drools.persistence.memory;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import junit.framework.TestCase;

import org.drools.persistence.DroolsXid;
import org.drools.transaction.MockByteArraySnapshotter;

public class MemoryPersistenceManagerTest extends TestCase {
    private byte[]           data1 = new byte[]{1, 1, 1, 1, 1};
    private byte[]           data2 = new byte[]{1, 1, 1, 1, 0};
    private byte[]           data3 = new byte[]{1, 1, 1, 0, 0};

    MockByteArraySnapshotter snapshotter;
    MemoryPersistenceManager pm;

    protected void setUp() throws Exception {
        snapshotter = new MockByteArraySnapshotter();
        pm = new MemoryPersistenceManager( snapshotter );
    }

    public void testSave() {
        snapshotter.loadSnapshot( data1 );
        pm.save();
        assertTrue( assertEquals( data1,
                                  snapshotter.bytes ) );

        snapshotter.loadSnapshot( data2 );
        assertTrue( assertEquals( data2,
                                  snapshotter.bytes ) );

        pm.load();
        assertTrue( assertEquals( data1,
                                  snapshotter.bytes ) );
    }

    public void testSaveInOpenTransaction() throws XAException {
        snapshotter.loadSnapshot( data1 );

        XAResource xa = pm.getXAResource();
        Xid xid = new DroolsXid( 100,
                                 new byte[]{0x01},
                                 new byte[]{0x01} );
        xa.start( xid,
                  XAResource.TMNOFLAGS );

        try {
            pm.save();
            fail( "save should fail as the session currently has an open transaction" );
        } catch ( Exception e ) {
            // success
        }
    }

    public void testLoadInOpenTransaction() throws XAException {
        snapshotter.loadSnapshot( data1 );
        pm.save();

        XAResource xa = pm.getXAResource();
        Xid xid = new DroolsXid( 100,
                                 new byte[]{0x01},
                                 new byte[]{0x01} );
        xa.start( xid,
                  XAResource.TMNOFLAGS );

        try {
            pm.load();
            fail( "load should fail as the session currently has an open transaction" );
        } catch ( Exception e ) {
            // success
        }
    }

    public void testLoadSaveAfterTransaction() throws Exception {
        snapshotter.loadSnapshot( data1 );
        XAResource xa = pm.getXAResource();
        Xid xid = new DroolsXid( 100,
                                 new byte[]{0x01},
                                 new byte[]{0x01} );
        xa.start( xid,
                  XAResource.TMNOFLAGS );

        snapshotter.loadSnapshot( data2 );
        xa.commit( xid,
                   true );

        pm.save();
        assertTrue( assertEquals( data2,
                                  snapshotter.bytes ) );

        snapshotter.loadSnapshot( data3 );
        assertTrue( assertEquals( data3,
                                  snapshotter.bytes ) );

        pm.load();
        assertTrue( assertEquals( data2,
                                  snapshotter.bytes ) );
    }

    public boolean assertEquals(byte[] bytes1,
                                byte[] bytes2) {
        if ( bytes1.length != bytes2.length ) {
            return false;
        }

        for ( int i = 0; i < bytes1.length; i++ ) {
            if ( bytes1[i] != bytes2[i] ) {
                return false;
            }
        }

        return true;
    }
}
