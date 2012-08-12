package org.drools.kproject.memory;

import java.util.Collection;

import org.drools.core.util.StringUtils;
import org.drools.kproject.File;
import org.drools.kproject.Folder;
import org.drools.kproject.Path;
import org.drools.kproject.Resource;

public class MemoryFolder
        implements
        Folder {
    private MemoryFileSystem mfs;

    private String           path;

    public MemoryFolder(MemoryFileSystem mfs,
                        String path) {
        this.mfs = mfs;
        this.path = path;
    }
    
    public String getName() {
        int lastSlash = path.lastIndexOf( '/' );
        if ( lastSlash >= 0 ) {
            return path.substring( lastSlash+1 );
        } else {
            return path;
        }
    }

    public Path getPath() {
        return new MemoryPath( path );
    }

    public File getFile(String name) {
        if ( !StringUtils.isEmpty( path )) {
            return mfs.getFile( path + "/" + name );
        } else {
            return mfs.getFile( name );
        }
    }

    public Folder getFolder(String name) {
        if ( !StringUtils.isEmpty( path )) {
            return mfs.getFolder( path + "/" + name );
        } else {
            return mfs.getFolder( name );
        }
    }

    public Folder getParent() {
        String[] elements = path.split( "/" );
        if ( elements.length == 0 ) {
            // we are at root
            return this;
        }
        
        String newPath = "";
        boolean first = true;
        for ( int i = 0; i < elements.length - 1; i++ ) {
            if ( !StringUtils.isEmpty( elements[i] ) ) {
                if ( !first ) {
                    newPath = newPath + "/";;
                }
                newPath = newPath + elements[i];
                first = false;
            }
        }
        
        if ( StringUtils.isEmpty( newPath ) ) {
            // we are at root
            newPath = "";
        }
        return new MemoryFolder( mfs,
                                 newPath );
    }
    

    public Collection<? extends Resource> getMembers() {
        return mfs.getMembers( this );
    }    

    public boolean exists() {
        return mfs.existsFolder( path );
    }

    public boolean create() {
        if ( !exists() ) {
            createFolder( this );
        }
        return true;

    }

    private void createFolder(MemoryFolder folder) {
        if ( !folder.exists() ) {
            createFolder( ( MemoryFolder ) folder.getParent() );
            mfs.createFolder( folder );
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        MemoryFolder other = (MemoryFolder) obj;
        if ( path == null ) {
            if ( other.path != null ) return false;
        } else if ( !path.equals( other.path ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "MemoryFolder [path=" + path + "]";
    }
    
}
