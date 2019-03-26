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

package org.drools.statics.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.internal.utils.ServiceRegistry;

import static org.drools.statics.common.SimpleInstanceCreator.constructor;
import static org.drools.statics.common.SimpleInstanceCreator.instance;

public class StaticServiceRegistry implements ServiceRegistry {

    static final StaticServiceRegistry INSTANCE = new StaticServiceRegistry();

    private Map<Class<?>, Object> serviceMap = new HashMap<>();

    private Map<String, Constructor<?>> constructorMap = new HashMap<>();

    StaticServiceRegistry() {
        wireServices();
    }

    private void wireServices() {
        serviceMap.put( org.kie.api.io.KieResources.class, instance("org.drools.core.io.impl.ResourceFactoryServiceImpl") );
        serviceMap.put( org.kie.api.marshalling.KieMarshallers.class, instance("org.drools.core.marshalling.impl.MarshallerProviderImpl") );
        serviceMap.put( org.kie.api.concurrent.KieExecutors.class, instance("org.drools.core.concurrent.ExecutorProviderImpl") );
        serviceMap.put( org.kie.api.KieServices.class, instance("org.drools.compiler.kie.builder.impl.KieServicesImpl") );
        serviceMap.put( org.kie.internal.builder.KnowledgeBuilderFactoryService.class, instance("org.drools.compiler.builder.impl.KnowledgeBuilderFactoryServiceImpl") );

        try {
            serviceMap.put( Class.forName( "org.drools.compiler.kie.builder.impl.InternalKieModuleProvider" ), instance("org.drools.modelcompiler.CanonicalKieModuleProvider") );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }

        constructorMap.put( "TimerService", constructor("org.drools.core.time.impl.JDKTimerService") );
    }

    @Override
    public <T> T get( Class<T> cls ) {
        return (T) serviceMap.get(cls);
    }

    public <T> T newInstance( String name ) {
        try {
            return (T) constructorMap.get(name).newInstance();
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException( e );
        }
    }
}
