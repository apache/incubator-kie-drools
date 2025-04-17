/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JarUtilsTest {

    @Test
    void normalizeSpringBootResourceUrlPath() {
        String normalized = JarUtils.normalizeSpringBootResourceUrlPath("/path/to/demo.jar!/BOOT-INF/classes!/org/example/MyClass.class");
        assertThat(normalized).isEqualTo("/path/to/demo.jar!/BOOT-INF/classes/org/example/MyClass.class");
    }

    @Test
    void normalizeSpringBootResourceUrlPathSB32() {
        String normalized = JarUtils.normalizeSpringBootResourceUrlPath("/path/to/demo.jar/!BOOT-INF/classes/!/org/example/MyClass.class");
        assertThat(normalized).isEqualTo("/path/to/demo.jar!/BOOT-INF/classes/org/example/MyClass.class");
    }

    @Test
    void replaceNestedPathForSpringBoot32_shouldNotAffectOldPath() {
        String result = JarUtils.replaceNestedPathForSpringBoot32("/dir/myapp.jar!/BOOT-INF/lib/mykjar.jar");
        assertThat(result).isEqualTo("/dir/myapp.jar!/BOOT-INF/lib/mykjar.jar");
    }

    @Test
    void replaceNestedPathForSpringBoot32_shouldReplaceNewPath() {
        String result = JarUtils.replaceNestedPathForSpringBoot32("/dir/myapp.jar/!BOOT-INF/lib/mykjar.jar");
        assertThat(result).isEqualTo("/dir/myapp.jar!/BOOT-INF/lib/mykjar.jar");
    }

    @Test
    void getPathWithoutScheme() throws MalformedURLException {
        URL url = new URL("jar", "", "nested:/path/to/demo.jar/!BOOT-INF/classes/!/org/example/MyClass.class");
        String path = JarUtils.getPathWithoutScheme(url);
        assertThat(path).isEqualTo("/path/to/demo.jar/!BOOT-INF/classes/!/org/example/MyClass.class");
    }
}