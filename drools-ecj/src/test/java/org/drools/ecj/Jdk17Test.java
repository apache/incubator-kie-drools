/*
 * Copyright (c) 2023. Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.resources.MemoryResourceReader;
import org.kie.memorycompiler.resources.MemoryResourceStore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Jdk17Test {

    @Test
    public void testRecordsWithVer17() throws Exception {
        String fileStr = "com/myspace/test/Criteria.java";
        List<String> classes = new ArrayList<>();
        classes.add(fileStr);

        MemoryResourceReader reader = new MemoryResourceReader();
        MemoryResourceStore store = new MemoryResourceStore();

        String fileContents = "package com.myspace.test; public record Criteria(String name) {}";
        reader.add(fileStr, fileContents.getBytes());

        EclipseJavaCompilerSettings settings = new EclipseJavaCompilerSettings();
        settings.setSourceVersion( "17" );
        settings.setTargetVersion( "17" );
        EclipseJavaCompiler compiler = new EclipseJavaCompiler( settings, "" );
        CompilationResult res = compiler.compile( classes.toArray( new String[classes.size()] ), reader, store );
        System.out.println(Arrays.asList(res.getErrors()));
        assertThat(0).isEqualTo(res.getErrors().length);
	}
}
