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

package org.drools.compiler.compiler;

import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.utils.ServiceRegistryImpl;


public class ProcessBuilderFactory {

    private static final String PROVIDER_CLASS = "org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl";

    private static IllegalArgumentException initializationException;
    private static ProcessBuilderFactoryService provider;

    public static ProcessBuilder newProcessBuilder(KnowledgeBuilder kBuilder) {
        return getProcessBuilderFactoryService().newProcessBuilder(kBuilder);
    }

    public static synchronized void setProcessBuilderFactoryService(ProcessBuilderFactoryService provider) {
        ProcessBuilderFactory.provider = provider;
    }

    public static synchronized ProcessBuilderFactoryService getProcessBuilderFactoryService() {
        if (provider == null && !initialized()) {
            try {
                loadProvider();
            } catch (IllegalArgumentException e) {
                initializationException = e;
            }
        }
        if (initializationException != null) {
            // KnowledgeBuilderImpl expects an exception to report the origin of the failure
            throw initializationException;
        }
        return provider;
    }

    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( ProcessBuilderFactoryService.class, PROVIDER_CLASS );
        setProcessBuilderFactoryService(ServiceRegistryImpl.getInstance().get( ProcessBuilderFactoryService.class ) );
    }


    public static synchronized void loadProvider(ClassLoader cl) {
        if (provider == null) {
            try {
                provider = (ProcessBuilderFactoryService)Class.forName(PROVIDER_CLASS, true, cl).newInstance();
            } catch (Exception e) { }
        }
    }

    private static boolean initialized() {
        return initializationException != null;
    }

    public static synchronized void resetInitialization() {
        initializationException = null;
    }
}
