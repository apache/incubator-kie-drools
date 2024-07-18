/*
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
package org.kie.kogito.codegen.api.context.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;

import javax.lang.model.SourceVersion;

import org.drools.codegen.common.AppPaths;
import org.drools.codegen.common.di.DependencyInjectionAnnotator;
import org.drools.codegen.common.rest.RestAnnotator;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.codegen.api.AddonsConfig;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.SourceFileCodegenBindNotifier;
import org.kie.kogito.codegen.api.context.KogitoApplicationPropertyProvider;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.utils.AddonsConfigDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractKogitoBuildContext implements KogitoBuildContext {

    public static final String DEFAULT_GROUP_ID = "org.kie.kogito";
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractKogitoBuildContext.class);

    protected final Predicate<String> classAvailabilityResolver;
    protected final Predicate<Class<?>> classSubTypeAvailabilityResolver;
    protected final KogitoApplicationPropertyProvider applicationProperties;
    protected final String packageName;
    protected final AddonsConfig addonsConfig;
    protected final ClassLoader classLoader;
    protected final AppPaths appPaths;
    protected final String contextName;
    protected final Map<String, Object> contextAttributes;
    protected final KogitoGAV gav;
    protected final SourceFileCodegenBindNotifier sourceFileCodegenBindNotifier;
    protected Set<ApplicationSection> applicationSections;
    protected Collection<String> appHandlers;

    protected DependencyInjectionAnnotator dependencyInjectionAnnotator;
    protected RestAnnotator restAnnotator;

    protected AbstractKogitoBuildContext(AbstractBuilder builder,
            DependencyInjectionAnnotator dependencyInjectionAnnotator,
            RestAnnotator restAnnotator,
            String contextName) {
        this.packageName = builder.packageName;
        this.classAvailabilityResolver = builder.classAvailabilityResolver;
        this.classSubTypeAvailabilityResolver = builder.classSubTypeAvailabilityResolver;
        this.dependencyInjectionAnnotator = dependencyInjectionAnnotator;
        this.restAnnotator = restAnnotator;
        this.applicationProperties = builder.applicationProperties;
        this.classLoader = builder.classLoader;
        this.addonsConfig = builder.addonsConfig != null ? builder.addonsConfig : AddonsConfigDiscovery.discover(this);
        this.appPaths = builder.appPaths;
        this.gav = builder.gav;
        this.contextName = contextName;
        this.contextAttributes = new HashMap<>();
        this.applicationSections = new HashSet<>();
        this.appHandlers = new HashSet<>();
        this.sourceFileCodegenBindNotifier = builder.sourceFileCodegenBindNotifier;
    }

    protected static Properties load(File... resourcePaths) {
        Properties applicationProperties = new Properties();

        for (File resourcePath : resourcePaths) {
            try (FileReader fileReader = new FileReader(new File(resourcePath, APPLICATION_PROPERTIES_FILE_NAME))) {
                applicationProperties.load(fileReader);
            } catch (IOException ioe) {
                LOGGER.debug("Unable to load '" + APPLICATION_PROPERTIES_FILE_NAME + "'.");
            }
        }

        return applicationProperties;
    }

    @Override
    public boolean hasClassAvailable(String fqcn) {
        return classAvailabilityResolver.test(fqcn);
    }

    @Override
    public boolean hasImplementationClassAvailable(Class<?> clazz) {
        return classSubTypeAvailabilityResolver.test(clazz);
    }

    @Override
    public DependencyInjectionAnnotator getDependencyInjectionAnnotator() {
        return dependencyInjectionAnnotator;
    }

    @Override
    public void setDependencyInjectionAnnotator(DependencyInjectionAnnotator dependencyInjectionAnnotator) {
        this.dependencyInjectionAnnotator = dependencyInjectionAnnotator;
    }

    @Override
    public RestAnnotator getRestAnnotator() {
        return restAnnotator;
    }

    @Override
    public void setRestAnnotator(RestAnnotator restAnnotator) {
        this.restAnnotator = restAnnotator;
    }

    @Override
    public Optional<String> getApplicationProperty(String property) {
        return applicationProperties.getApplicationProperty(property);
    }

    @Override
    public <T> Optional<T> getApplicationProperty(String property, Class<T> clazz) {
        return applicationProperties.getApplicationProperty(property, clazz);
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
    public void removeApplicationProperty(String key) {
        applicationProperties.removeApplicationProperty(key);
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public AddonsConfig getAddonsConfig() {
        return addonsConfig;
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
    public Optional<KogitoGAV> getGAV() {
        return Optional.ofNullable(gav);
    }

    @Override
    public Map<String, Object> getContextAttributes() {
        return Collections.unmodifiableMap(contextAttributes);
    }

    @Override
    public Collection<String> getGeneratedHandlers() {
        return Collections.unmodifiableCollection(appHandlers);
    }

    @Override
    public void addGeneratedHandler(String workName) {
        appHandlers.add(workName);
    }

    @Override
    public <T> T getContextAttribute(String key, Class<T> asClass) {
        final Object output = this.contextAttributes.get(key);
        if (output == null) {
            return null;
        }
        if (asClass.isAssignableFrom(output.getClass())) {
            return asClass.cast(output);
        }
        throw new AssertionError("Impossible to cast '" + key + "' key value as " + asClass.getName() + ", found " + output.getClass().getCanonicalName());
    }

    @Override
    public void addContextAttribute(String key, Object value) {
        this.contextAttributes.put(key, value);
    }

    @Override
    public Set<ApplicationSection> getApplicationSections() {
        return Collections.unmodifiableSet(applicationSections);
    }

    @Override
    public void addAllApplicationSections(Set<ApplicationSection> applicationSections) {
        this.applicationSections.addAll(applicationSections);
    }

    @Override
    public void addApplicationSection(ApplicationSection applicationSection) {
        this.applicationSections.add(applicationSection);
    }

    @Override
    public Optional<SourceFileCodegenBindNotifier> getSourceFileCodegenBindNotifier() {
        return Optional.ofNullable(sourceFileCodegenBindNotifier);
    }

    @Override
    public String toString() {
        return "KogitoBuildContext{" +
                "contextName='" + contextName + '\'' +
                ", applicationProperties=" + applicationProperties +
                ", packageName='" + packageName + '\'' +
                ", addonsConfig=" + addonsConfig +
                '}';
    }

    protected abstract static class AbstractBuilder implements Builder {
        protected SourceFileCodegenBindNotifier sourceFileCodegenBindNotifier;

        protected String packageName = DEFAULT_PACKAGE_NAME;
        protected KogitoApplicationPropertyProvider applicationProperties = KogitoApplicationPropertyProvider.of(new Properties());
        protected AddonsConfig addonsConfig;
        protected ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        protected Predicate<String> classAvailabilityResolver = this::hasClass;
        protected Predicate<Class<?>> classSubTypeAvailabilityResolver = c -> false;
        // default fallback value (usually overridden)
        protected AppPaths appPaths = AppPaths.fromProjectDir(new File(".").toPath());
        protected KogitoGAV gav;

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
        public Builder withApplicationPropertyProvider(KogitoApplicationPropertyProvider applicationProperties) {
            Objects.requireNonNull(applicationProperties, "applicationPropertiesProvider cannot be null");
            this.applicationProperties = applicationProperties;
            return this;
        }

        @Override
        public Builder withApplicationProperties(Properties applicationProperties) {
            Objects.requireNonNull(applicationProperties, "applicationProperties cannot be null");
            this.applicationProperties = KogitoApplicationPropertyProvider.of(applicationProperties);
            return this;
        }

        @Override
        public Builder withApplicationProperties(File... files) {
            this.applicationProperties = KogitoApplicationPropertyProvider.of(load(files));
            return this;
        }

        @Override
        public Builder withAddonsConfig(AddonsConfig addonsConfig) {
            this.addonsConfig = addonsConfig;
            return this;
        }

        @Override
        public Builder withClassAvailabilityResolver(Predicate<String> classAvailabilityResolver) {
            Objects.requireNonNull(classAvailabilityResolver, "classAvailabilityResolver cannot be null");
            this.classAvailabilityResolver = classAvailabilityResolver;
            return this;
        }

        @Override
        public Builder withClassSubTypeAvailabilityResolver(Predicate<Class<?>> classSubTypeAvailabilityResolver) {
            Objects.requireNonNull(classSubTypeAvailabilityResolver, "classSubTypeAvailabilityResolver cannot be null");
            this.classSubTypeAvailabilityResolver = classSubTypeAvailabilityResolver;
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

        @Override
        public Builder withGAV(KogitoGAV gav) {
            Objects.requireNonNull(gav, "gav cannot be null");
            this.gav = gav;
            return this;
        }

        @Override
        public Builder withSourceFileProcessBindNotifier(SourceFileCodegenBindNotifier sourceFileCodegenBindNotifier) {
            Objects.requireNonNull(sourceFileCodegenBindNotifier, "sourceFileProcessBindNotifier cannot be null");
            this.sourceFileCodegenBindNotifier = sourceFileCodegenBindNotifier;
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
