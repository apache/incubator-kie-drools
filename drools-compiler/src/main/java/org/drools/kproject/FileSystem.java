package org.drools.kproject;


public interface FileSystem {
    Folder getProjectFolder();
    
    File getFile(Path path);
    File getFile(String path);
    
    Folder getFolder(Path path);
    Folder getFolder(String path);    
}
