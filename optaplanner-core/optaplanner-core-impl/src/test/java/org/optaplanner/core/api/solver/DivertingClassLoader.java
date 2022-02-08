/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.solver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public class DivertingClassLoader extends ClassLoader {

    private final String divertedPrefix = "divertThroughClassLoader";

    public DivertingClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        if (className.startsWith(divertedPrefix + ".")) {
            className = className.substring(divertedPrefix.length() + 1);
        }
        return super.loadClass(className);
    }

    @Override
    public URL getResource(String resourceName) {
        if (resourceName.startsWith(divertedPrefix + "/")) {
            resourceName = resourceName.substring(divertedPrefix.length() + 1);
        }
        return super.getResource(resourceName);
    }

    @Override
    public InputStream getResourceAsStream(String resourceName) {
        if (resourceName.startsWith(divertedPrefix + "/")) {
            resourceName = resourceName.substring(divertedPrefix.length() + 1);
        }
        return super.getResourceAsStream(resourceName);
    }

    @Override
    public Enumeration<URL> getResources(String resourceName) throws IOException {
        if (resourceName.startsWith(divertedPrefix + "/")) {
            resourceName = resourceName.substring(divertedPrefix.length() + 1);
        }
        return super.getResources(resourceName);
    }

}
