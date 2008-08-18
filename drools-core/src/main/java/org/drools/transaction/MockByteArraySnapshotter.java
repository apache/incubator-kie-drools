/**
 * 
 */
package org.drools.transaction;

public class MockByteArraySnapshotter implements ByteArraySnapshotter {
    public byte[] bytes;

    public MockByteArraySnapshotter() {
        
    }
    
    public MockByteArraySnapshotter(byte[] bytes) {
        this.bytes = bytes;
    }
    
    public byte[] getSnapshot() {
        return bytes;
    }

    public void restoreSnapshot(byte[] bytes) {
        this.bytes = bytes;
    }        
}