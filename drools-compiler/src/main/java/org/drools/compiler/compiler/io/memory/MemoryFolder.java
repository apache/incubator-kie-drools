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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.FileSystemItem;
import org.kie.memorycompiler.resources.KiePath;

public class MemoryFolder implements Folder, Serializable {

    private final MemoryFileSystem mfs;

    private final KiePath          path;
    
    private MemoryFolder     pFolder;

    public MemoryFolder(MemoryFileSystem mfs, String path) {
        this(mfs, KiePath.of(path) );
    }

    public MemoryFolder(MemoryFileSystem mfs, KiePath path) {
        this.mfs = mfs;
        this.path = path;
    }
    
    public String getName() {
        return path.getFileName();
    }

    public KiePath getPath() {
        return path;
    }

    public File getFile(String name) {
        return mfs.getFile( path.resolve(name) );
    }

    public Folder getFolder(String name) {
        return mfs.getFolder( path.resolve(name) );
    }

    public Folder getParent() {
        if ( pFolder == null ) {
            pFolder = new MemoryFolder( mfs, path.getParent() );
        }
        
        return pFolder;
    }
    
    public Collection<? extends FileSystemItem> getMembers() {
        Collection<? extends FileSystemItem> members = mfs.getMembers( this );
        return members != null ? members : Collections.emptyList();
    }

    public boolean exists() {
        return mfs.existsFolder( path );
    }

    public boolean create() {
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
        return Objects.equals(path, other.path);
    }

    @Override
    public String toString() {
        return "MemoryFolder [path=" + path.asString() + "]";
    }
}
