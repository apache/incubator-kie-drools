/*
 * Copyright 2015 JBoss Inc
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


public class BPMN2ProcessFactory {

    private static final String PROVIDER_CLASS = "org.jbpm.bpmn2.BPMN2ProcessProviderImpl";

    private static BPMN2ProcessProvider provider;

    public static void configurePackageBuilder(KnowledgeBuilder kBuilder) {
        getBPMN2ProcessProvider().configurePackageBuilder(kBuilder);
    }

    public static synchronized void setBPMN2ProcessProvider(BPMN2ProcessProvider provider) {
        BPMN2ProcessFactory.provider = provider;
    }

    public static synchronized BPMN2ProcessProvider getBPMN2ProcessProvider() {
        if (provider == null) {
            loadProvider();
        }
        return provider;
    }

    @SuppressWarnings("unchecked")
    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( BPMN2ProcessProvider.class, PROVIDER_CLASS );
        setBPMN2ProcessProvider(ServiceRegistryImpl.getInstance().get( BPMN2ProcessProvider.class ) );
    }

    public static synchronized void loadProvider(ClassLoader cl) {
        if (provider == null) {
            try {
                provider = (BPMN2ProcessProvider)Class.forName(PROVIDER_CLASS, true, cl).newInstance();
            } catch (Exception e) { }
        }
    }
}
