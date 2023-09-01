package org.drools.mvel.compiler.kproject.memory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.FileSystem;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.util.StringUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class MemoryFileTest {

    @Test
    public void testFileCreation() throws IOException {
        FileSystem fs = new MemoryFileSystem();
        
        Folder mres = fs.getFolder( "src/main/java/org/domain" );
        
        File f1 = mres.getFile( "MyClass.java" );
        f1.create( new ByteArrayInputStream( "ABC".getBytes() ) );
        
        mres.create();
        
        f1 = mres.getFile( "MyClass.java" );
        assertThat(f1.exists()).isTrue();
        
        f1.create( new ByteArrayInputStream( "ABC".getBytes() ) );        
        f1 = mres.getFile( "MyClass.java" );
        assertThat(f1.exists()).isTrue();

        assertThat(StringUtils.toString(f1.getContents())).isEqualTo("ABC");

        f1.create( new ByteArrayInputStream( "ABC".getBytes() ) );
        
        f1.setContents( new ByteArrayInputStream( "DEF".getBytes() ) );
        assertThat(StringUtils.toString(f1.getContents())).isEqualTo("DEF");
    }
    
    @Test
    public void testFileRemoval() throws IOException {
        FileSystem fs = new MemoryFileSystem();
        
        Folder mres = fs.getFolder( "src/main/java/org/domain" );  
        mres.create();
        
        File f1 = mres.getFile( "MyClass.java" );                
        f1.create( new ByteArrayInputStream( "ABC".getBytes() ) );
        assertThat(f1.exists()).isTrue();
        assertThat(StringUtils.toString(f1.getContents())).isEqualTo("ABC");
        
        fs.remove( f1 );
        
        f1 = mres.getFile( "MyClass.java" );
        assertThat(f1.exists()).isFalse();
        
        try {
            f1.getContents();
            fail( "Should throw IOException" );
        } catch( IOException e ) {
            
        }      
    }

    @Test
    public void testFilePath() {
        FileSystem fs = new MemoryFileSystem();
        
        Folder mres = fs.getFolder( "src/main/java/org/domain" );  
        
        File f1 = mres.getFile( "MyClass.java" );
        assertThat(f1.getPath().asString()).isEqualTo("src/main/java/org/domain/MyClass.java");
    }
}
