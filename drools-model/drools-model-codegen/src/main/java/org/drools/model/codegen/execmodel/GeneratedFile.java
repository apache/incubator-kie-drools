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
package org.drools.model.codegen.execmodel;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.drools.util.PortablePath;

public class GeneratedFile {

    public enum Type {
        APPLICATION,
        PROCESS,
        PROCESS_INSTANCE,
        REST,
        RULE,
        DECLARED_TYPE,
        QUERY,
        MODEL,
        CLASS,
        MESSAGE_CONSUMER,
        MESSAGE_PRODUCER,
        PMML;
    }

    private final PortablePath path;
    private final byte[] data;
    private final Type type;

    public GeneratedFile(String path, String data) {
        this(Type.RULE, path, data);
    }

    public GeneratedFile(String path, byte[] data) {
        this(Type.RULE, path, data);
    }

    public GeneratedFile(Type type, String path, String data) {
        this(type, path, data.getBytes(StandardCharsets.UTF_8));
    }

    private GeneratedFile(Type type, String path, byte[] data) {
        this.type = type;
        this.path = PortablePath.of(path);
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public String getPath() {
        return path.asString();
    }

    public PortablePath getKiePath() {
        return path;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "GeneratedFile{" +
                "path='" + path.asString() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeneratedFile that = (GeneratedFile) o;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
