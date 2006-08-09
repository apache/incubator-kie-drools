package org.drools.runtime;

/**
 * Simple wrapper around resource data.
 * @author Michael Neale
 */
public class ResourceVersion {
    
    public long versionNumber;
    public byte[] data;
    public ResourceVersion(long versionNumber,
                           byte[] data) {
        super();
        this.versionNumber = versionNumber;
        this.data = data;
    }
    
    
     
}
