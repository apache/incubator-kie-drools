package org.drools.kproject.memory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.commons.jci.readers.ResourceReader;
import org.drools.commons.jci.stores.ResourceStore;
import org.drools.kproject.File;
import org.drools.kproject.FileSystem;
import org.drools.kproject.Folder;
import org.drools.kproject.Path;
import org.drools.kproject.Resource;

public class MemoryFileSystem implements FileSystem, ResourceReader, ResourceStore {
    
    private MemoryFolder folder;
    
    private Map<String, Set<Resource>> folders;
    
    private Map<String, byte[]> fileContents;
    
    public MemoryFileSystem() {        
        folders = new HashMap<String, Set<Resource>>();
        fileContents = new HashMap<String, byte[]>();
        
        folder = new MemoryFolder( this, "" );
        folders.put( "", new HashSet<Resource>() );     
    }

    public Folder getProjectFolder() {
        return folder;
    }
    
    public File getFile(Path path) {
        //return new MemoryFile( project.getFile( ((MemoryPath)path ).getRawPath() ) );
        return null;
    }
    
    public File getFile(String path) {
        int lastSlashPos = path.lastIndexOf( '/' );
        if ( lastSlashPos >=0 ) {
            Folder folder = getFolder( path.substring( 0, lastSlashPos ) );
            String name = path.substring( lastSlashPos + 1 );
            return new MemoryFile( this, name, folder );
        } else {
            // path is already at root
            Folder folder = getProjectFolder();
            return new MemoryFile( this, path, folder ); 
        }
        
    }    
    
    public Folder getFolder(Path path) {
        return new MemoryFolder( this, path.toPortableString() );
    }

    public Folder getFolder(String path) {
        return new MemoryFolder( this, path );
    }
    
    public Set<? extends Resource> getMembers(Folder folder) {
        return folders.get( folder.getPath().toPortableString() );
    }
    
    public byte[] getFileContents(MemoryFile file) {
        return fileContents.get( file.getPath().toPortableString() );
    }
    
    public void setFileContents(MemoryFile file, byte[] contents) throws IOException {
        if ( !existsFolder( file.getFolder().getPath().toPortableString() )) {
            throw new IOException( "Folder  does not exist, cannot write contents" );    
        }
        
        fileContents.put( file.getPath().toPortableString(), contents );
        
        folders.get( file.getFolder().getPath().toPortableString() ).add( file );
        
    }
    
    public boolean existsFolder(String path) {
        return folders.get( path ) != null;
    }
    
    public boolean existsFile(String path) {
        return fileContents.containsKey( path );
    }    
    
    public void createFolder(MemoryFolder folder) {
        if ( existsFolder( folder.getParent().getPath().toPortableString() ) && 
                !existsFolder( folder.getPath().toPortableString() ) ) {
            folders.put( folder.getPath().toPortableString(), new HashSet<Resource>() );
            
            Folder parent = folder.getParent();
            folders.get( parent.getPath().toPortableString() ).add( folder );
            
            
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fileContents == null) ? 0 : fileContents.hashCode());
        result = prime * result + ((folder == null) ? 0 : folder.hashCode());
        result = prime * result + ((folders == null) ? 0 : folders.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        MemoryFileSystem other = (MemoryFileSystem) obj;
        if ( fileContents == null ) {
            if ( other.fileContents != null ) return false;
        } else if ( !fileContents.equals( other.fileContents ) ) return false;
        if ( folder == null ) {
            if ( other.folder != null ) return false;
        } else if ( !folder.equals( other.folder ) ) return false;
        if ( folders == null ) {
            if ( other.folders != null ) return false;
        } else if ( !folders.equals( other.folders ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "MemoryFileSystem [folder=" + folder + ", folders=" + folders + ", fileContents=" + fileContents + "]";
    }

    public boolean isAvailable(String pResourceName) {
        return existsFile( pResourceName );
    }

    public byte[] getBytes(String pResourceName) {
        return getFileContents( ( MemoryFile ) getFile( pResourceName ) );
    }

    public void write(String pResourceName,
                      byte[] pResourceData) {
        try {
            setFileContents( ( MemoryFile ) getFile( pResourceName ), pResourceData );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        
    }

    public byte[] read(String pResourceName) {
        return getBytes(pResourceName);
    }

    public void remove(String pResourceName) {
        throw new UnsupportedOperationException();
    }        
}
