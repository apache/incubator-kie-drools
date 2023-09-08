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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.security.ProtectionDomain;

import org.drools.wiring.api.ComponentsSupplier;
import org.drools.wiring.api.ResourceProvider;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.drools.wiring.api.util.ByteArrayClassLoader;
import org.kie.api.runtime.rule.ConsequenceExceptionHandler;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.RuleRuntime;

public class StaticComponentsSupplier implements ComponentsSupplier {

    public ProjectClassLoader createProjectClassLoader(ClassLoader parent, ResourceProvider resourceProvider) {
        return org.drools.wiring.statics.StaticProjectClassLoader.create(parent, resourceProvider);
    }

    @Override
    public ByteArrayClassLoader createByteArrayClassLoader(ClassLoader parent) {
        return new DummyByteArrayClassLoader();
    }

    public static class DummyByteArrayClassLoader implements ByteArrayClassLoader {
        @Override
        public Class<?> defineClass(final String name,
                final byte[] bytes,
                final ProtectionDomain domain) {
            throw new UnsupportedOperationException(
                    "The artifact org.drools:drools-wiring-static does not support this operation, try using org.drools:drools-wiring-dynamic instead.");
        }
    }

    @Override
    public Object createConsequenceExceptionHandler(String className, ClassLoader classLoader) {
        return new StaticConsequenceExceptionHandler();
    }

    public static class StaticConsequenceExceptionHandler implements ConsequenceExceptionHandler, Externalizable {

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
        }

        @Override
        public void handleException(Match activation,
                                    RuleRuntime workingMemory,
                                    Exception exception) {
            throw new org.kie.api.runtime.rule.ConsequenceException(exception, workingMemory, activation);
        }
    }
}
