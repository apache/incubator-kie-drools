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
