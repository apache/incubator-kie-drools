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

import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.api.io.Resource;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.internal.utils.ServiceRegistryImpl;

import java.io.InputStream;
import java.util.List;

public class DecisionTableFactory {

    private static DecisionTableProvider provider = ServiceRegistry.getInstance().get(DecisionTableProvider.class);
    
    public static String loadFromInputStream(InputStream is, DecisionTableConfiguration configuration ) {
        return loadFromResource(ResourceFactory.newInputStreamResource( is ), configuration);
    }

    public static String loadFromResource(Resource resource, DecisionTableConfiguration configuration) {
        return getDecisionTableProvider().loadFromResource( resource, configuration );
    }

    public static List<String> loadFromInputStreamWithTemplates(Resource resource, DecisionTableConfiguration configuration) {
        return getDecisionTableProvider().loadFromInputStreamWithTemplates( resource, configuration );
    }

    public static synchronized void setDecisionTableProvider(DecisionTableProvider provider) {
        DecisionTableFactory.provider = provider;
    }
    
    public static synchronized DecisionTableProvider getDecisionTableProvider() {
        return provider;
    }
}
