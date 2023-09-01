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
package org.kie.memorycompiler;

public interface WritableClassLoader {
    Class<?> writeClass(String name, byte[] bytecode);

    default ClassLoader asClassLoader() {
        return (ClassLoader) this;
    }

    static WritableClassLoader asWritableClassLoader(ClassLoader classLoader) {
        return classLoader instanceof WritableClassLoader ? ( WritableClassLoader ) classLoader : new WritableClassLoaderImpl( classLoader );
    }

    class WritableClassLoaderImpl extends ClassLoader implements WritableClassLoader {

        WritableClassLoaderImpl(ClassLoader classLoader) {
            super(classLoader);
        }

        public Class<?> writeClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }

        @Override
        public ClassLoader asClassLoader() {
            return getParent();
        }
    }
}
