/*
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
package org.kie.maven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.drools.util.PortablePath;
import org.kie.memorycompiler.resources.ResourceStore;

import static org.drools.util.IoUtils.readBytesFromInputStream;

public class DiskResourceStore implements ResourceStore {

    private final File root;

    public DiskResourceStore(File root) {
        this.root = root;
    }

    @Override
    public void write(PortablePath resourcePath, byte[] pResourceData) {
        write(resourcePath, pResourceData, false);
    }

    @Override
    public void write(String pResourceName, byte[] pResourceData) {
        write(pResourceName, pResourceData, false);
    }

    @Override
    public void write(PortablePath resourcePath, byte[] pResourceData, boolean createFolder) {
        commonWrite(getFilePath(resourcePath.asString()), pResourceData, createFolder);
    }

    @Override
    public void write(String pResourceName, byte[] pResourceData, boolean createFolder) {
        commonWrite(getFilePath(pResourceName), pResourceData, createFolder);
    }

    @Override
    public byte[] read(PortablePath resourcePath) {
        return commonRead(getFilePath(resourcePath.asString()));
    }

    @Override
    public byte[] read(String pResourceName) {
        return commonRead(getFilePath(pResourceName));
    }

    @Override
    public void remove(PortablePath resourcePath) {
        commonRemove(getFilePath(resourcePath.asString()));
    }

    @Override
    public void remove(String pResourceName) {
        commonRemove(getFilePath(pResourceName));
    }

    private void commonWrite(String fullPath, byte[] pResourceData, boolean createFolder) {
        try {
            final Path path = Paths.get(fullPath).normalize();
            if (createFolder) {
                final Path parentPath = path.getParent();
                if (parentPath != null) {
                    parentPath.toFile().mkdirs();
                }
            }
            Files.write(Paths.get(fullPath).normalize(), pResourceData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] commonRead(String fullPath) {
        try {
            return Files.readAllBytes(Paths.get(fullPath).normalize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void commonRemove(String fullPath) {
        try {
            Files.deleteIfExists(Paths.get(fullPath).normalize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFilePath(String pResourceName) {
        return root.getAbsolutePath() + File.separator + pResourceName;
    }
}
