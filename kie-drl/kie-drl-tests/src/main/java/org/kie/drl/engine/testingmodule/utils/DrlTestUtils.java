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
package org.kie.drl.engine.testingmodule.utils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.efesto.common.api.io.IndexFile;

import static org.kie.efesto.common.api.constants.Constants.INDEXFILE_DIRECTORY_PROPERTY;

public class DrlTestUtils {

    private static final String SRC_TEST_RESOURCES = "src/test/resources";
    private static final String TARGET_TEST_CLASSES = "target/test-classes"; // for the case running tests without maven

    private DrlTestUtils() {}

    /**
     * refresh target/classes/IndexFile.drl_json with src/main/resources/IndexFile.drl_json because basically, unit tests should not depend on the file's state
     */
    public static void refreshDrlIndexFile() {
        IndexFile source = new IndexFile(SRC_TEST_RESOURCES, "drl");
        String parentPath = System.getProperty(INDEXFILE_DIRECTORY_PROPERTY, TARGET_TEST_CLASSES);
        IndexFile target = new IndexFile(parentPath, "drl");

        Path sourcePath = source.getAbsoluteFile().toPath();
        Path targetPath = target.getAbsoluteFile().toPath();
        try {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Collect drl files under `startPath`
     */
    public static Set<File> collectDrlFiles(String startPath) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(startPath))) {
            return paths.map(Path::toFile)
                    .filter(File::isFile)
                    .filter(f -> f.getName().endsWith(".drl"))
                    .collect(Collectors.toSet());
        }
    }
}
