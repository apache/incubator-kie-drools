/**
 * 
 */
package org.drools.persistence;

public interface ByteArraySnapshotter<T> {
	
    byte[] getSnapshot();

    void loadSnapshot(byte[] bytes);
    
    T getObject();
    
}