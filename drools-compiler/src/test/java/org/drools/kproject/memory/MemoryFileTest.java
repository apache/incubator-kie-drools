package org.drools.kproject.memory;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.drools.compiler.io.File;
import org.drools.compiler.io.FileSystem;
import org.drools.compiler.io.Folder;
import org.drools.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

public class MemoryFileTest {
    @Test
    @Ignore // this now passes, as we want to allow overwriting as default
    public void testFileCreation() throws IOException {
        FileSystem fs = new MemoryFileSystem();
        
        Folder mres = fs.getFolder( "src/main/java/org/domain" );  
        
        File f1 = mres.getFile( "MyClass.java" );
        try {
            f1.create( new ByteArrayInputStream( "ABC".getBytes() ) );
            fail( "Folder does not exist, cannot create file" );
        } catch (IOException e){
            
        }
        
        mres.create();
        
        f1 = mres.getFile( "MyClass.java" );
        assertFalse( f1.exists());
        
        f1.create( new ByteArrayInputStream( "ABC".getBytes() ) );        
        f1 = mres.getFile( "MyClass.java" );
        assertTrue( f1.exists() );
        
        assertEquals( "ABC", StringUtils.toString( f1.getContents() ) );
        
        try {
            f1.create( new ByteArrayInputStream( "ABC".getBytes() ) );
            fail( "file already exists, should only allow setContents" );
        } catch (IOException e){
            
        }
        
        f1.setContents( new ByteArrayInputStream( "DEF".getBytes() ) );
        assertEquals( "DEF", StringUtils.toString( f1.getContents() ) );
    }
    
    @Test
    public void testFileRemoval() throws IOException {
        FileSystem fs = new MemoryFileSystem();
        
        Folder mres = fs.getFolder( "src/main/java/org/domain" );  
        mres.create();
        
        File f1 = mres.getFile( "MyClass.java" );                
        f1.create( new ByteArrayInputStream( "ABC".getBytes() ) );        
        assertTrue( f1.exists() );        
        assertEquals( "ABC", StringUtils.toString( f1.getContents() ) );
        
        fs.remove( f1 );
        
        f1 = mres.getFile( "MyClass.java" );  
        assertFalse( f1.exists() );
        
        try {
            f1.getContents();
            fail( "Should throw IOException" );
        } catch( IOException e ) {
            
        }      
    }
    
    public void testFilePath() {
        FileSystem fs = new MemoryFileSystem();
        
        Folder mres = fs.getFolder( "src/main/java/org/domain" );  
        
        File f1 = mres.getFile( "MyClass.java" );
        assertEquals( "src/main/java/org/domain/MyClass.java",
                      f1.getPath().toPortableString() );
    }
    
    public void testRelativeToParentFilePath() {
        FileSystem fs = new MemoryFileSystem();
        
        Folder mres = fs.getFolder( "src/main/java/org/domain" );
        Folder f2 = fs.getFolder( "src/main/java/org/domain/f1/f2/" );
        
        File f1 = mres.getFile( "MyClass.java" );
        assertEquals( "../../MyClass.java",
                      f1.getPath().toRelativePortableString( f2.getPath() ) );
    }  
    
    public void testRelativeToBranchFilePath() {
        FileSystem fs = new MemoryFileSystem();
        
        Folder mres = fs.getFolder( "src/main/java/org/domain" );
        Folder f2 = fs.getFolder( "src/main/resources/org/domain/" );
        
        File f1 = mres.getFile( "MyClass.java" );
        assertEquals( "../../../src/main/java/org/domain/MyClass.java",
                      f1.getPath().toRelativePortableString( f2.getPath() ) );
    }     
}
