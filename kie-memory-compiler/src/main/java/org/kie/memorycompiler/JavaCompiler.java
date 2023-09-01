package org.kie.memorycompiler;


import org.kie.memorycompiler.resources.ResourceReader;
import org.kie.memorycompiler.resources.ResourceStore;

/**
 * The general compiler interface. All compilers implementing
 * this interface should read the resources from the reader
 * and store the java class files into the ResourceStore.
 * 
 * The actual compilation language does not matter. But the
 * contract is that the result of the compilation will be a
 * class file.
 * 
 * If possible the compiler should notify the optional
 * CompilationProblemHandler as soon as a problem is found.
 */
public interface JavaCompiler {

    boolean DUMP_GENERATED_CLASSES = false;

    static JavaCompiler getCompiler() {
        return CompilerHolder.JAVA_COMPILER;
    }

    class CompilerHolder {
        private static final JavaCompiler JAVA_COMPILER =
                System.getProperty("jboss.server.name") != null ?
                        JavaCompiler.createEclipseCompiler() :
                        JavaCompiler.createNativeCompiler();
    }

    static JavaCompiler createNativeCompiler() {
        return createNativeCompiler(System.getProperty("java.version"));
    }

    static JavaCompiler createNativeCompiler(String javaVersion) {
        return JavaCompilerFactory.loadCompiler(JavaConfiguration.CompilerType.NATIVE, javaVersion, "src/main/java/");
    }

    static JavaCompiler createEclipseCompiler() {
        return createEclipseCompiler(System.getProperty("java.version"));
    }

    static JavaCompiler createEclipseCompiler(String javaVersion) {
        return JavaCompilerFactory.loadCompiler(JavaConfiguration.CompilerType.ECLIPSE, javaVersion, "src/main/java/");
    }

    default void setSourceFolder( String sourceFolder ) { }

    /**
     * factory method to create the underlying default settings
     */
    JavaCompilerSettings createDefaultSettings();

    void setJavaCompilerSettings( JavaCompilerSettings javaCompilerSettings );
    
    /**
     * uses the default compiler settings and the current classloader
     */
    CompilationResult compile( final String[] pResourcePaths, final ResourceReader pReader, final ResourceStore pStore );

    /**
     * uses the default compiler settings
     */
    CompilationResult compile( final String[] pResourcePaths, final ResourceReader pReader, final ResourceStore pStore, final ClassLoader pClassLoader );

    /**
     * Compiles the java resources "some/path/to/MyJava.java"
     * read through the ResourceReader and then stores the resulting
     * classes in the ResourceStore under "some/path/to/MyJava.class".
     * Note: As these are resource path you always have to use "/" 
     * 
     * The result of the compilation run including detailed error
     * information is returned as CompilationResult. If you need to
     * get notified already during the compilation process you can
     * register a CompilationProblemHandler.
     * Note: Not all compilers might support this notification mechanism.
     * 
     * @param pResourcePaths
     * @param pReader
     * @param pStore
     * @param pClassLoader
     * @param pSettings
     * @return always a CompilationResult
     */
    CompilationResult compile( final String[] pResourcePaths, final ResourceReader pReader, final ResourceStore pStore, final ClassLoader pClassLoader, final JavaCompilerSettings pSettings );

}
