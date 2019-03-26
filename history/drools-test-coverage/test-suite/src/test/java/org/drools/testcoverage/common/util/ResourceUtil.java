/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.common.util;

import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;

/**
 * Utility class for handling resources.
 */
public final class ResourceUtil {

    public static Resource getDecisionTableResourceFromClasspath(final String resourceName,
                                                                 final Class classLoaderFromClass,
                                                                 final DecisionTableInputType type) {
        final Resource dtable =
                KieServices.Factory.get().getResources().newClassPathResource(resourceName, classLoaderFromClass);
        final DecisionTableConfiguration resourceConfig = new DecisionTableConfigurationImpl();
        resourceConfig.setInputType(type);
        dtable.setConfiguration(resourceConfig);
        return dtable;
    }

    private ResourceUtil() {
        // Creating instances of util classes should not be possible.
    }
}
