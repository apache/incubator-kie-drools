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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Test-only stand-in for {@code org.jboss.vfs.VFS}. NativeJavaCompiler discovers VFS via
 * {@code Class.forName("org.jboss.vfs.VFS")}, so placing this on the test classpath
 * lets us drive the VFS code path without depending on WildFly.
 *
 * <p>The real class exposes a static {@code getChild(URI)} method returning a
 * {@code VirtualFile} — that's the only reflective shape NativeJavaCompiler relies on.
 */
public final class VFS {

    private static final Map<URI, VirtualFile> REGISTRY = new HashMap<>();

    private VFS() {}

    public static void register(URI uri, VirtualFile virtualFile) {
        REGISTRY.put(uri, virtualFile);
    }

    public static void clear() {
        REGISTRY.clear();
    }

    public static VirtualFile getChild(URI uri) {
        return REGISTRY.get(uri);
    }
}
