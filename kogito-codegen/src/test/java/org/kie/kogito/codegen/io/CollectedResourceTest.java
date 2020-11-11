/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen.io;

import java.io.File;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;

class CollectedResourceTest {

    @Test
    void shouldNotContainDirectories() {
        assertThat(
                CollectedResource.fromDirectory(Paths.get("src/main/resources"))
                        .stream()
                        .map(CollectedResource::resource)
                        .map(Resource::getSourcePath)
                        .map(File::new)
                        .filter(File::isDirectory)
                        .count()).isZero();
    }
}