package org.drools.transaction;


import junit.framework.TestCase;

public class TransactionManagerTest extends TestCase {
    
    public void test1() {
        MockByteArrayAccessor accessor = new MockByteArrayAccessor();
        //DefaultTransactionManager tm = new DefaultTransactionManager( accessor );
        
        accessor.bytes = new byte[] { 0, 1, 0, 1 };
        
        //tm.start();
    }

    public static class MockByteArrayAccessor implements ByteArraySnapshotter {
        public byte[] bytes;
        

        public byte[] getSnapshot() {
            return this.bytes;
        }

        public void restoreSnapshot(byte[] bytes) {
            this.bytes = bytes;
        }
        
    }
}
