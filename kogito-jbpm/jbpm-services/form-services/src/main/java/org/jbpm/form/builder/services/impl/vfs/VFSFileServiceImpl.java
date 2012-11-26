/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.form.builder.services.impl.vfs;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.form.builder.services.api.FileException;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystems;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

/**
 *
 */
public class VFSFileServiceImpl {

    private static final String REPO_PLAYGROUND = "jgit:///playground";
    private static final String ORIGIN_URL = "https://github.com/guvnorngtestuser1/formbuilder-playground.git";
    private FileSystem fileSystem = null;

    public void checkFileSystem() {
        if (FileSystems.getFileSystem(URI.create(REPO_PLAYGROUND)) == null) {
            final String userName = "guvnorngtestuser1";
            final String password = "test1234";
            final URI fsURI = URI.create(REPO_PLAYGROUND);

            final Map<String, Object> env = new HashMap<String, Object>() {{
                put("username", userName);
                put("password", password);
                put("giturl", ORIGIN_URL);
            }};

            fileSystem = FileSystems.newFileSystem(fsURI, env);
        }
    }

    public byte[] loadFile(final Path file) throws FileException {
        if (file == null) {
            throw new IllegalArgumentException();
        }

        checkFileSystem();

        if (!file.getFileSystem().equals(fileSystem)) {
            throw new IllegalStateException( "file's fileSystem not supported.");
        }

        try {
            return Files.readAllBytes(file);
        } catch (IOException ex) {
            throw new FileException(ex.getMessage(), ex);
        }
    }

    public Iterable<Path> loadFilesByType(final String fileType) throws FileException {
        checkFileSystem();

        return Files.newDirectoryStream(Paths.get("default:///playground"), new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(final Path entry) throws IOException {
                if (entry.getFileName().toString().endsWith(fileType)) {
                    return true;
                }
                return false;
            }
        });
    }
}
