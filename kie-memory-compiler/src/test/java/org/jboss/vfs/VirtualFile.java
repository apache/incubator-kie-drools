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
package org.jboss.vfs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Test-only stand-in for {@code org.jboss.vfs.VirtualFile}. Only the methods
 * NativeJavaCompiler invokes reflectively are exposed: {@code getChildren()},
 * {@code getName()}, {@code openStream()}.
 */
public final class VirtualFile {

    private final String name;
    private final byte[] content;
    private final List<VirtualFile> children;

    private VirtualFile(String name, byte[] content, List<VirtualFile> children) {
        this.name = name;
        this.content = content;
        this.children = children;
    }

    public static VirtualFile directory(String name, List<VirtualFile> children) {
        return new VirtualFile(name, null, List.copyOf(children));
    }

    public static VirtualFile file(String name, byte[] content) {
        return new VirtualFile(name, content.clone(), Collections.emptyList());
    }

    public String getName() {
        return name;
    }

    public List<VirtualFile> getChildren() {
        return children;
    }

    public InputStream openStream() {
        return new ByteArrayInputStream(content == null ? new byte[0] : content);
    }
}
