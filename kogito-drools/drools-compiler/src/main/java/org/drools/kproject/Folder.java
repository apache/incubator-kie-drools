package org.drools.kproject;

import java.util.Collection;

public interface Folder extends Resource {
    String getName();
    
    File getFile(String name);
    
    boolean exists();
    
    boolean create();
    
    Folder getFolder(String name);
    
    Path getPath();
    
    Folder getParent();
    
    Collection<? extends Resource> getMembers();
}
