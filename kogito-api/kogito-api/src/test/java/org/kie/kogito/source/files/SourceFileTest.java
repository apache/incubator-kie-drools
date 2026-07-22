/*
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
package org.kie.kogito.source.files;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SourceFileTests {

    @Test
    void toPosixPath() {
        assertThat(SourceFile.toPosixPath(null)).isNull();
        assertThat(SourceFile.toPosixPath(Path.of("test/petstore.json"))).isEqualTo("test/petstore.json");
        assertThat(SourceFile.toPosixPath(Path.of("foo/bar/test/petstore.json"))).isEqualTo("foo/bar/test/petstore.json");
        assertThat(SourceFile.toPosixPath(Path.of("test\\petstore.json"))).isEqualTo("test/petstore.json");
        assertThat(SourceFile.toPosixPath(Path.of("foo\\bar\\test\\petstore.json"))).isEqualTo("foo/bar/test/petstore.json");
        assertThat(SourceFile.toPosixPath(Path.of("petstore.json"))).isEqualTo("petstore.json");
    }
}
