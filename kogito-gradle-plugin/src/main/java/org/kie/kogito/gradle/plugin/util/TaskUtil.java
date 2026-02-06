/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.kie.kogito.gradle.plugin.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.kogito.codegen.manager.util.CodeGenManagerUtil;
import org.kie.util.maven.support.ReleaseIdImpl;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;

public final class TaskUtil {

    private static final String COMPILE_CLASSPATH = "compileClasspath";
    private static final String RUNTIME_CLASSPATH = "runtimeClasspath";

    public static Set<URI> getProjectFiles(final Project gradleProject,
            final List<InternalKieModule> kmoduleDeps)
            throws IOException {
        final Set<URI> toReturn = getScaffoldFiles(gradleProject, kmoduleDeps);
        File classesDirectory = gradleProject.getProjectDir().toPath()
                .resolve("build")
                .resolve("classes")
                .resolve("java")
                .resolve("main")
                .toFile();
        toReturn.add(classesDirectory.toURI());
        return toReturn;
    }

    public static Set<URI> getScaffoldFiles(final Project gradleProject,
            final List<InternalKieModule> kmoduleDeps)
            throws IOException {
        final Set<URI> toReturn = new HashSet<>();
        Set<ResolvedArtifact> resolvedArtifacts = new HashSet<>();
        resolvedArtifacts.addAll(getResolvedArtifactsByConfigurationName(gradleProject, COMPILE_CLASSPATH));
        resolvedArtifacts.addAll(getResolvedArtifactsByConfigurationName(gradleProject, RUNTIME_CLASSPATH));
        for (final ResolvedArtifact artifact : resolvedArtifacts) {
            if (artifact.getType().equals("jar")) {
                populateURLsFromJarArtifact(toReturn, artifact, kmoduleDeps);
            }
        }
        return toReturn;
    }

    public static List<String> getRuntimeClasspathElements(final Project gradleProject) {
        List<String> toReturn = getRuntimeClasspathArtifactsPaths(gradleProject);
        File classesDirectory = gradleProject.getProjectDir().toPath()
                .resolve("build")
                .resolve("classes")
                .resolve("java")
                .resolve("main")
                .toFile();
        toReturn.add(classesDirectory.getAbsolutePath());
        return toReturn;
    }

    public static List<String> getRuntimeClasspathArtifactsPaths(final Project gradleProject) {
        return getResolvedArtifactsByConfigurationName(gradleProject, RUNTIME_CLASSPATH)
                .stream()
                .map(ResolvedArtifact::getFile)
                .map(File::getPath)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static Set<URI> getRuntimeClasspathArtifacts(final Project gradleProject) {
        return getResolvedArtifactsByConfigurationName(gradleProject, RUNTIME_CLASSPATH)
                .stream()
                .map(ResolvedArtifact::getFile)
                .map(File::toURI)
                .collect(Collectors.toSet());
    }

    public static boolean hasDependency(final Project gradleProject, CodeGenManagerUtil.Framework framework) {
        Predicate<ResolvedArtifact> predicate = artifact -> {
            ModuleVersionIdentifier identifier = artifact.getModuleVersion().getId();
            return identifier.getName().contains(framework.toName());
        };
        return getResolvedArtifactsByConfigurationName(gradleProject, COMPILE_CLASSPATH)
                .stream()
                .anyMatch(predicate) ||
                getResolvedArtifactsByConfigurationName(gradleProject, RUNTIME_CLASSPATH)
                        .stream()
                        .anyMatch(predicate);
    }

    static Set<ResolvedArtifact> getResolvedArtifactsByConfigurationName(final Project gradleProject, String configurationName) {
        return gradleProject.getConfigurations().getByName(configurationName)
                .getResolvedConfiguration()
                .getResolvedArtifacts();
    }

    private static void populateURLsFromJarArtifact(final Set<URI> toPopulate, final ResolvedArtifact artifact,
            final List<InternalKieModule> kmoduleDeps) throws IOException {
        final File file = artifact.getFile();
        if (file != null && file.isFile()) {
            toPopulate.add(file.toURI());
            final KieModuleModel depModel = getDependencyKieModel(file);
            if (kmoduleDeps != null && depModel != null) {
                ModuleVersionIdentifier identifier = artifact.getModuleVersion().getId();
                final ReleaseId releaseId = new ReleaseIdImpl(identifier.getGroup(), identifier.getName(),
                        identifier.getVersion());
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

    private TaskUtil() {
        // Creating instances of util classes is forbidden.
    }
}
