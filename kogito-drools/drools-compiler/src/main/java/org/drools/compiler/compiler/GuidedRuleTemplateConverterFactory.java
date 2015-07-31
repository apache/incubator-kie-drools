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

public class GuidedRuleTemplateConverterFactory {
    private static final String PROVIDER_CLASS = "org.drools.workbench.models.guided.template.backend.GuidedRuleTemplateConverterImpl";

    private static GuidedRuleTemplateConverter provider;

    public static synchronized void setGuidedRuleTemplateConverter(GuidedRuleTemplateConverter provider) {
        GuidedRuleTemplateConverterFactory.provider = provider;
    }

    public static synchronized GuidedRuleTemplateConverter getGuidedRuleTemplateConverter() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( GuidedRuleTemplateConverter.class, PROVIDER_CLASS  );
        setGuidedRuleTemplateConverter(ServiceRegistryImpl.getInstance().get( GuidedRuleTemplateConverter.class ) );
    }
}
