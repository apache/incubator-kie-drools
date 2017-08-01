/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.container.tools;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

public class IntegrationMavenResolver {

    public static PomEquippedResolveStage get(String... profiles) {
        String localRepositoryPath = System.getProperty("settings.localRepository");
        String version = System.getProperty("project.version");
        File pom = new File(localRepositoryPath, "org/jbpm/shrinkwrap-war-profiles/" + version
                + "/shrinkwrap-war-profiles-" + version + ".pom");

        // Custom settings.xml can be passed via org.apache.maven.user-settings property
        final MavenResolverSystem resolver = Maven.resolver();

        return resolver.loadPomFromFile(pom, profiles);
    }

}
