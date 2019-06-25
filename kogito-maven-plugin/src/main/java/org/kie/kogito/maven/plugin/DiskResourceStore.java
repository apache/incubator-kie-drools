/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.kogito.maven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

import org.drools.compiler.commons.jci.stores.ResourceStore;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;

public class DiskResourceStore implements ResourceStore {
    private final File root;

    public DiskResourceStore(File root) {
        this.root = root;
    }

    @Override
    public void write(String pResourceName, byte[] pResourceData) {
        write(pResourceName, pResourceData, false);
    }

    @Override
    public void write(String pResourceName, byte[] pResourceData, boolean createFolder) {
        File file = new File(getFilePath(pResourceName));
        if (createFolder) {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(pResourceData);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public byte[] read(String pResourceName) {
        try (FileInputStream fis = new FileInputStream(getFilePath(pResourceName))) {
            return readBytesFromInputStream(fis);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void remove(String pResourceName) {
        File file = new File(getFilePath(pResourceName));
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String getFilePath(String pResourceName) {
        return root.getAbsolutePath() + File.separator + pResourceName;
    }
}
