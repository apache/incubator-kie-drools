package org.drools.semantics.java;

import org.apache.commons.jci.stores.ResourceStore;
import org.drools.rule.PackageCompilationData;

public class PackageStore
    implements
    ResourceStore {
    private PackageCompilationData packageCompilationData;

    public PackageStore() {
    }
    
    public PackageStore(PackageCompilationData packageCompiationData) {
        this.packageCompilationData = packageCompiationData;
    }
    
    public void setPackageCompilationData(PackageCompilationData packageCompiationData) {
        this.packageCompilationData = packageCompiationData;
    }

    public void write(String resourceName,
                      byte[] clazzData) {
        try {
            this.packageCompilationData.write( resourceName,
                                               clazzData );
        } catch ( Exception e ) {

        }
    }

    public byte[] read(String resourceName) {
        byte[] clazz = null;
        try {
            clazz = this.packageCompilationData.read( resourceName );
        } catch ( Exception e ) {

        }
        return clazz;
    }

    public void remove(String resourceName) {
        try {
            this.packageCompilationData.remove( resourceName );
        } catch ( Exception e ) {

        }
    }
}
