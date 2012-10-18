package org.drools.kproject;


public interface FileSystem {
    Folder getRootFolder();
    
    File getFile(Path path);
    File getFile(String path);
    
    Folder getFolder(Path path);
    Folder getFolder(String path);    
    
    boolean remove(File file);
    
    boolean remove(Folder folder);
}
