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

import org.kie.api.io.Resource;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.utils.ServiceRegistryImpl;

import java.io.InputStream;
import java.util.List;

public class DecisionTableFactory {

    private static final String PROVIDER_CLASS = "org.drools.decisiontable.DecisionTableProviderImpl";

    private static DecisionTableProvider provider;
    
    public static String loadFromInputStream(InputStream is, DecisionTableConfiguration configuration) {
        return getDecisionTableProvider().loadFromInputStream( is, configuration );
    }

    public static List<String> loadFromInputStreamWithTemplates(Resource resource, DecisionTableConfiguration configuration) {
        return getDecisionTableProvider().loadFromInputStreamWithTemplates( resource, configuration );
    }

    public static synchronized void setDecisionTableProvider(DecisionTableProvider provider) {
        DecisionTableFactory.provider = provider;
    }
    
    public static synchronized DecisionTableProvider getDecisionTableProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }
    
    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( DecisionTableProvider.class, PROVIDER_CLASS );
        setDecisionTableProvider(ServiceRegistryImpl.getInstance().get( DecisionTableProvider.class ) );
    }

    public static synchronized void loadProvider(ClassLoader cl) {
        if (provider == null) {
            try {
                provider = (DecisionTableProvider)Class.forName(PROVIDER_CLASS, true, cl).newInstance();
            } catch (Exception e) { }
        }
    }
}
