package org.kie.memorycompiler;


import org.kie.memorycompiler.resources.ResourceReader;
import org.kie.memorycompiler.resources.ResourceStore;

/**
 * Base class for compiler implementations. Provides just a few
 * convenience methods.
 */
public abstract class AbstractJavaCompiler implements JavaCompiler {

    private JavaCompilerSettings javaCompilerSettings;

    public CompilationResult compile( final String[] pClazzNames, final ResourceReader pReader, final ResourceStore pStore ) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }

        return compile(pClazzNames, pReader, pStore, classLoader, createDefaultSettings());
    }

    public CompilationResult compile( final String[] pClazzNames, final ResourceReader pReader, final ResourceStore pStore, final ClassLoader pClassLoader ) {
        return compile(pClazzNames, pReader, pStore, pClassLoader, javaCompilerSettings != null ? javaCompilerSettings : createDefaultSettings());
    }

    public void setJavaCompilerSettings( JavaCompilerSettings javaCompilerSettings ) {
        this.javaCompilerSettings = javaCompilerSettings;
    }
}
