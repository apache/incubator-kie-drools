/*
 * Copyright 2015 JBoss Inc
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
import org.drools.core.util.IoUtils;
import org.drools.core.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;

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
        
        mfs.setFileContents( this, StringUtils.toString( is ).getBytes( IoUtils.UTF8_CHARSET ) );
    }

    public void create(InputStream is) throws IOException {
        mfs.setFileContents( this, readBytesFromInputStream(is) );
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
