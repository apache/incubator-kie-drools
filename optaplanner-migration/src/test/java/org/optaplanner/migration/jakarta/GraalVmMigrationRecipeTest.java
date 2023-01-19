package org.optaplanner.migration.jakarta;

import static org.openrewrite.java.Assertions.mavenProject;
import static org.openrewrite.maven.Assertions.pomXml;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;

class GraalVmMigrationRecipeTest implements RewriteTest {

    @Test
    void migrateOtherModules() {
        rewriteRun(
                spec -> spec.recipe(new GraalVmMigrationRecipe())
                        .expectedCyclesThatMakeChanges(0),
                mavenProject("any-project",
                        pomXml(
                                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                        +
                                        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
                                        +
                                        "    <modelVersion>4.0.0</modelVersion>\n" +
                                        "    <groupId>org.optaplanner</groupId>\n" +
                                        "    <artifactId>optaplanner-persistence-xstream</artifactId>\n" +
                                        "    <version>0.0.1-SNAPSHOT</version>\n" +
                                        "    <dependencies>\n" +
                                        "        <dependency>\n" +
                                        "            <groupId>org.graalvm.sdk</groupId>\n" +
                                        "            <artifactId>graal-sdk</artifactId>\n" +
                                        "            <version>22.3.0</version>\n" +
                                        "        </dependency>\n" +
                                        "    </dependencies>\n" +
                                        "</project>\n")));
    }

    @Test
    void migrateQuarkusRuntimeModule() {
        rewriteRun(
                spec -> spec.recipe(new GraalVmMigrationRecipe())
                        .expectedCyclesThatMakeChanges(1),
                mavenProject("optaplanner-quarkus/runtime",
                        pomXml(
                                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                        +
                                        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
                                        +
                                        "    <modelVersion>4.0.0</modelVersion>\n" +
                                        "    <groupId>org.optaplanner</groupId>\n" +
                                        "    <artifactId>optaplanner-quarkus</artifactId>\n" +
                                        "    <version>0.0.1-SNAPSHOT</version>\n" +
                                        "    <dependencies>\n" +
                                        "        <dependency>\n" +
                                        "            <groupId>org.graalvm.sdk</groupId>\n" +
                                        "            <artifactId>graal-sdk</artifactId>\n" +
                                        "            <version>22.3.0</version>\n" +
                                        "        </dependency>\n" +
                                        "    </dependencies>\n" +
                                        "</project>\n",
                                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                        +
                                        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
                                        +
                                        "    <modelVersion>4.0.0</modelVersion>\n" +
                                        "    <groupId>org.optaplanner</groupId>\n" +
                                        "    <artifactId>optaplanner-quarkus</artifactId>\n" +
                                        "    <version>0.0.1-SNAPSHOT</version>\n" +
                                        "    <dependencies>\n" +
                                        "        <dependency>\n" +
                                        "            <groupId>org.graalvm.nativeimage</groupId>\n" +
                                        "            <artifactId>svm</artifactId>\n" +
                                        "            <version>22.3.0</version>\n" +
                                        "        </dependency>\n" +
                                        "    </dependencies>\n" +
                                        "</project>\n")));
    }
}