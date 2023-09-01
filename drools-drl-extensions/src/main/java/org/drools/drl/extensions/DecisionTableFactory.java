/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.drl.extensions;

import java.io.InputStream;
import java.util.List;

import org.kie.api.internal.utils.KieService;
import org.kie.api.io.Resource;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.io.ResourceFactory;

public class DecisionTableFactory {

    private static DecisionTableProvider provider = KieService.load(DecisionTableProvider.class);
    
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
