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
package org.kie.kogito.maven.plugin.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.resolver.filter.CumulativeScopeArtifactFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.kogito.codegen.manager.util.CodeGenManagerUtil;
import org.kie.util.maven.support.ReleaseIdImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;
import static org.kie.kogito.codegen.manager.util.CodeGenManagerUtil.convertURIsToURLs;

public final class MojoUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MojoUtil.class);

    public static Set<URI> getProjectFiles(final MavenProject mavenProject,
            final List<InternalKieModule> kmoduleDeps)
            throws DependencyResolutionRequiredException, IOException {
        final Set<URI> toReturn = getScaffoldFiles(mavenProject, kmoduleDeps);
        for (final String element : mavenProject.getCompileClasspathElements()) {
            toReturn.add(new File(element).toURI());
        }
        return toReturn;
    }

    public static Set<URI> getScaffoldFiles(final MavenProject mavenProject,
            final List<InternalKieModule> kmoduleDeps)
            throws IOException {
        final Set<URI> toReturn = new HashSet<>();
        mavenProject.setArtifactFilter(new CumulativeScopeArtifactFilter(Arrays.asList("compile", "runtime")));
        for (final Artifact artifact : mavenProject.getArtifacts()) {
            if (artifact.getType().equals("jar")) {
                populateURLsFromJarArtifact(toReturn, artifact, kmoduleDeps);
            }
        }
        return toReturn;
    }

    public static ClassLoader createProjectClassLoader(final ClassLoader parentClassLoader,
            final MavenProject mavenProject,
            final File outputDirectory,
            final List<InternalKieModule> kmoduleDeps) throws MojoExecutionException {
        try {
            final Set<URI> uris = getProjectFiles(mavenProject, kmoduleDeps);
            uris.add(outputDirectory.toURI());
            URL[] urlArray = convertURIsToURLs(uris);
            LOGGER.debug("Creating maven project class loading with: {}", Arrays.asList(urlArray));
            return URLClassLoader.newInstance(urlArray, parentClassLoader);
        } catch (final DependencyResolutionRequiredException | IOException e) {
            throw new MojoExecutionException("Error setting up Kie ClassLoader", e);
        }
    }

    private static void populateURLsFromJarArtifact(final Set<URI> toPopulate, final Artifact artifact,
            final List<InternalKieModule> kmoduleDeps) throws IOException {
        final File file = artifact.getFile();
        if (file != null && file.isFile()) {
            toPopulate.add(file.toURI());
            final KieModuleModel depModel = getDependencyKieModel(file);
            if (kmoduleDeps != null && depModel != null) {
                final ReleaseId releaseId = new ReleaseIdImpl(artifact.getGroupId(), artifact.getArtifactId(),
                        artifact.getVersion());
                kmoduleDeps.add(new ZipKieModule(releaseId, depModel, file));
            }
        }
    }

    private static KieModuleModel getDependencyKieModel(final File jar) throws IOException {
        try (final ZipFile zipFile = new ZipFile(jar)) {
            final ZipEntry zipEntry = zipFile.getEntry(KieModuleModelImpl.KMODULE_JAR_PATH.asString());
            if (zipEntry != null) {
                final KieModuleModel kieModuleModel = KieModuleModelImpl.fromXML(zipFile.getInputStream(zipEntry));
                setDefaultsforEmptyKieModule(kieModuleModel);
                return kieModuleModel;
            }
        }
        return null;
    }

    public static boolean hasDependency(final MavenProject mavenProject, CodeGenManagerUtil.Framework framework) {
        return mavenProject.getDependencies().stream().anyMatch(d -> d.getArtifactId().contains(framework.toName()));
    }

    public static boolean hasClassOnClasspath(final MavenProject project, String className) {
        try {
            Set<Artifact> elements = project.getArtifacts();
            Set<URI> uris = new HashSet<>();
            for (Artifact artifact : elements) {
                uris.add(artifact.getFile().toURI());
            }
            return CodeGenManagerUtil.isClassNameInUrlClassLoader(uris, className);
        } catch (Exception e) {
            return false;
        }
    }

    private MojoUtil() {
        // Creating instances of util classes is forbidden.
    }
}
