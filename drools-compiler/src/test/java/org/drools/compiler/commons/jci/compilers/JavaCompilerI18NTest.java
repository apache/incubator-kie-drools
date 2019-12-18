/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.commons.jci.compilers;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavaCompilerI18NTest {

    @Test
    public void testi18NFile () throws Exception {
        String fileStr = "com/myspace/test/あ.java";
        List<String> classes = new ArrayList<>();
        classes.add(fileStr);

        MemoryFileSystem fs = new MemoryFileSystem();
        MemoryFile file = (MemoryFile) fs.getFile(fileStr);

        String fileContents = "package com.myspace.test; public class あ { }";
        fs.setFileContents(file, fileContents.getBytes());


        EclipseJavaCompilerSettings settings = new EclipseJavaCompilerSettings();
        settings.setSourceVersion( "1.5" );
        settings.setTargetVersion( "1.5" );
        EclipseJavaCompiler compiler = new EclipseJavaCompiler( settings, "" );
        CompilationResult res = compiler.compile( classes.toArray( new String[classes.size()] ), fs, fs );
        assertEquals(res.getErrors().length, 0);
	}
}
