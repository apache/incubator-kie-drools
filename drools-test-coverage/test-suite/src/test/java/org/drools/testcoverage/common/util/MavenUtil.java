/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.common.util;

import java.io.File;
import java.io.IOException;
import org.drools.core.util.FileManager;
import org.kie.api.builder.ReleaseId;

/**
 * Util class that provides various methods related to Maven.
 */
public final class MavenUtil {

    public static File createPomXml(final FileManager fileManager, final ReleaseId releaseId, final ReleaseId... dependencies)
            throws IOException {
        File pomFile = fileManager.newFile("pom.xml");
        fileManager.write(pomFile, getPomXml(releaseId, dependencies));
        return pomFile;
    }

    public static String getPomXml(final ReleaseId releaseId, final ReleaseId... dependencies) {
        final StringBuilder pomBuilder = new StringBuilder();
        pomBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                        "  <modelVersion>4.0.0</modelVersion>\n" +
                        "\n" +
                        "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
                        "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
                        "  <version>" + releaseId.getVersion() + "</version>\n" +
                        "\n");
        if (dependencies != null && dependencies.length > 0) {
            pomBuilder.append("<dependencies>\n");
            for (ReleaseId dep : dependencies) {
                pomBuilder.append("<dependency>\n");
                pomBuilder.append("  <groupId>" + dep.getGroupId() + "</groupId>\n");
                pomBuilder.append("  <artifactId>" + dep.getArtifactId() + "</artifactId>\n");
                pomBuilder.append("  <version>" + dep.getVersion() + "</version>\n");
                pomBuilder.append("</dependency>\n");
            }
            pomBuilder.append("</dependencies>\n");
        }
        pomBuilder.append("</project>");
        return pomBuilder.toString();
    }

    private MavenUtil() {
        // Creating instances of util classes should not be possible.
    }
}
