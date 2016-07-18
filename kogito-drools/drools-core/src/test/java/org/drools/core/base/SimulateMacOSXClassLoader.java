/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SimulateMacOSXClassLoader extends ClassLoader {
    private ClassLoader wrappedRealClassLoader;
    private Set<Class<?>> forClasses = Collections.emptySet();

    /**
     * JVM classloader can't "scan" for available classes, hence class in scope of Mac/OSX simulation of this classloader must be added manually.
     * @param wrappedRealClassLoader a real classloader wrapped by this simulator.
     * @param forClasses collection of classes to be added in scope of this simulation.
     */
    public SimulateMacOSXClassLoader(final ClassLoader wrappedRealClassLoader,
                                     final Set<Class<?>> forClasses) {
        this.wrappedRealClassLoader = wrappedRealClassLoader;
        this.forClasses = forClasses;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> macOSXSimilar = null;

        // I know Java 8 API would be better but what about potential back-porting to 6.x branch?
        Iterator<Class<?>> iterator = forClasses.iterator();
        while(iterator.hasNext()) {
            Class<?> curElement = iterator.next();
            if (curElement.getName().equalsIgnoreCase(name)) {
                macOSXSimilar = curElement;
            }
        }

        Class<?> loaded = null;
        if (macOSXSimilar != null) {
            loaded = wrappedRealClassLoader.loadClass(macOSXSimilar.getName());
        } else {
            // not in scope of the Mac/OSX simulation, I pass down to the real classloader for the resolution directly.
            loaded = wrappedRealClassLoader.loadClass(name);
        }

        // complete Mac/OSX simulation by checking the name is the one requested.
        if (loaded.getName().equals(name)) {
            return loaded;
        } else {
            String resolvedPackage = loaded.getPackage().getName();
            String declaringClasses = "";
            Class<?> p = loaded.getDeclaringClass();
            while ( p != null ) {
                declaringClasses = p.getSimpleName() + "$" + declaringClasses;
                p = p.getDeclaringClass();
            }
            throw new NoClassDefFoundError( resolvedPackage.replace(".", "/") + "/" + name.replace(resolvedPackage+".", "")
                                            + " (wrong name: "
                                            + resolvedPackage.replace(".", "/") + "/" + declaringClasses + loaded.getSimpleName()
                                            + ")"
            );
        }

    }


    /**
     * JVM classloader can't "scan" for available classes, hence class in scope of Mac/OSX simulation of this classloader must be added manually.
     * @param clazz class to be added in scope of this simulation.
     */
    public void addClassInScope(Class<?> clazz) {
        if (this.forClasses == Collections.EMPTY_SET) {
            this.forClasses = new HashSet<Class<?>>();
        }
        this.forClasses.add(clazz);
    }

}

