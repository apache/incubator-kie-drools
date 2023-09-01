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
package org.kie.pmml.compiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PMMLTestUtils {

    private PMMLTestUtils() {
    }

    /**
     * Collect drl files under `startPath`
     */
    public static Set<File> collectFiles(String startPath, String suffix) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(startPath))) {
            String dottedSuffix = suffix.startsWith(".") ? suffix : "." + suffix;
            return paths.map(Path::toFile)
                    .filter(File::isFile)
                    .filter(f -> f.getName().endsWith(dottedSuffix))
                    .collect(Collectors.toSet());
        }
    }
}
