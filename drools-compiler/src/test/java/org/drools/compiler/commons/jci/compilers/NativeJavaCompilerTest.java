package org.drools.compiler.commons.jci.compilers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NativeJavaCompilerTest {

    @Test
    public void testJarUri() {
        String packageUrl = "jar:file:/home/.../drools-executable-reproducer-1.0-SNAPSHOT.jar!/BOOT-INF/lib/drools-canonical-model-7.46.0-SNAPSHOT.jar!/org/drools/model/functions";
        String jarUri = "jar:file:/home/.../drools-executable-reproducer-1.0-SNAPSHOT.jar!/BOOT-INF/lib/drools-canonical-model-7.46.0-SNAPSHOT.jar";
        assertEquals(jarUri, NativeJavaCompiler.jarUri(packageUrl));

        String simpleJarUri = "jar:file:/home/.../drools-executable-reproducer-1.0-SNAPSHOT.jar";
        assertEquals(simpleJarUri, NativeJavaCompiler.jarUri(simpleJarUri));
    }
}
