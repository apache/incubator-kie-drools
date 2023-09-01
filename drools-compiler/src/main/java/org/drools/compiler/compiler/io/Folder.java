package org.drools.compiler.compiler.io;

import java.util.Collection;

public interface Folder extends FileSystemItem {
    String getName();
    
    File getFile(String name);
    
    boolean exists();
    
    boolean create();
    
    Folder getFolder(String name);

    Folder getParent();
    
    Collection<? extends FileSystemItem> getMembers();
}
