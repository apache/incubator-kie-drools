package org.drools.verifier.jarloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.JarInputStream;

import org.drools.verifier.Verifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VerifierMapBackedClassLoaderTest {

    @Test
    void testCheckResources() throws Exception {
        ArrayList<JarInputStream> jarInputStreams = new ArrayList<JarInputStream>();
        jarInputStreams.add(new JarInputStream(Verifier.class.getResourceAsStream("model.jar")));

        VerifierMapBackedClassLoader verifierMapBackedClassLoader = new VerifierMapBackedClassLoader(jarInputStreams);

        assertThat(verifierMapBackedClassLoader.getStore()).containsKeys("org.test.Person", "org.test.Rambo", "org.test.Pet");
    }

    @Test
    void testToMakeSureExceptionsAreNotLost() throws Exception {
        ArrayList<JarInputStream> jarInputStreams = new ArrayList<JarInputStream>();

        JarInputStream jarInputStream = mock(JarInputStream.class);

        when(jarInputStream.getNextJarEntry()).thenThrow(new IOException());

        jarInputStreams.add(jarInputStream);

        try {
            new VerifierMapBackedClassLoader(jarInputStreams);
        } catch (IOException e) {
            return;
        }

        fail("Expected IOException");
    }
}
