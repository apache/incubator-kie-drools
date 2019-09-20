/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder;

import java.nio.charset.StandardCharsets;

public class GeneratedFile {

    private final String path;
    private final byte[] data;

    public GeneratedFile(String path, String data) {
        this.path = path;
        this.data = data.getBytes( StandardCharsets.UTF_8);
    }

    private GeneratedFile(String path, byte[] data) {
        this.path = path;
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "GeneratedFile{" +
                "path='" + path + '\'' +
                '}';
    }
}
