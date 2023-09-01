/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
