/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ecj;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.resources.MemoryResourceReader;
import org.kie.memorycompiler.resources.MemoryResourceStore;

import static org.junit.Assert.assertEquals;

public class JavaCompilerI18NTest {

    @Test
    public void testi18NFile () throws Exception {
        Path fileStr = Paths.get("com", "myspace", "test", "あ.java");
        List<Path> classes = new ArrayList<>();
        classes.add(fileStr);

        MemoryResourceReader reader = new MemoryResourceReader();
        MemoryResourceStore store = new MemoryResourceStore();

        String fileContents = "package com.myspace.test; public class あ { }";
        reader.add(fileStr, fileContents.getBytes());

        EclipseJavaCompilerSettings settings = new EclipseJavaCompilerSettings();
        settings.setSourceVersion( "1.5" );
        settings.setTargetVersion( "1.5" );
        EclipseJavaCompiler compiler = new EclipseJavaCompiler( settings, Paths.get("") );
        CompilationResult res = compiler.compile( classes.toArray( new Path[classes.size()] ), reader, store );
        assertEquals(res.getErrors().length, 0);
	}
}
