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

package org.drools.java.compiler.jdknative;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NativeJavaCompilerTest {

    @Test
    public void testJarUri() {
        String packageUrl = "jar:file:/home/.../drools-executable-reproducer-1.0-SNAPSHOT.jar!/BOOT-INF/lib/drools-canonical-model-7.47.0-SNAPSHOT.jar!/org/drools/model/functions";
        String jarUri = "jar:file:/home/.../drools-executable-reproducer-1.0-SNAPSHOT.jar!/BOOT-INF/lib/drools-canonical-model-7.47.0-SNAPSHOT.jar";
        assertEquals(jarUri, NativeJavaCompiler.jarUri(packageUrl));

        String simpleJarUri = "jar:file:/home/.../drools-executable-reproducer-1.0-SNAPSHOT.jar";
        assertEquals(simpleJarUri, NativeJavaCompiler.jarUri(simpleJarUri));
    }
}
