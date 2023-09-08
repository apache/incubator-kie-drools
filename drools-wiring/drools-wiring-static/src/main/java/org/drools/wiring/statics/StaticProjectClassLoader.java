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
package org.drools.wiring.statics;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import org.drools.wiring.api.ResourceProvider;
import org.drools.wiring.api.classloader.ProjectClassLoader;

public class StaticProjectClassLoader extends ProjectClassLoader {

    private static boolean isIBM_JVM = System.getProperty("java.vendor").toLowerCase().contains("ibm");

    protected StaticProjectClassLoader(ClassLoader parent, ResourceProvider resourceProvider) {
        super(parent, resourceProvider);
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    public static class IBMStaticClassLoader extends StaticProjectClassLoader {
        private final boolean parentImplementsFindResources;

        private static final Enumeration<URL> EMPTY_RESOURCE_ENUM = new Vector<URL>().elements();

        private IBMStaticClassLoader(ClassLoader parent, ResourceProvider resourceProvider) {
            super(parent, resourceProvider);
            Method m = null;
            try {
                m = parent.getClass().getMethod("findResources", String.class);
            } catch (NoSuchMethodException e) {
            }
            parentImplementsFindResources = m != null && m.getDeclaringClass() == parent.getClass();
        }

        @Override
        protected Enumeration<URL> findResources(String name) throws IOException {
            // if the parent doesn't implemnt this method call getResources directly on it
            // see https://blogs.oracle.com/bhaktimehta/entry/ibm_jdk_and_classloader_getresources
            return parentImplementsFindResources ? EMPTY_RESOURCE_ENUM : getParent().getResources(name);
        }
    }

    public static StaticProjectClassLoader create(ClassLoader parent, ResourceProvider resourceProvider) {
        return isIBM_JVM ? new IBMStaticClassLoader(parent, resourceProvider) : new StaticProjectClassLoader(parent, resourceProvider);
    }

    @Override
    public InternalTypesClassLoader makeClassLoader() {
        return new DummyInternalTypesClassLoader(this);
    }

    private static class DummyInternalTypesClassLoader extends ClassLoader implements InternalTypesClassLoader {

        private final ProjectClassLoader projectClassLoader;

        private DummyInternalTypesClassLoader(ProjectClassLoader projectClassLoader) {
            super(projectClassLoader.getParent());
            this.projectClassLoader = projectClassLoader;
        }

        public Class<?> defineClass(String name, byte[] bytecode) {
            throw new UnsupportedOperationException("You're trying to dynamically define a class, please add the module org.drools:drools-wiring-dynamic to your classpath.");
        }

        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            try {
                return loadType(name, resolve);
            } catch (ClassNotFoundException cnfe) {
                return projectClassLoader.internalLoadClass(name, resolve);
            }
        }

        public Class<?> loadType(String name, boolean resolve) throws ClassNotFoundException {
            return super.loadClass(name, resolve);
        }

        @Override
        public URL getResource(String name) {
            return projectClassLoader.getResource(name);
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            return projectClassLoader.getResourceAsStream(name);
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            return projectClassLoader.getResources(name);
        }
    }
}
