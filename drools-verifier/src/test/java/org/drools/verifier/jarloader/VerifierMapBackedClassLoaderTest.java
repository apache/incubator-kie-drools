package org.drools.verifier.jarloader;

import org.drools.verifier.Verifier;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.JarInputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VerifierMapBackedClassLoaderTest {

    @Test
    public void testCheckResources() throws Exception {
        ArrayList<JarInputStream> jarInputStreams = new ArrayList<JarInputStream>();
        jarInputStreams.add(new JarInputStream(Verifier.class.getResourceAsStream("model.jar")));

        VerifierMapBackedClassLoader verifierMapBackedClassLoader = new VerifierMapBackedClassLoader(jarInputStreams);

        assertNotNull(verifierMapBackedClassLoader.getStore().containsKey("org.test.Person"));
        assertNotNull(verifierMapBackedClassLoader.getStore().containsKey("org.test.Rambo"));
        assertNotNull(verifierMapBackedClassLoader.getStore().containsKey("org.test.Pet"));
    }

    @Test()
    public void testToMakeSureExceptionsAreNotLost() throws Exception {
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
