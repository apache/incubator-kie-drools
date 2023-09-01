package org.kie.memorycompiler;

import java.util.Optional;

/**
 * Creates JavaCompilers
 */
public class JavaCompilerFactory {

    public static JavaCompiler loadCompiler( JavaConfiguration configuration) {
        return loadCompiler( configuration.getCompiler(), configuration.getJavaLanguageLevel() );
    }

    public static JavaCompiler loadCompiler( JavaConfiguration.CompilerType compilerType, String lngLevel ) {
        return loadCompiler( compilerType, lngLevel, "" );
    }

    public static JavaCompiler loadCompiler( JavaConfiguration.CompilerType compilerType, String lngLevel, String sourceFolder ) {
        JavaCompiler compiler = createCompiler( compilerType ).orElseThrow( () -> new RuntimeException("Instance of " + compilerType + " compiler cannot be created!") );
        compiler.setJavaCompilerSettings( createSettings( compiler, lngLevel ) );
        compiler.setSourceFolder(sourceFolder);
        return compiler;
    }

    private static JavaCompilerSettings createSettings( JavaCompiler compiler, String lngLevel ) {
        JavaCompilerSettings settings = compiler.createDefaultSettings();
        settings.setTargetVersion( lngLevel );
        settings.setSourceVersion( lngLevel );
        return settings;
    }

    private static Optional<JavaCompiler> createCompiler( JavaConfiguration.CompilerType compilerType) {
        return createCompiler(compilerType.getImplClass());
    }

    private static Optional<JavaCompiler> createCompiler(Class compilerClass) {
        try {
            return Optional.of( (JavaCompiler) compilerClass.getConstructor().newInstance() );
        } catch (Throwable t) {
            return Optional.empty();
        }
    }
}
