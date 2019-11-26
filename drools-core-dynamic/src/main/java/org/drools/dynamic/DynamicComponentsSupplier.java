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

package org.drools.dynamic;

import java.security.ProtectionDomain;

import org.drools.reflective.ComponentsSupplier;
import org.drools.reflective.ResourceProvider;
import org.drools.reflective.classloader.ProjectClassLoader;
import org.drools.reflective.util.ByteArrayClassLoader;
import org.drools.reflective.util.ClassUtils;

public class DynamicComponentsSupplier implements ComponentsSupplier {

    @Override
    public ProjectClassLoader createProjectClassLoader(ClassLoader parent, ResourceProvider resourceProvider) {
        return DynamicProjectClassLoader.create(parent, resourceProvider);
    }

    @Override
    public ByteArrayClassLoader createByteArrayClassLoader(ClassLoader parent) {
        return ClassUtils.isAndroid() ? (ByteArrayClassLoader) ClassUtils.instantiateObject("org.drools.android.MultiDexClassLoader", null, parent) : new DefaultByteArrayClassLoader(parent);
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

    @Override
    public Object createTimerService(String className) {
        return ClassUtils.instantiateObject(className);
    }
}
