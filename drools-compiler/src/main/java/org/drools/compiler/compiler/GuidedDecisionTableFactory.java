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

import org.kie.internal.utils.ServiceRegistryImpl;

import java.io.IOException;
import java.io.InputStream;

public class GuidedDecisionTableFactory {

    private static final String PROVIDER_CLASS = "org.drools.workbench.models.guided.dtable.backend.GuidedDecisionTableProviderImpl";

    private static GuidedDecisionTableProvider provider;

    public static synchronized void setGuidedDecisionTableProvider(GuidedDecisionTableProvider provider) {
        GuidedDecisionTableFactory.provider = provider;
    }

    public static synchronized GuidedDecisionTableProvider getGuidedDecisionTableProvider() {
        if (provider == null) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault(GuidedDecisionTableProvider.class, PROVIDER_CLASS);
        setGuidedDecisionTableProvider(ServiceRegistryImpl.getInstance().get(GuidedDecisionTableProvider.class));
    }

    public static synchronized void loadProvider(ClassLoader cl) {
        if (provider == null) {
            try {
                provider = (GuidedDecisionTableProvider) Class.forName(PROVIDER_CLASS, true, cl).newInstance();
            } catch (Exception e) {
            }
        }
    }

}
