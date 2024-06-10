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
package org.drools.codegen.common;

import java.io.File;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;

import org.drools.codegen.common.di.DependencyInjectionAnnotator;
import org.drools.codegen.common.rest.RestAnnotator;

public interface DroolsModelBuildContext {

    String APPLICATION_PROPERTIES_FILE_NAME = "application.properties";
    String DEFAULT_PACKAGE_NAME = "org.kie.kogito.app";
    String KOGITO_GENERATE_REST = "kogito.generate.rest";
    String KOGITO_GENERATE_DI = "kogito.generate.di";

    Optional<String> getApplicationProperty(String property);

    Collection<String> getApplicationProperties();

    void setApplicationProperty(String key, String value);

    String getPackageName();

    ClassLoader getClassLoader();

    AppPaths getAppPaths();

    String name();

    DependencyInjectionAnnotator getDependencyInjectionAnnotator();

    RestAnnotator getRestAnnotator();

    boolean hasRest();

    boolean hasDI();

    default boolean hasJackson() {
        return hasClassAvailable("com.fasterxml.jackson.core.JsonParser");
    }

    default boolean hasJacksonDatabind() {
        return hasClassAvailable("com.fasterxml.jackson.databind.ObjectMapper");
    }

    default boolean hasClassAvailable(String fqcn) {
        try {
            getClassLoader().loadClass(fqcn);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    interface Builder {

        Builder withPackageName(String packageName);

        Builder withApplicationPropertyProvider(DroolsModelApplicationPropertyProvider applicationProperties);

        Builder withApplicationProperties(Properties applicationProperties);

        Builder withApplicationProperties(File... files);

        Builder withClassAvailabilityResolver(Predicate<String> classAvailabilityResolver);

        Builder withClassLoader(ClassLoader classLoader);

        Builder withAppPaths(AppPaths appPaths);

        DroolsModelBuildContext build();
    }
}
