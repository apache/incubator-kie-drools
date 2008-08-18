/**
 * 
 */
package org.drools.transaction;

import org.drools.persistence.ByteArraySnapshotter;

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

    public void loadSnapshot(byte[] bytes) {
        this.bytes = bytes;
    }        
}