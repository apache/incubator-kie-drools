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

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.context.JavaKogitoBuildContext;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.context.SpringBootKogitoBuildContext;

public abstract class AbstractKieMojo extends AbstractMojo {

    @Parameter(required = true, defaultValue = "${project.build.directory}")
    protected File targetDirectory;

    @Parameter(required = true, defaultValue = "${project.basedir}")
    protected File projectDir;

    @Parameter
    protected Map<String, String> properties;

    @Parameter(required = true, defaultValue = "${project}")
    protected MavenProject project;

    @Parameter(required = true, defaultValue = "${project.build.outputDirectory}")
    protected File outputDirectory;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/kogito")
    protected File generatedSources;

    @Parameter(required = true, defaultValue = "${project.basedir}/src/main/resources")
    protected File kieSourcesDirectory;

    protected void setSystemProperties(Map<String, String> properties) {

        if (properties != null) {
            getLog().debug("Additional system properties: " + properties);
            for (Map.Entry<String, String> property : properties.entrySet()) {
                System.setProperty(property.getKey(), property.getValue());
            }
            getLog().debug("Configured system properties were successfully set.");
        }
    }

    protected AddonsConfig loadAddonsConfig(boolean persistence, MavenProject project) {
        boolean usePersistence = persistence || hasClassOnClasspath(project, "org.kie.kogito.persistence.KogitoProcessInstancesFactory");
        boolean usePrometheusMonitoring = hasClassOnClasspath(project, "org.kie.kogito.monitoring.prometheus.common.rest.MetricsResource");
        boolean useMonitoring = usePrometheusMonitoring || hasClassOnClasspath(project, "org.kie.kogito.monitoring.core.common.MonitoringRegistry");
        boolean useTracing = hasClassOnClasspath(project, "org.kie.kogito.tracing.decision.DecisionTracingListener");
        boolean useKnativeEventing = hasClassOnClasspath(project, "org.kie.kogito.events.knative.ce.extensions.KogitoProcessExtension");
        boolean useCloudEvents = hasClassOnClasspath(project, "org.kie.kogito.addon.cloudevents.AbstractTopicDiscovery");

        return AddonsConfig.builder()
                .withPersistence(usePersistence)
                .withMonitoring(useMonitoring)
                .withPrometheusMonitoring(usePrometheusMonitoring)
                .withTracing(useTracing)
                .withKnativeEventing(useKnativeEventing)
                .withCloudEvents(useCloudEvents)
                .build();

    }

    protected KogitoBuildContext.Builder discoverKogitoRuntimeContext(MavenProject project) {
        KogitoBuildContext.Builder builder = contextBuilder(project);
        builder.withClassAvailabilityResolver(fqcn -> hasClassOnClasspath(project, fqcn));
        return builder;
    }

    private KogitoBuildContext.Builder contextBuilder(MavenProject project)  {
        switch (discoverFramework(project)) {
            case QUARKUS: return QuarkusKogitoBuildContext.builder();
            case SPRING: return SpringBootKogitoBuildContext.builder();
            default: return JavaKogitoBuildContext.builder();
        }
    }

    private enum Framework { QUARKUS, SPRING, NONE }

    private Framework discoverFramework(MavenProject project) {
        if ( hasDependency( project, "quarkus" ) ) {
            return Framework.QUARKUS;
        }

        if ( hasDependency( project, "spring" ) ) {
            return Framework.SPRING;
        }

        return Framework.NONE;
    }

    private boolean hasDependency( MavenProject project, String dependency ) {
        return project.getDependencies().stream().anyMatch( d -> d.getArtifactId().contains( dependency ) );
    }

    protected boolean hasClassOnClasspath(MavenProject project, String className) {
        try {
            Set<Artifact> elements = project.getArtifacts();
            URL[] urls = new URL[elements.size()];

            int i = 0;
            Iterator<Artifact> it = elements.iterator();
            while (it.hasNext()) {
                Artifact artifact = it.next();

                urls[i] = artifact.getFile().toURI().toURL();
                i++;
            }
            try (URLClassLoader cl = new URLClassLoader(urls)) {
                cl.loadClass(className);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void writeGeneratedFile(GeneratedFile generatedFile) {
        try {
            Path targetPath = pathOf(generatedFile);
            getLog().info("Generating: " + targetPath);
            Files.write(
                    targetPath,
                    generatedFile.contents());
        } catch (IOException e) {
            String message = "Error while writing " + generatedFile.relativePath();
            getLog().error(message, e);
            throw new UncheckedIOException(message, e);
        }
    }

    protected File getSourcesPath() {
        return generatedSources;
    }

    private Path pathOf(GeneratedFile f) throws IOException {
        File sourceFolder;
        Path path;
        if (f.getType() == GeneratedFile.Type.GENERATED_CP_RESOURCE) { // since kogito-maven-plugin is after maven-resource-plugin, need to manually place in the correct (CP) location
            sourceFolder = outputDirectory;
            path = Paths.get(sourceFolder.getPath(), f.relativePath());
        } else if (f.getType().isCustomizable()) {
            sourceFolder = getSourcesPath();
            path = Paths.get(sourceFolder.getPath(), f.relativePath());
        } else {
            sourceFolder = generatedSources;
            path = Paths.get(sourceFolder.getPath(), f.relativePath());
        }

        Files.createDirectories(path.getParent());
        return path;
    }
}
