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
package org.drools.codegen.common.context;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;

import javax.lang.model.SourceVersion;

import org.drools.codegen.common.AppPaths;
import org.drools.codegen.common.DroolsModelApplicationPropertyProvider;
import org.drools.codegen.common.DroolsModelBuildContext;
import org.drools.codegen.common.di.DependencyInjectionAnnotator;
import org.drools.codegen.common.rest.RestAnnotator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDroolsModelBuildContext implements DroolsModelBuildContext {

    public static final String DEFAULT_GROUP_ID = "org.kie.kogito";
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractDroolsModelBuildContext.class);

    protected final Predicate<String> classAvailabilityResolver;
    protected final DroolsModelApplicationPropertyProvider applicationProperties;
    protected final String packageName;
    protected final ClassLoader classLoader;
    protected final AppPaths appPaths;
    protected final String contextName;
    protected final Map<String, Object> contextAttributes;

    protected final DependencyInjectionAnnotator dependencyInjectionAnnotator;
    protected final RestAnnotator restAnnotator;

    protected AbstractDroolsModelBuildContext(AbstractBuilder builder,
                                              DependencyInjectionAnnotator dependencyInjectionAnnotator,
                                              RestAnnotator restAnnotator,
                                              String contextName) {
        this.dependencyInjectionAnnotator = dependencyInjectionAnnotator;
        this.restAnnotator = restAnnotator;
        this.packageName = builder.packageName;
        this.classAvailabilityResolver = builder.classAvailabilityResolver;
        this.applicationProperties = builder.applicationProperties;
        this.classLoader = builder.classLoader;
        this.appPaths = builder.appPaths;
        this.contextName = contextName;
        this.contextAttributes = new HashMap<>();
    }

    protected static Properties load(File... resourcePaths) {
        Properties applicationProperties = new Properties();

        for (File resourcePath : resourcePaths) {
            try (FileReader fileReader = new FileReader(new File(resourcePath, APPLICATION_PROPERTIES_FILE_NAME), StandardCharsets.UTF_8)) {
                applicationProperties.load(fileReader);
            } catch (IOException ioe) {
                LOGGER.debug("Unable to load '" + APPLICATION_PROPERTIES_FILE_NAME + "'.");
            }
        }

        return applicationProperties;
    }

    public boolean hasClassAvailable(String fqcn) {
        return classAvailabilityResolver.test(fqcn);
    }

    @Override
    public Optional<String> getApplicationProperty(String property) {
        return applicationProperties.getApplicationProperty(property);
    }

    @Override
    public Collection<String> getApplicationProperties() {
        return applicationProperties.getApplicationProperties();
    }

    @Override
    public void setApplicationProperty(String key, String value) {
        applicationProperties.setApplicationProperty(key, value);
    }

    @Override
    public String getPackageName() {
        return packageName;
    }


    @Override
    public String name() {
        return contextName;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public AppPaths getAppPaths() {
        return appPaths;
    }

    @Override
    public DependencyInjectionAnnotator getDependencyInjectionAnnotator() {
        return dependencyInjectionAnnotator;
    }

    @Override
    public RestAnnotator getRestAnnotator() {
        return restAnnotator;
    }

    @Override
    public String toString() {
        return "KogitoBuildContext{" +
                "contextName='" + contextName + '\'' +
                ", applicationProperties=" + applicationProperties +
                ", packageName='" + packageName + '\'' +
                '}';
    }

    protected abstract static class AbstractBuilder implements Builder {

        protected String packageName = DEFAULT_PACKAGE_NAME;
        protected DroolsModelApplicationPropertyProvider applicationProperties = DroolsModelApplicationPropertyProvider.of(new Properties());
        protected ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        protected Predicate<String> classAvailabilityResolver = this::hasClass;

        // default fallback value (usually overridden)
        protected AppPaths appPaths = AppPaths.fromProjectDir(new File(".").toPath());

        protected AbstractBuilder() {
        }

        @Override
        public Builder withPackageName(String packageName) {
            Objects.requireNonNull(packageName, "packageName cannot be null");
            if (!SourceVersion.isName(packageName)) {
                throw new IllegalArgumentException(
                        MessageFormat.format(
                                "Package name \"{0}\" is not valid. It should be a valid Java package name.", packageName));
            }

            // safe guard to not generate application classes that would clash with interfaces
            if (!packageName.equals(DEFAULT_GROUP_ID)) {
                this.packageName = packageName;
            } else {
                LOGGER.warn("Skipping the package provided because invalid: '{}' (current value '{}')", packageName, this.packageName);
            }
            return this;
        }

        @Override
        public Builder withApplicationPropertyProvider(DroolsModelApplicationPropertyProvider applicationProperties) {
            Objects.requireNonNull(applicationProperties, "applicationPropertiesProvider cannot be null");
            this.applicationProperties = applicationProperties;
            return this;
        }

        @Override
        public Builder withApplicationProperties(Properties applicationProperties) {
            Objects.requireNonNull(applicationProperties, "applicationProperties cannot be null");
            this.applicationProperties = DroolsModelApplicationPropertyProvider.of(applicationProperties);
            return this;
        }

        @Override
        public Builder withApplicationProperties(File... files) {
            this.applicationProperties = DroolsModelApplicationPropertyProvider.of(load(files));
            return this;
        }

        @Override
        public Builder withClassAvailabilityResolver(Predicate<String> classAvailabilityResolver) {
            Objects.requireNonNull(classAvailabilityResolver, "classAvailabilityResolver cannot be null");
            this.classAvailabilityResolver = classAvailabilityResolver;
            return this;
        }

        @Override
        public Builder withClassLoader(ClassLoader classLoader) {
            Objects.requireNonNull(classLoader, "classLoader cannot be null");
            this.classLoader = classLoader;
            return this;
        }

        @Override
        public Builder withAppPaths(AppPaths appPaths) {
            Objects.requireNonNull(appPaths, "appPaths cannot be null");
            this.appPaths = appPaths;
            return this;
        }

        private boolean hasClass(String className) {
            try {
                this.classLoader.loadClass(className);
                return true;
            } catch (ClassNotFoundException ex) {
                return false;
            }
        }
    }
}
