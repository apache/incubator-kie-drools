/**
 * 
 */
package org.drools.transaction;

import org.drools.persistence.ByteArraySnapshotter;

public class MockByteArraySnapshotter<T> implements ByteArraySnapshotter<T> {
	
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

	public T getObject() {
		return null;
	}        
}