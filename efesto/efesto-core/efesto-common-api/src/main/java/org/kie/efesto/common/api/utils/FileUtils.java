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
package org.kie.efesto.common.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;

public class FileUtils {

    private FileUtils() {
    }


    /**
     * Retrieve the <code>File</code> of the given <b>file</b>
     *
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
        if (toReturn == null) {
            throw new KieEfestoCommonException(String.format("Failed to find %s due to", fileName));
        }
        return toReturn;
    }

    /**
     * Retrieve the <code>FileInputStream</code> of the given <b>file</b>
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static FileInputStream getFileInputStream(String fileName) throws IOException {
        File sourceFile = getFile(fileName);
        return new FileInputStream(sourceFile);
    }

    /**
     * Retrieve the <b>content</b> of the given <b>file</b>
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String getFileContent(String fileName) throws IOException {
        File file = getFile(fileName);
        Path path = file.toPath();
        Stream<String> lines = Files.lines(path);
        String toReturn = lines.collect(Collectors.joining("\n"));
        lines.close();
        return toReturn;
    }

    public static InputStream getInputStreamFromFileName(String fileName) {
        try {
            InputStream toReturn = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (toReturn == null) {
                throw new KieEfestoCommonException(String.format("Failed to find %s", fileName));
            } else {
                return toReturn;
            }
        } catch (Exception e) {
            throw new KieEfestoCommonException(String.format("Failed to find %s due to %s", fileName,
                    e.getMessage()), e);
        }
    }

    public static Optional<File> getFileFromFileName(String fileName) {
        return getFileFromFileName(fileName, Thread.currentThread().getContextClassLoader());
    }

    public static Optional<File> getFileFromFileName(String fileName, ClassLoader classLoader) {
        URL retrieved = classLoader.getResource(fileName);
        if (retrieved != null) {
            return Optional.of(new File(retrieved.getFile()));
        }
        File file = new File(fileName);
        return file.exists() ? Optional.of(file) : Optional.empty();
    }
}
