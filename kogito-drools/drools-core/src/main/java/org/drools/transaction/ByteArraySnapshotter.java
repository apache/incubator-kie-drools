/**
 * 
 */
package org.drools.transaction;

public interface ByteArraySnapshotter {
    public byte[] getSnapshot();
    
    public void restoreSnapshot(byte[] bytes);       
}