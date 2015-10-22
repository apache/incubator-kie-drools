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
 *
 */

package org.drools.compiler.compiler;

import org.kie.internal.utils.ServiceRegistryImpl;

public class GuidedRuleTemplateFactory {
    private static final String PROVIDER_CLASS = "org.drools.workbench.models.guided.template.backend.GuidedRuleTemplateProviderImpl";

    private static GuidedRuleTemplateProvider provider;

    public static synchronized void setGuidedRuleTemplateProvider(GuidedRuleTemplateProvider provider) {
        GuidedRuleTemplateFactory.provider = provider;
    }

    public static synchronized GuidedRuleTemplateProvider getGuidedRuleTemplateProvider() {
        if (provider == null) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault(GuidedRuleTemplateProvider.class, PROVIDER_CLASS);
        setGuidedRuleTemplateProvider(ServiceRegistryImpl.getInstance().get(GuidedRuleTemplateProvider.class));
    }

    public static synchronized void loadProvider(ClassLoader cl) {
        if (provider == null) {
            try {
                provider = (GuidedRuleTemplateProvider) Class.forName(PROVIDER_CLASS, true, cl).newInstance();
            } catch (Exception e) {
            }
        }
    }

}
