package org.drools.persistence.memory;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import junit.framework.TestCase;

import org.drools.persistence.DroolsXid;
import org.drools.transaction.MockByteArraySnapshotter;

public class MemoryXaResourceTest extends TestCase {
    private byte[]           data1 = new byte[]{1, 1, 1, 1, 1};
    private byte[]           data2 = new byte[]{1, 1, 1, 1, 0};
    private byte[]           data3 = new byte[]{1, 1, 1, 0, 0};

    MockByteArraySnapshotter snapshotter;
    MemoryPersister pm;

    protected void setUp() throws Exception {
        snapshotter = new MockByteArraySnapshotter();
        pm = new MemoryPersister( snapshotter );
    }

    public void testInitFields() {
        MemoryXaResource xa = pm.getXAResource();

        // make sure these are initialised correctly
        assertEquals( 0,
                      xa.list.size() );
        assertNull( pm.lastSave.getData( null ) );
    }

    public void testSingleTransactionWithRollBack() throws Exception {
        MemoryXaResource xa = pm.getXAResource();

        Xid xid = new DroolsXid( 100,
                                 new byte[]{0x01},
                                 new byte[]{0x01} );

        snapshotter.bytes = data1;
        xa.start( xid,
                  XAResource.TMNOFLAGS );
        assertEquals( 1,
                      xa.list.size() ); // we only have one transaction
        assertSame( xa.data.get( xa.list.get( 0 ) ),
                    pm.lastSave.getData(null) ); // lastSave is always set to begin of the first transaction

        snapshotter.bytes = data2;
        xa.rollback( xid );

        assertTrue( assertEquals( data1,
                                  snapshotter.bytes ) );
        assertTrue( xa.list.isEmpty() );
        assertTrue( xa.data.isEmpty() );
    }

    public void testSingleTransactionWithCommit() throws Exception {
        MemoryXaResource xa = pm.getXAResource();

        Xid xid = new DroolsXid( 100,
                                 new byte[]{0x01},
                                 new byte[]{0x01} );

        snapshotter.bytes = data1;
        xa.start( xid,
                  XAResource.TMNOFLAGS );

        snapshotter.bytes = data2;
        xa.commit( xid,
                   true );

        // check levels are empty and that lastsave was updated to be the same as end
        assertEquals( 0,
                      xa.list.size() );
        assertSame( data2,
                    pm.lastSave.getData(null) );

        // should do nothing as there is nothing to rollback
        xa.rollback( xid );

        assertTrue( assertEquals( data2,
                                  snapshotter.bytes ) );
        assertTrue( xa.list.isEmpty() );
        assertTrue( xa.data.isEmpty() );
    }

    public void testMultipleTransactions() throws Exception {
        MemoryXaResource xa = pm.getXAResource();

        Xid xid1 = new DroolsXid( 100,
                                  new byte[]{0x01},
                                  new byte[]{0x01} );

        snapshotter.bytes = data1;
        xa.start( xid1,
                  XAResource.TMNOFLAGS );

        Xid xid2 = new DroolsXid( 100,
                                  new byte[]{0x02},
                                  new byte[]{0x02} );
        snapshotter.bytes = data2;
        xa.start( xid2,
                  XAResource.TMNOFLAGS );
        assertEquals( 2,
                      xa.list.size() ); // we now have two levels
        assertSame( xa.data.get( xa.list.get( 0 ) ),
                    pm.lastSave.getData(null) ); // check lastSave is still first transaction

        Xid xid3 = new DroolsXid( 100,
                                  new byte[]{0x03},
                                  new byte[]{0x03} );
        snapshotter.bytes = data3;
        xa.start( xid3,
                  XAResource.TMNOFLAGS );
        assertEquals( 3,
                      xa.list.size() ); // we now have three levels.

        // commit the first, so the second should become the lastSave point
        xa.commit( xid1,
                   true );

        assertSame( xa.data.get( xid2 ),
                    pm.lastSave.getData(null) ); // xid2 should now be the lastSave point

        // rollback xid2, should result in rolling back xid3 too
        xa.rollback( xid2 );

        assertTrue( xa.list.isEmpty() );
        assertTrue( xa.data.isEmpty() );
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
