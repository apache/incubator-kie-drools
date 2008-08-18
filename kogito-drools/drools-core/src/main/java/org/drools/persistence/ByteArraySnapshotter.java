/**
 * 
 */
package org.drools.persistence;

public interface ByteArraySnapshotter {
    public byte[] getSnapshot();
    
    public void loadSnapshot(byte[] bytes);       
}