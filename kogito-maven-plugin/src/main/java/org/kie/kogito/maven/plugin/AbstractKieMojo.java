/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.maven.plugin;

import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.SpringDependencyInjectionAnnotator;

public abstract class AbstractKieMojo extends AbstractMojo {

    protected void setSystemProperties(Map<String, String> properties) {

        if (properties != null) {
            getLog().debug("Additional system properties: " + properties);
            for (Map.Entry<String, String> property : properties.entrySet()) {
                System.setProperty(property.getKey(), property.getValue());
            }
            getLog().debug("Configured system properties were successfully set.");
        }
    }

    protected DependencyInjectionAnnotator discoverDependencyInjectionAnnotator(boolean dependencyInjection, MavenProject project) {
        if (!dependencyInjection) {
            return null;
        }

        boolean hasSpring = project.getDependencies().stream().anyMatch(d -> d.getArtifactId().contains("spring"));
        if (hasSpring) {
            return new SpringDependencyInjectionAnnotator();
        }

        boolean hasQuarkus = project.getDependencies().stream().anyMatch(d -> d.getArtifactId().contains("quarkus"));
        if (hasQuarkus) {
            return new CDIDependencyInjectionAnnotator();
        }

        throw new IllegalStateException("Unable to find dependency injection annotator");
    }
}
