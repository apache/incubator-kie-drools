package org.drools.compiler.compiler.io;


import org.drools.util.PortablePath;

public interface FileSystem {
    Folder getRootFolder();
    
    File getFile(PortablePath path);
    default File getFile(String name) {
        return getFile(PortablePath.of(name));
    }

    Folder getFolder(PortablePath path);
    default Folder getFolder(String name) {
        return getFolder(PortablePath.of(name));
    }
    
    boolean remove(File file);
    
    boolean remove(Folder folder);
}
