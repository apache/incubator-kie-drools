/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.test.util.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * Utility to access files
 */
public class FileUtils {

    private FileUtils() {
        // Avoid instantiating class
    }

    /**
     * Retrieve the <code>File</code> of the given <b>file</b>
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File getFile(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        File toReturn = ResourceHelper.getResourcesByExtension(extension)
                .filter(file -> file.getName().equals(fileName))
                .findFirst()
                .orElse(null);
        assertNotNull(toReturn);
        return toReturn;
    }

    /**
     * Retrieve the <code>FileInputStream</code> of the given <b>file</b>
     * @param fileName
     * @return
     * @throws IOException
     */
    public static FileInputStream getFileInputStream(String fileName) throws IOException {
        File sourceFile = getFile(fileName);
        return new FileInputStream(sourceFile);
    }
}
