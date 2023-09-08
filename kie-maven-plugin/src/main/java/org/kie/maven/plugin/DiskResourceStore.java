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
package org.kie.maven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
        File file = new File(fullPath);
        if (createFolder) {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(pResourceData);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private byte[] commonRead(String fullPath) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fullPath);
            return readBytesFromInputStream(fis);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void commonRemove(String fullPath) {
        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
        }
    }

    private String getFilePath(String pResourceName) {
        return root.getAbsolutePath() + File.separator + pResourceName;
    }
}
