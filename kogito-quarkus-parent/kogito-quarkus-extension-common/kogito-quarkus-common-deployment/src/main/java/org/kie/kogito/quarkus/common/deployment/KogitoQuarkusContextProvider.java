/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.quarkus.common.deployment;

import java.util.function.Predicate;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.api.utils.AppPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KogitoQuarkusContextProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoQuarkusContextProvider.class);

    private KogitoQuarkusContextProvider() {
        // utility class
    }

    public static KogitoBuildContext context(AppPaths appPaths, ClassLoader classLoader, Predicate<String> classAvailabilityResolver) {
        KogitoBuildContext context = QuarkusKogitoBuildContext.builder()
                .withApplicationProperties(appPaths.getResourceFiles())
                .withClassLoader(classLoader)
                .withClassAvailabilityResolver(classAvailabilityResolver)
                .withAppPaths(appPaths)
                .build();

        if (!context.hasClassAvailable(QuarkusKogitoBuildContext.QUARKUS_REST)) {
            LOGGER.info("Disabling REST generation because class '" + QuarkusKogitoBuildContext.QUARKUS_REST + "' is not available");
            context.setApplicationProperty(KogitoBuildContext.KOGITO_GENERATE_REST, "false");
        }
        if (!context.hasClassAvailable(QuarkusKogitoBuildContext.QUARKUS_DI)) {
            LOGGER.info("Disabling dependency injection generation because class '" + QuarkusKogitoBuildContext.QUARKUS_DI + "' is not available");
            context.setApplicationProperty(KogitoBuildContext.KOGITO_GENERATE_DI, "false");
        }

        return context;
    }

    private static boolean hasClassOnClasspath(ClassLoader cl, String className) {
        try {
            cl.loadClass(className);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
