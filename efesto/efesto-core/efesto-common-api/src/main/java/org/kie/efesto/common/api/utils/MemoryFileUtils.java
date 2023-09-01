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
package org.kie.efesto.common.api.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.io.MemoryFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryFileUtils {

    private static final Logger logger = LoggerFactory.getLogger(MemoryFileUtils.class.getName());

    private static final String TO_RETURN_TEMPLATE = "toReturn {}";
    private static final String TO_RETURN_GETABSOLUTEPATH_TEMPLATE = "toReturn.getAbsolutePath() {}";

    private MemoryFileUtils() {
    }


    public static Optional<File> getFileFromFileNameOrFilePath(String fileName, String filePath) {
        Optional<File> fromClassloader = getFileByFileNameFromClassloader(fileName, Thread.currentThread().getContextClassLoader());
        return fromClassloader.isPresent() ? fromClassloader :  getFileByFilePath(filePath);
    }

    public static Optional<File> getFileFromFileName(String fileName) {
        logger.debug("getFileFromFileName {}", fileName);
        return getFileByFileNameFromClassloader(fileName, Thread.currentThread().getContextClassLoader());
    }

    public static Optional<File> getFileByFileNameFromClassloader(String fileName, ClassLoader classLoader) {
        logger.debug("getFileByFileNameFromClassloader {} {}", fileName, classLoader);
        URL retrieved = classLoader.getResource(fileName);
        if (retrieved != null) {
            logger.debug("retrieved {}", retrieved);
            return getFileFromURL(retrieved);
        }
        File file = new File(fileName);
        logger.debug("file {}", file);
        logger.debug("file.exists() {}", file.exists());
        return file.exists() ? Optional.of(file) : Optional.empty();
    }

    public static Optional<File> getFileByFilePath(String filePath) {
        File file = new File(filePath);
        return file.exists() ? Optional.of(file) : Optional.empty();
    }

    static Optional<File> getFileFromURL(URL retrieved) {
        logger.debug("getFileFromURL {}", retrieved);
        logger.debug("retrieved.getProtocol() {}", retrieved.getProtocol());
        if (logger.isDebugEnabled()) {
            debugURLContent(retrieved);
        }
        logger.debug("retrieved.getPath() {}", retrieved.getPath());
        switch (retrieved.getProtocol()) {
            case "jar":
                return getOptionalFileFromJar(retrieved);
            case "resource":
                return  getOptionalFileFromResource(retrieved);
            default:
                return getOptionalFileFromURLFile(retrieved);
        }
    }

    static Optional<File> getOptionalFileFromJar(URL retrieved) {
        try {
            File toReturn = getFileFromJar(retrieved);
            logger.debug(TO_RETURN_TEMPLATE, toReturn);
            return Optional.of(toReturn);
        } catch (Exception e) {
            throw new KieEfestoCommonException("Failed to read file " + retrieved, e);
        }
    }

    static Optional<File> getOptionalFileFromResource(URL retrieved) {
        try {
            File toReturn = getFileFromResource(retrieved);
            logger.debug(TO_RETURN_TEMPLATE, toReturn);
            return Optional.of(toReturn);
        } catch (Exception e) {
            throw new KieEfestoCommonException("Failed to read file " + retrieved, e);
        }
    }

    static Optional<File> getOptionalFileFromURLFile(URL retrieved) {
        File toReturn = new File(retrieved.getFile());
        logger.debug(TO_RETURN_TEMPLATE, toReturn);
        logger.debug(TO_RETURN_GETABSOLUTEPATH_TEMPLATE, toReturn.getAbsolutePath());
        return Optional.of(toReturn);
    }

    static File getFileFromResource(URL retrieved) throws IOException {
        logger.debug("getFileFromResource {}", retrieved);
        File toReturn = new MemoryFile(retrieved);
        logger.debug(TO_RETURN_TEMPLATE, toReturn);
        logger.debug(TO_RETURN_GETABSOLUTEPATH_TEMPLATE, toReturn.getAbsolutePath());
        return toReturn;
    }

    static File getFileFromJar(URL retrieved) throws URISyntaxException, IOException {
        logger.debug("getFileFromJar {}", retrieved);
        String fileName = retrieved.getFile();
        if (fileName.contains("/")) {
            fileName = fileName.substring(fileName.lastIndexOf('/'));
        }
        String jarPath = retrieved.toString();
        jarPath = jarPath.substring(0, jarPath.lastIndexOf("!/") + 2);
        URI uri = new URI(jarPath);
        Map<String, ?> env = new HashMap<>();
        Path filePath;
        try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
            filePath = fs.getPath(fileName);
        }
        File toReturn = new MemoryFile(filePath);
        logger.debug(TO_RETURN_TEMPLATE, toReturn);
        logger.debug(TO_RETURN_GETABSOLUTEPATH_TEMPLATE, toReturn.getAbsolutePath());
        return toReturn;
    }

    static void debugURLContent(URL retrieved) {
        if (retrieved != null) {
            try(InputStream input = retrieved.openStream()) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int read;
                byte[] bytes = new byte[1024];
                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                logger.debug("retrieved.getContent() {}", out.toByteArray());
                out.flush();
                out.close();
            } catch (Exception e) {
                logger.warn("failed to read content for {}", retrieved);
            }
        }
    }
}
