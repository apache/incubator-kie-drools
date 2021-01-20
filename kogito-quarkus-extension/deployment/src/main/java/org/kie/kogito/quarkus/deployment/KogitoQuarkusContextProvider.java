/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.quarkus.deployment;

import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.utils.AppPaths;

import java.util.function.Predicate;

public class KogitoQuarkusContextProvider {

    private KogitoQuarkusContextProvider() {
        // utility class
    }

    public static KogitoBuildContext context(AppPaths appPaths, ClassLoader classLoader) {
        return context(appPaths, classLoader, className -> hasClassOnClasspath(classLoader, className));
    }

    public static KogitoBuildContext context(AppPaths appPaths, ClassLoader classLoader, Predicate<String> classAvailabilityResolver) {
        return QuarkusKogitoBuildContext.builder()
                .withApplicationProperties(appPaths.getResourceFiles())
                .withClassLoader(classLoader)
                .withClassAvailabilityResolver(classAvailabilityResolver)
                .withAppPaths(appPaths)
                .build();
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
