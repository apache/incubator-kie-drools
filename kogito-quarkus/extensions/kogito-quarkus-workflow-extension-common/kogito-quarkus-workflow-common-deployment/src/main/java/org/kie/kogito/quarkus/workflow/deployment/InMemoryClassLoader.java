/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.quarkus.workflow.deployment;

import java.util.HashMap;
import java.util.Map;

public class InMemoryClassLoader extends ClassLoader {
    private Map<String, byte[]> classes = new HashMap<>();

    public InMemoryClassLoader(ClassLoader parent, Map<String, byte[]> classes) {
        super(parent);
        this.classes.putAll(classes);
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        byte[] byteClass = classes.remove(name);
        if (byteClass != null) {
            return defineClass(name, byteClass, 0, byteClass.length);
        }
        return super.findClass(name);
    }
}