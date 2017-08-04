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
import org.kie.internal.builder.KnowledgeBuilder;


public class ProcessBuilderFactory {

    private static ProcessBuilderFactoryService provider = initializeProvider();

    public static ProcessBuilder newProcessBuilder(KnowledgeBuilder kBuilder) {
        if (provider == null) {
            return null;
        } else {
            return provider.newProcessBuilder(kBuilder);
        }
    }

    /**
     * This method is for test utility only.
     */
    public static void setProcessBuilderFactoryService(ProcessBuilderFactoryService provider) {
        ProcessBuilderFactory.provider = provider;
    }

    private static ProcessBuilderFactoryService initializeProvider() {
        return ServiceRegistry.getInstance().get( ProcessBuilderFactoryService.class );
    }
}
