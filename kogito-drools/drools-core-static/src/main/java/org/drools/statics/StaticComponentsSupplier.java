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

package org.drools.statics;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.security.ProtectionDomain;

import org.drools.reflective.ComponentsSupplier;
import org.drools.reflective.ResourceProvider;
import org.drools.reflective.classloader.ProjectClassLoader;
import org.drools.reflective.util.ByteArrayClassLoader;
import org.kie.api.runtime.rule.ConsequenceExceptionHandler;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.RuleRuntime;

public class StaticComponentsSupplier implements ComponentsSupplier {

    public ProjectClassLoader createProjectClassLoader( ClassLoader parent, ResourceProvider resourceProvider) {
        return StaticProjectClassLoader.create(parent, resourceProvider);
    }

    @Override
    public ByteArrayClassLoader createByteArrayClassLoader( ClassLoader parent ) {
        return new DummyByteArrayClassLoader();
    }

    public static class DummyByteArrayClassLoader implements ByteArrayClassLoader {
        @Override
        public Class< ? > defineClass(final String name,
                                      final byte[] bytes,
                                      final ProtectionDomain domain) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Object createConsequenceExceptionHandler( String className, ClassLoader classLoader ) {
        return new StaticConsequenceExceptionHandler();
    }

    @Override
    public Object createTimerService( String className ) {
        return StaticServiceRegistry.INSTANCE.newInstance( "TimerService" );
    }

    public static class StaticConsequenceExceptionHandler implements ConsequenceExceptionHandler, Externalizable {

        @Override
        public void readExternal( ObjectInput in) throws IOException, ClassNotFoundException { }

        @Override
        public void writeExternal( ObjectOutput out) throws IOException { }

        @Override
        public void handleException( Match activation,
                                     RuleRuntime workingMemory,
                                     Exception exception) {
            throw new org.kie.api.runtime.rule.ConsequenceException(exception, workingMemory, activation );
        }
    }
}
