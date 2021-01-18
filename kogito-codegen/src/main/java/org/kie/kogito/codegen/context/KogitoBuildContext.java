/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.context;

import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.KogitoCodeGenConstants;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;

public interface KogitoBuildContext {

    String APPLICATION_PROPERTIES_FILE_NAME = "application.properties";
    String DEFAULT_PACKAGE_NAME = "org.kie.kogito.app";
    KogitoBuildContext EMPTY_CONTEXT = JavaKogitoBuildContext.builder()
            .build();

    boolean hasClassAvailable(String fqcn);

    /**
     * Return DependencyInjectionAnnotator if available or null
     *
     * @return
     */
    DependencyInjectionAnnotator getDependencyInjectionAnnotator();

    /**
     * Method to override default dependency injection annotator
     *
     * @param dependencyInjectionAnnotator
     * @return
     */
    void setDependencyInjectionAnnotator(DependencyInjectionAnnotator dependencyInjectionAnnotator);

    default boolean hasDI() {
        return getDependencyInjectionAnnotator() != null;
    }

    default boolean isValidationSupported() {
        return hasClassAvailable(KogitoCodeGenConstants.VALIDATION_CLASS);
    }

    Optional<String> getApplicationProperty(String property);

    Collection<String> getApplicationProperties();

    default Path getProjectDirectory() {
        return getTargetDirectory().getParentFile().toPath();
    }

    File getTargetDirectory();

    String getPackageName();

    AddonsConfig getAddonsConfig();

    /**
     * Name of the context (e.g. Quarkus, Spring) used to identify a context and for template naming conventions
     * (see {@link org.kie.kogito.codegen.TemplatedGenerator})
     * @return
     */
    String name();

    interface Builder {
        Builder withPackageName(String packageName);

        Builder withApplicationProperties(Properties applicationProperties);

        Builder withApplicationProperties(File... files);

        Builder withAddonsConfig(AddonsConfig addonsConfig);

        Builder withClassAvailabilityResolver(Predicate<String> classAvailabilityResolver);

        Builder withTargetDirectory(File targetDirectory);

        KogitoBuildContext build();

        @SuppressWarnings("unchecked")
        static <T extends Builder> T merge(KogitoBuildContext original, T target) {
            Properties newProperties = new Properties();
            original.getApplicationProperties().forEach(prop -> newProperties.put(prop, original.getApplicationProperty(prop)));

            return (T) target.withPackageName(original.getPackageName())
                    .withApplicationProperties(newProperties)
                    .withAddonsConfig(original.getAddonsConfig())
                    .withTargetDirectory(original.getTargetDirectory())
                    .withClassAvailabilityResolver(original::hasClassAvailable);
        }


    }
}
