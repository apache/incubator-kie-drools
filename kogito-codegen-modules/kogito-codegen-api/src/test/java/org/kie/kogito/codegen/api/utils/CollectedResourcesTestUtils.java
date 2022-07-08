/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.api.utils;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.io.FileSystemResource;
import org.kie.kogito.codegen.api.io.CollectedResource;

public final class CollectedResourcesTestUtils {

    private static final String TEST_RESOURCE_PATH = "src/test/resources";

    private CollectedResourcesTestUtils() {

    }

    /**
     * @see #toCollectedResources(String...)
     */
    public static CollectedResource toCollectedResource(String path) {
        return new CollectedResource(Paths.get(TEST_RESOURCE_PATH + path), new FileSystemResource(TEST_RESOURCE_PATH + path));
    }

    /**
     * Transform Test Resources in {@link CollectedResource} to be used in unit tests.
     *
     * @param paths relative to "src/test/resources". For example "/myfile.bpmn".
     * @return A list of {@link CollectedResource}
     */
    public static List<CollectedResource> toCollectedResources(String... paths) {
        return Arrays.stream(paths).map(p -> new CollectedResource(Paths.get(TEST_RESOURCE_PATH + p), new FileSystemResource(TEST_RESOURCE_PATH + p))).collect(Collectors.toList());
    }
}
