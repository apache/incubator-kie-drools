package org.drools.kproject.memory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.drools.core.util.StringUtils;
import org.drools.kproject.File;
import org.drools.kproject.Folder;
import org.drools.kproject.Path;

public class MemoryFile implements File {
    private String name;
    private Folder folder;
    private MemoryFileSystem mfs;

    public MemoryFile( MemoryFileSystem mfs, String name, Folder folder) {
        this.name = name;
        this.folder = folder;
        this.mfs = mfs;
    }
    
    public String getName() {
        return name;
    }         
    
    public InputStream getContents()  throws IOException {
        if ( !exists() ) {
            throw new IOException("File does not exist, unable to open InputStream" );
        }
        return new ByteArrayInputStream( mfs.getFileContents( this ) );
    }
    
    public Path getPath() {
        return getRelativePath();
    }            
    
    public Path getRelativePath() {
        if ( !StringUtils.isEmpty( folder.getPath().toPortableString() ) ) {
            return new MemoryPath( folder.getPath().toPortableString() + "/" + name );
        } else {
            return new MemoryPath( name );
        }
    }    
    
    public Folder getFolder() {
        return this.folder;
    }
    
    public boolean exists() {
        return mfs.existsFile( getRelativePath().toPortableString() );
    }        


    public void setContents(InputStream is) throws IOException {   
        if ( !exists() ) {
            throw new IOException( "File does not exists, cannot set contents" );
        }
        
        mfs.setFileContents( this, StringUtils.toString( is ).getBytes() );
    }

    public void create(InputStream is) throws IOException {
        if ( exists() ) {
            throw new IOException( "File does already exists, cannot create contents" );
        }
        
        mfs.setFileContents( this, StringUtils.toString( is ).getBytes() );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((folder == null) ? 0 : folder.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        MemoryFile other = (MemoryFile) obj;
        if ( folder == null ) {
            if ( other.folder != null ) return false;
        } else if ( !folder.equals( other.folder ) ) return false;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "MemoryFile [name=" + name + ", folder=" + folder + "]";
    }
    
}
