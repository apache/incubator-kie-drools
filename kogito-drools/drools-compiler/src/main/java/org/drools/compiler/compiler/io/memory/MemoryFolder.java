/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.compiler.io.memory;

import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.Path;
import org.drools.compiler.compiler.io.Resource;
import org.drools.core.util.StringUtils;

import java.util.Collection;

public class MemoryFolder
        implements
        Folder {
    private MemoryFileSystem mfs;

    private String           path;
    
    private MemoryPath       mPath;
    
    private MemoryFolder     pFolder;

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
        if ( mPath == null ) {
            mPath = new MemoryPath( path );
        }
        return mPath;
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
        if ( pFolder == null ) {
            String p = trimLeadingAndTrailing( path );
            
            if ( p.indexOf( '/' ) == -1 ) {
                pFolder = new MemoryFolder( mfs,
                                         "" );            
            } else {           
                String[] elements = p.split( "/" );
        
                String newPath = "";
                boolean first = true;
                for ( int i = 0; i < elements.length - 1; i++ ) {
                    if ( !StringUtils.isEmpty( elements[i] ) ) {
                        if ( !first ) {
                            newPath = newPath + "/";
                        }
                        newPath = newPath + elements[i];
                        first = false;
                    }
                }
                pFolder = new MemoryFolder( mfs,
                                            newPath );
            }
        }
        
        return pFolder;
    }
    
    
    public static String trimLeadingAndTrailing(String p) {
        if (p.isEmpty()) {
            return p;
        }
        while ( p.charAt( 0 ) == '/') {
            p = p.substring( 1 );
        }

        while ( p.charAt( p.length() -1 ) == '/') {
            p = p.substring( 0, p.length() -1 );
        } 
        
        return p;
    }

    public Collection<? extends Resource> getMembers() {
        return mfs.getMembers( this );
    }

    public boolean exists() {
        return mfs.existsFolder( path );
    }

    public boolean create() {
        path = trimLeadingAndTrailing( path );
        mfs.createFolder( this );
        return true;
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
