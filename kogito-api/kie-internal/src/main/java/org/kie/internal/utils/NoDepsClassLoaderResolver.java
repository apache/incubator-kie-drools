/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.internal.utils;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

import org.kie.api.builder.KieModule;


public class NoDepsClassLoaderResolver implements ClassLoaderResolver {
    private static final ProtectionDomain  PROTECTION_DOMAIN;

    static {
        PROTECTION_DOMAIN = (ProtectionDomain) AccessController.doPrivileged( new PrivilegedAction() {

            public Object run() {
                return NoDepsClassLoaderResolver.class.getProtectionDomain();
            }
        } );
    }


    @Override
    public ClassLoader getClassLoader(KieModule kmodule) {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }
        if (parent == null) {
            parent = NoDepsClassLoaderResolver.class.getClassLoader();
        }
        return parent;
    }

}
