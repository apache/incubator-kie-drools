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
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
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
    protected final File targetDirectory;
    protected final String packageName;
    protected final AddonsConfig addonsConfig;

    protected DependencyInjectionAnnotator dependencyInjectionAnnotator;

    protected AbstractKogitoBuildContext(String packageName,
                                      Predicate<String> classAvailabilityResolver,
                                      DependencyInjectionAnnotator dependencyInjectionAnnotator,
                                      File targetDirectory,
                                      AddonsConfig addonsConfig,
                                      Properties applicationProperties) {
        this.packageName = packageName;
        this.classAvailabilityResolver = classAvailabilityResolver;
        this.dependencyInjectionAnnotator = dependencyInjectionAnnotator;
        this.targetDirectory = targetDirectory;
        this.addonsConfig = addonsConfig;
        this.applicationProperties = applicationProperties;
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
    public File getTargetDirectory() {
        return targetDirectory;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public AddonsConfig getAddonsConfig() {
        return addonsConfig;
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
        protected AddonsConfig addonsConfig = AddonsConfig.DEFAULT;
        protected Predicate<String> classAvailabilityResolver = this::hasClass;
        protected File targetDirectory = new File("target");

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
            this.packageName = packageName;
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
            Objects.requireNonNull(addonsConfig, "addonsConfig cannot be null");
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
        public Builder withTargetDirectory(File targetDirectory) {
            Objects.requireNonNull(targetDirectory, "targetDirectory cannot be null");
            if (!targetDirectory.isDirectory()) {
                throw new IllegalArgumentException("targetDirectory must exist and be a directory");
            }
            this.targetDirectory = targetDirectory;
            return this;
        }

        private boolean hasClass(String className) {
            try {
                Thread.currentThread().getContextClassLoader().loadClass(className);
                return true;
            } catch (ClassNotFoundException ex) {
                return false;
            }
        }

    }
}