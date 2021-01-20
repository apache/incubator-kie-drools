/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.utils.AddonsConfigDiscovery;
import org.kie.kogito.codegen.utils.AppPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.SourceVersion;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;

public abstract class AbstractKogitoBuildContext implements KogitoBuildContext {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractKogitoBuildContext.class);

    protected final Predicate<String> classAvailabilityResolver;
    protected final Properties applicationProperties;
    protected final String packageName;
    protected final AddonsConfig addonsConfig;
    protected final ClassLoader classLoader;
    protected final AppPaths appPaths;
    protected final String contextName;

    protected DependencyInjectionAnnotator dependencyInjectionAnnotator;

    protected AbstractKogitoBuildContext(AbstractBuilder builder,
                                         DependencyInjectionAnnotator dependencyInjectionAnnotator,
                                         String contextName) {
        this.packageName = builder.packageName;
        this.classAvailabilityResolver = builder.classAvailabilityResolver;
        this.dependencyInjectionAnnotator = dependencyInjectionAnnotator;
        this.applicationProperties = builder.applicationProperties;
        this.addonsConfig = builder.addonsConfig != null ? builder.addonsConfig : AddonsConfigDiscovery.discover(this);
        this.classLoader = builder.classLoader;
        this.appPaths = builder.appPaths;
        this.contextName = contextName;
    }

    @Override
    public boolean hasClassAvailable(String fqcn) {
        return classAvailabilityResolver.test(fqcn);
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
    public Optional<String> getApplicationProperty(String property) {
        return Optional.ofNullable(applicationProperties.getProperty(property));
    }

    @Override
    public Collection<String> getApplicationProperties() {
        return applicationProperties.stringPropertyNames();
    }

    @Override
    public void setApplicationProperty(String key, Object value) {
        applicationProperties.put(key, value);
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

    protected static Properties load(File... resourcePaths) {
        Properties applicationProperties = new Properties();

        for (File resourcePath : resourcePaths) {
            try (FileReader fileReader = new FileReader( new File( resourcePath, APPLICATION_PROPERTIES_FILE_NAME ) )) {
                applicationProperties.load( fileReader );
            } catch (IOException ioe) {
                LOGGER.debug( "Unable to load '" + APPLICATION_PROPERTIES_FILE_NAME + "'." );
            }
        }

        return applicationProperties;
    }

    protected abstract static class AbstractBuilder implements Builder {

        protected String packageName = DEFAULT_PACKAGE_NAME;
        protected Properties applicationProperties = new Properties();
        protected AddonsConfig addonsConfig;
        protected Predicate<String> classAvailabilityResolver = this::hasClass;
        protected ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
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
            if (!packageName.equals(ApplicationGenerator.DEFAULT_GROUP_ID)) {
                this.packageName = packageName;
            }
            else {
                LOGGER.warn("Skipping the package provided because invalid: '{}' (current value '{}')", packageName, this.packageName);
            }
            return this;
        }

        @Override
        public Builder withApplicationProperties(Properties applicationProperties) {
            Objects.requireNonNull(applicationProperties, "applicationProperties cannot be null");
            this.applicationProperties = applicationProperties;
            return this;
        }

        @Override
        public Builder withApplicationProperties(File ... files) {
            this.applicationProperties = load(files);
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