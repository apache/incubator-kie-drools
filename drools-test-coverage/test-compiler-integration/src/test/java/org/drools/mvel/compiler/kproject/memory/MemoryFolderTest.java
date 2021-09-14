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

package org.drools.mvel.compiler.kproject.memory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.FileSystem;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.compiler.io.memory.MemoryFolder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MemoryFolderTest {
    
    @Test
    public void testGetParentWithLeadingAndTrailingSlash() {
        MemoryFileSystem mfs = new MemoryFileSystem();
        assertEquals( "", new MemoryFolder( mfs, "/src" ).getParent().getPath().asString() );

        assertEquals( "", new MemoryFolder( mfs, "src/" ).getParent().getPath().asString() );

        assertEquals( "", new MemoryFolder( mfs, "/src/" ).getParent().getPath().asString() );

        assertEquals( "/src", new MemoryFolder( mfs, "/src/main" ).getParent().getPath().asString() );

        assertEquals( "src", new MemoryFolder( mfs, "src/main/" ).getParent().getPath().asString() );

        assertEquals( "/src", new MemoryFolder( mfs, "/src/main/" ).getParent().getPath().asString() );

        assertEquals( "/src/main", new MemoryFolder( mfs, "/src/main/java" ).getParent().getPath().asString() );

        assertEquals( "src/main", new MemoryFolder( mfs, "src/main/java/" ).getParent().getPath().asString() );

        assertEquals( "/src/main", new MemoryFolder( mfs, "/src/main/java/" ).getParent().getPath().asString() );
    }
    
    
    @Test
    public void testRecursiveFolderCreation() {
        FileSystem fs = new MemoryFileSystem();
        
        Folder mres = fs.getFolder( "src/main/resources" );
        assertFalse( mres.exists() );
        mres.create();
        assertTrue( mres.exists() );
        
        Folder fld = fs.getFolder( "src/main" );
        assertTrue( fld.exists() );  
        
        Folder src = fs.getFolder( "src" );
        assertTrue( src.exists() );         
    }
    
    @Test
    public void testFolderGetParent() {
        FileSystem fs = new MemoryFileSystem();
        
        Folder mres = fs.getFolder( "src/main/resources" );
        mres.create();
        
        assertEquals( "src/main", mres.getParent().getPath().asString() );
        
        assertEquals( "src", mres.getParent().getParent().getPath().asString() );
        
    }    
    

    @Test
    public void testFolderRemoval() throws IOException {
        FileSystem fs = new MemoryFileSystem();
        
        Folder fld = fs.getFolder( "src/main/resources/org/domain" );
        fld.create();
        
        fld = fs.getFolder( "src/main" );
        File file = fld.getFile( "MyClass1.java" );
        file.create( new ByteArrayInputStream( "ABC1".getBytes() ) );  
        file = fld.getFile( "MyClass2.java" );                
        file.create( new ByteArrayInputStream( "ABC2".getBytes() ) ); 
        
        fld = fs.getFolder( "src/main/resources/org" );
        file = fld.getFile( "MyClass3.java" );                
        file.create( new ByteArrayInputStream( "ABC3".getBytes() ) );  
        file = fld.getFile( "MyClass4.java" );                
        file.create( new ByteArrayInputStream( "ABC4".getBytes() ) ); 
          
        
        fld = fs.getFolder( "src/main/resources/org/domain" );
        file = fld.getFile( "MyClass4.java" );                
        file.create( new ByteArrayInputStream( "ABC5".getBytes() ) );                        

        assertTrue( fs.getFolder( "src/main" ).exists() );
        assertTrue( fs.getFile( "src/main/MyClass1.java" ).exists() );
        assertTrue( fs.getFile( "src/main/MyClass2.java" ).exists() );
        assertTrue( fs.getFile( "src/main/resources/org/MyClass3.java" ).exists() );
        assertTrue( fs.getFile( "src/main/resources/org/MyClass4.java" ).exists() );        
        assertTrue( fs.getFile( "src/main/resources/org/domain/MyClass4.java" ).exists() );
                
        fs.remove( fs.getFolder( "src/main" ) );
        
        assertFalse( fs.getFolder( "src/main" ).exists() );
        assertFalse( fs.getFile( "src/main/MyClass1.java" ).exists() );
        assertFalse( fs.getFile( "src/main/MyClass2.java" ).exists() );
        assertFalse( fs.getFile( "src/main/resources/org/MyClass3.java" ).exists() );
        assertFalse( fs.getFile( "src/main/resources/org/MyClass4.java" ).exists() );        
        assertFalse( fs.getFile( "src/main/resources/org/domain/MyClass4.java" ).exists() );
    }

    @Test
    public void testCreateAndCopyFolder() {
        MemoryFileSystem memoryFileSystem = new MemoryFileSystem();

        // this also creates a folder if it doesn't exist
        final Folder emptyFolder = memoryFileSystem.getFolder("emptyfolder");
        final MemoryFolder destinationFolder = new MemoryFolder(memoryFileSystem, "destinationfolder");
        memoryFileSystem.createFolder(destinationFolder);
        memoryFileSystem.copyFolder(emptyFolder, memoryFileSystem, destinationFolder);
    }
}
