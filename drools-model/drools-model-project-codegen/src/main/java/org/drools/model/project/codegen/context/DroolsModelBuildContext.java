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
package org.drools.model.project.codegen.context;

import java.io.File;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;

import org.drools.model.project.codegen.template.TemplatedGenerator;

public interface DroolsModelBuildContext {

    String APPLICATION_PROPERTIES_FILE_NAME = "application.properties";
    String DEFAULT_PACKAGE_NAME = "org.kie.kogito.app";
    String KOGITO_GENERATE_REST = "kogito.generate.rest";
    String KOGITO_GENERATE_DI = "kogito.generate.di";

    /**
     * Method to check if dependency injection is available and enabled.
     * This is platform/classpath specific (e.g. Quarkus) but it can also be explicitly disabled using
     * kogito.generate.di property
     */
    default boolean hasDI() {
        return false;
    }

    Optional<String> getApplicationProperty(String property);

    Collection<String> getApplicationProperties();

    void setApplicationProperty(String key, String value);

    String getPackageName();

    ClassLoader getClassLoader();

    AppPaths getAppPaths();

    /**
     * Name of the context (e.g. Quarkus, Spring) used to identify a context and for template naming conventions
     * (see {@link TemplatedGenerator})
     */
    String name();

    interface Builder {

        Builder withPackageName(String packageName);

        Builder withApplicationPropertyProvider(DroolsModelApplicationPropertyProvider applicationProperties);

        Builder withApplicationProperties(Properties applicationProperties);

        Builder withApplicationProperties(File... files);

//        Builder withAddonsConfig(AddonsConfig addonsConfig);

        Builder withClassAvailabilityResolver(Predicate<String> classAvailabilityResolver);

        Builder withClassLoader(ClassLoader classLoader);

        Builder withAppPaths(AppPaths appPaths);

//        Builder withGAV(KogitoGAV gav);

        DroolsModelBuildContext build();
    }
}
