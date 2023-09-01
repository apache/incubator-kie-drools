/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
