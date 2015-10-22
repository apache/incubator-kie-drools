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

import org.kie.internal.utils.ServiceRegistryImpl;

import java.io.IOException;
import java.io.InputStream;

public class GuidedScoreCardFactory {

    private static final String PROVIDER_CLASS = "org.drools.workbench.models.guided.scorecard.backend.GuidedScoreCardProviderImpl";

    private static GuidedScoreCardProvider provider;

    public static String loadFromInputStream(InputStream is) throws IOException {
        return getGuidedScoreCardProvider().loadFromInputStream(is);
    }

    public static synchronized void setGuidedScoreCardProvider(GuidedScoreCardProvider provider) {
        GuidedScoreCardFactory.provider = provider;
    }

    public static synchronized GuidedScoreCardProvider getGuidedScoreCardProvider() {
        if (provider == null) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault(GuidedScoreCardProvider.class, PROVIDER_CLASS);
        setGuidedScoreCardProvider(ServiceRegistryImpl.getInstance().get(GuidedScoreCardProvider.class));
    }

    public static synchronized void loadProvider(ClassLoader cl) {
        if (provider == null) {
            try {
                provider = (GuidedScoreCardProvider) Class.forName(PROVIDER_CLASS, true, cl).newInstance();
            } catch (Exception e) {
            }
        }
    }

}
