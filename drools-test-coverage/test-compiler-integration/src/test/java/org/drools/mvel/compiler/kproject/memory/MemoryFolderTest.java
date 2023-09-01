package org.drools.mvel.compiler.kproject.memory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.FileSystem;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.compiler.io.memory.MemoryFolder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryFolderTest {
    
    @Test
    public void testGetParentWithLeadingAndTrailingSlash() {
        MemoryFileSystem mfs = new MemoryFileSystem();
        assertThat(new MemoryFolder( mfs, "/src" ).getParent().getPath().asString()).isEqualTo("");

        assertThat(new MemoryFolder( mfs, "src/" ).getParent().getPath().asString()).isEqualTo("");

        assertThat(new MemoryFolder( mfs, "/src/" ).getParent().getPath().asString()).isEqualTo("");

        assertThat(new MemoryFolder( mfs, "/src/main" ).getParent().getPath().asString()).isEqualTo("/src");

        assertThat(new MemoryFolder( mfs, "src/main/" ).getParent().getPath().asString()).isEqualTo("src");

        assertThat(new MemoryFolder( mfs, "/src/main/" ).getParent().getPath().asString()).isEqualTo("/src");

        assertThat(new MemoryFolder( mfs, "/src/main/java" ).getParent().getPath().asString()).isEqualTo("/src/main");

        assertThat(new MemoryFolder( mfs, "src/main/java/" ).getParent().getPath().asString()).isEqualTo("src/main");

        assertThat(new MemoryFolder( mfs, "/src/main/java/" ).getParent().getPath().asString()).isEqualTo("/src/main");
    }
    
    
    @Test
    public void testRecursiveFolderCreation() {
        FileSystem fs = new MemoryFileSystem();
        
        Folder mres = fs.getFolder( "src/main/resources" );
        assertThat(mres.exists()).isFalse();
        mres.create();
        assertThat(mres.exists()).isTrue();
        
        Folder fld = fs.getFolder( "src/main" );
        assertThat(fld.exists()).isTrue();  
        
        Folder src = fs.getFolder( "src" );
        assertThat(src.exists()).isTrue();         
    }
    
    @Test
    public void testFolderGetParent() {
        FileSystem fs = new MemoryFileSystem();
        
        Folder mres = fs.getFolder( "src/main/resources" );
        mres.create();

        assertThat(mres.getParent().getPath().asString()).isEqualTo("src/main");

        assertThat(mres.getParent().getParent().getPath().asString()).isEqualTo("src");
        
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

        assertThat(fs.getFolder("src/main").exists()).isTrue();
        assertThat(fs.getFile("src/main/MyClass1.java").exists()).isTrue();
        assertThat(fs.getFile("src/main/MyClass2.java").exists()).isTrue();
        assertThat(fs.getFile("src/main/resources/org/MyClass3.java").exists()).isTrue();
        assertThat(fs.getFile("src/main/resources/org/MyClass4.java").exists()).isTrue();
        assertThat(fs.getFile("src/main/resources/org/domain/MyClass4.java").exists()).isTrue();
                
        fs.remove( fs.getFolder( "src/main" ) );

        assertThat(fs.getFolder("src/main").exists()).isFalse();
        assertThat(fs.getFile("src/main/MyClass1.java").exists()).isFalse();
        assertThat(fs.getFile("src/main/MyClass2.java").exists()).isFalse();
        assertThat(fs.getFile("src/main/resources/org/MyClass3.java").exists()).isFalse();
        assertThat(fs.getFile("src/main/resources/org/MyClass4.java").exists()).isFalse();
        assertThat(fs.getFile("src/main/resources/org/domain/MyClass4.java").exists()).isFalse();
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
