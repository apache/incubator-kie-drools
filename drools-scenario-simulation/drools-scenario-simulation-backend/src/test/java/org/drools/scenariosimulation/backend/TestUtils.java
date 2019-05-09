/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.scenariosimulation.backend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.drools.scenariosimulation.backend.util.ResourceHelper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Class used to provide commonly used method for test classes
 */
public class TestUtils {

    public static String getFileContent(String fileName) throws IOException {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String filePath = ResourceHelper.getResourcesByExtension(extension)
                .filter(path -> path.endsWith(fileName))
                .findFirst()
                .orElse(null);
        assertNotNull(filePath);
        File sourceFile = new File(filePath);
        assertTrue(sourceFile.exists());
        return new String(Files.readAllBytes(sourceFile.toPath()));
    }

}
