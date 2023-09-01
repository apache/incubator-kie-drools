package org.drools.ecj;

import java.util.ArrayList;
import java.util.List;

import org.kie.memorycompiler.CompilationResult;
import org.kie.memorycompiler.resources.MemoryResourceReader;
import org.kie.memorycompiler.resources.MemoryResourceStore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaCompilerI18NTest {

    @Test
    public void testi18NFile () throws Exception {
        String fileStr = "com/myspace/test/あ.java";
        List<String> classes = new ArrayList<>();
        classes.add(fileStr);

        MemoryResourceReader reader = new MemoryResourceReader();
        MemoryResourceStore store = new MemoryResourceStore();

        String fileContents = "package com.myspace.test; public class あ { }";
        reader.add(fileStr, fileContents.getBytes());

        EclipseJavaCompilerSettings settings = new EclipseJavaCompilerSettings();
        settings.setSourceVersion( "1.5" );
        settings.setTargetVersion( "1.5" );
        EclipseJavaCompiler compiler = new EclipseJavaCompiler( settings, "" );
        CompilationResult res = compiler.compile( classes.toArray( new String[classes.size()] ), reader, store );
        assertThat(0).isEqualTo(res.getErrors().length);
	}
}
