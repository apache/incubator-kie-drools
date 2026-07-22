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
package org.jbpm.tools.maven;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassLoaderHelper {
    private static Logger LOGGER = LoggerFactory.getLogger(ClassLoaderHelper.class);

    public static ClassLoader getClassLoader(MavenProject project) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {

            Set<URL> classPathUrls = new HashSet<>();

            // adding the projects classes itself
            List<String> classpathElements = project.getCompileClasspathElements();
            classpathElements.add(project.getBuild().getOutputDirectory());
            classpathElements.add(project.getBuild().getTestOutputDirectory());
            for (final String classpathElement : classpathElements) {
                LOGGER.info("adding classpath element {} to classloader", classpathElement);
                classPathUrls.add(new File(classpathElement).toURI().toURL());
            }

            return new URLClassLoader(classPathUrls.stream().toArray(URL[]::new), contextClassLoader);
        } catch (final Exception e) {
            return contextClassLoader;
        }
    }
}
