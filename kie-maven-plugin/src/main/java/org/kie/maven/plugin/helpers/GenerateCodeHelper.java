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
package org.kie.maven.plugin.helpers;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.resolver.filter.CumulativeScopeArtifactFilter;
import org.apache.maven.project.MavenProject;
import org.kie.memorycompiler.JavaCompilerSettings;
import org.kie.memorycompiler.JavaConfiguration;
import org.kie.memorycompiler.KieMemoryCompiler;

import static org.kie.memorycompiler.JavaConfiguration.findJavaVersion;

public class GenerateCodeHelper {

    private GenerateCodeHelper() {
    }

    public static URLClassLoader getProjectClassLoader(MavenProject project, File outputDirectory, JavaCompilerSettings javaCompilerSettings) {
        try {
            Set<URL> urls = new HashSet<>();
            for (String element : project.getCompileClasspathElements()) {
                File file = new File(element);
                javaCompilerSettings.addClasspath(file);
                urls.add(file.toURI().toURL());
            }

            project.setArtifactFilter(new CumulativeScopeArtifactFilter(Arrays.asList("compile", "runtime")));
            for (Artifact artifact : project.getArtifacts()) {
                File file = artifact.getFile();
                if (file != null) {
                    javaCompilerSettings.addClasspath(file);
                    urls.add(file.toURI().toURL());
                }
            }
            urls.add(outputDirectory.toURI().toURL());

            return URLClassLoader.newInstance(urls.toArray(new URL[0]), GenerateCodeHelper.class.getClassLoader());

        } catch (DependencyResolutionRequiredException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void compileAndWriteClasses(File targetDirectory, ClassLoader projectClassLoader, JavaCompilerSettings javaCompilerSettings,
                                              JavaConfiguration.CompilerType compilerType, Map<String, String> classNameSourceMap, String dumpKieSourcesFolder) {
        if (dumpKieSourcesFolder != null && !dumpKieSourcesFolder.isEmpty()) {
            dumpGeneratedSources(targetDirectory, classNameSourceMap, dumpKieSourcesFolder);
        }

        Map<String, byte[]> compiledClassesMap = KieMemoryCompiler.compileNoLoad(classNameSourceMap, projectClassLoader, javaCompilerSettings, compilerType);
        writeClasses(targetDirectory, compiledClassesMap);
    }

    public static void writeClasses(File targetDirectory, Map<String, byte[]> compiledClassesMap) {


        for (Map.Entry<String, byte[]> entry : compiledClassesMap.entrySet()) {
            Path packagesDestinationPath = Paths.get(targetDirectory.getPath(), "classes", entry.getKey().replace('.', '/') + ".class");
            writeFile(packagesDestinationPath, entry.getValue());
        }
    }

    private static void dumpGeneratedSources(File targetDirectory, Map<String, String> classNameSourceMap, String dumpKieSourcesFolder) {
        for (Map.Entry<String, String> entry : classNameSourceMap.entrySet()) {
            Path sourceDestinationPath = Paths.get(targetDirectory.getPath(), dumpKieSourcesFolder, entry.getKey().replace('.', '/') + ".java");
            writeFile(sourceDestinationPath, entry.getValue().getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void writeFile(Path packagesDestinationPath, byte[] value) {
        try {
            if (!Files.exists(packagesDestinationPath)) {
                Files.createDirectories(packagesDestinationPath.getParent());
            }
            Files.write(packagesDestinationPath, value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static JavaCompilerSettings createJavaCompilerSettings(MavenProject project) {
        JavaCompilerSettings javaCompilerSettings = new JavaCompilerSettings();
        String javaVersion = findJavaVersion(project.getModel().getProperties().getOrDefault("maven.compiler.release", System.getProperty("java.version")).toString());
        javaCompilerSettings.setSourceVersion(javaVersion);
        javaCompilerSettings.setTargetVersion(javaVersion);
        return javaCompilerSettings;
    }

    public static String toClassName(String source) {
        if (source.startsWith("./") || source.startsWith(".\\")) {
            source = source.substring(2);
        }
        if (source.endsWith(".java")) {
            source = source.substring(0, source.length()-5);
        }
        return source.replace('/', '.').replace('\\', '.');
    }
}
