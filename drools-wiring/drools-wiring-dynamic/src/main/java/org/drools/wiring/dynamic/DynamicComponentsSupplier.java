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
package org.drools.wiring.dynamic;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Map;

import org.drools.wiring.api.ComponentsSupplier;
import org.drools.wiring.api.ResourceProvider;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.drools.wiring.api.util.ByteArrayClassLoader;
import org.drools.wiring.api.util.ClassUtils;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;

public class DynamicComponentsSupplier implements ComponentsSupplier {

    @Override
    public int servicePriority() {
        return 1;
    }

    @Override
    public ProjectClassLoader createProjectClassLoader(ClassLoader parent, ResourceProvider resourceProvider) {
        return DynamicProjectClassLoader.create(parent, resourceProvider);
    }

    @Override
    public ByteArrayClassLoader createByteArrayClassLoader(ClassLoader parent) {
        return ClassUtils.isAndroid() ? (ByteArrayClassLoader) ClassUtils.instantiateObject("org.drools.android.MultiDexClassLoader", null, parent) : new DefaultByteArrayClassLoader(parent);
    }

    @Override
    public ClassLoader createPackageClassLoader(Map<String, byte[]> store, ClassLoader rootClassLoader) {
        return ClassUtils.isAndroid() ?
                (ClassLoader) ClassUtils.instantiateObject("org.drools.android.DexPackageClassLoader", null, this, rootClassLoader) :
                new PackageClassLoader( store, rootClassLoader );
    }

    public static class DefaultByteArrayClassLoader extends ClassLoader implements ByteArrayClassLoader {

        public DefaultByteArrayClassLoader(final ClassLoader parent) {
            super(parent);
        }

        @Override
        public Class<?> defineClass(final String name,
                                    final byte[] bytes,
                                    final ProtectionDomain domain) {
            return defineClass(name,
                               bytes,
                               0,
                               bytes.length,
                               domain);
        }
    }

    @Override
    public Object createConsequenceExceptionHandler(String className, ClassLoader classLoader) {
        return ClassUtils.instantiateObject(className, classLoader);
    }


    private static class JaxbMethodHolder {
        private static final Method jaxbMethod = findJaxbMethod();

        private static Method findJaxbMethod() {
            String jaxbClassName = "org.drools.compiler.runtime.pipeline.impl.DroolsJaxbHelperProviderImpl";
            try {
                Class<?> jaxbClass = Class.forName( jaxbClassName );
                return jaxbClass.getMethod( "addPackageFromXSD", KnowledgeBuilder.class, Resource.class, ResourceConfiguration.class );
            } catch (NoClassDefFoundError e) {
                // There's no JAXB on classpath, it is safe to ignore the xsd file
                return null;
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException( e );
            }
        }
    }

    @Override
    public void addPackageFromXSD(KnowledgeBuilder kBuilder, Resource resource, ResourceConfiguration configuration) throws IOException {
        try {
            if (JaxbMethodHolder.jaxbMethod != null) {
                JaxbMethodHolder.jaxbMethod.invoke( null, kBuilder, resource, configuration );
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException( e );
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof IOException) {
                throw ( IOException ) e.getCause();
            }
            throw new RuntimeException( e );
        }
    }
}
