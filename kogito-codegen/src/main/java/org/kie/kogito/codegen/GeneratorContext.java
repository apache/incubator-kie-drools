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

package org.kie.kogito.codegen;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import org.kie.kogito.codegen.context.JavaKogitoBuildContext;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneratorContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorContext.class);

    private static final String APPLICATION_PROPERTIES_FILE_NAME = "application.properties";

    public static GeneratorContext emptyContext() {
        return ofResourcePath().withBuildContext(new JavaKogitoBuildContext());
    }

    public static GeneratorContext ofResourcePath(File... resourcePaths) {
        Properties applicationProperties = new Properties();

        for (File resourcePath : resourcePaths) {
            try (FileReader fileReader = new FileReader( new File( resourcePath, APPLICATION_PROPERTIES_FILE_NAME ) )) {
                applicationProperties.load( fileReader );
            } catch (IOException ioe) {
                LOGGER.debug( "Unable to load '" + APPLICATION_PROPERTIES_FILE_NAME + "'." );
            }
        }

        return new GeneratorContext(applicationProperties);
    }

    public static GeneratorContext ofProperties(Properties props) {
        return new GeneratorContext(props);
    }

    private KogitoBuildContext buildContext = new JavaKogitoBuildContext();

    private Properties applicationProperties = new Properties();

    private GeneratorContext(Properties properties) {
        this.applicationProperties = properties;
    }

    public GeneratorContext withBuildContext(KogitoBuildContext buildContext) {
        Objects.requireNonNull(buildContext, "KogitoBuildContext cannot be null");
        this.buildContext = buildContext;
        return this;
    }

    public KogitoBuildContext getBuildContext() {
        return this.buildContext;
    }

    public Optional<String> getApplicationProperty(String property) {
        return Optional.ofNullable(applicationProperties.getProperty(property));
    }

    public Collection<String> getApplicationProperties() {
        return applicationProperties.stringPropertyNames();
    }
}
