/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.service;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class TestUtils {

    private TestUtils() {
    }

    public static byte[] readFileContent(String classPathResource) throws Exception {
        URL url = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(classPathResource),
                "Required test resource was not found in class path: " + classPathResource);
        Path path = Paths.get(url.toURI());
        return Files.readAllBytes(path);
    }
}
