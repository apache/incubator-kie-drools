package org.optaplanner.migration.jakarta;

import static org.openrewrite.java.Assertions.mavenProject;
import static org.openrewrite.maven.Assertions.pomXml;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;

class JakartaJsonMigrationRecipeTest implements RewriteTest {

    @Test
    void migrateOptaPlannerPersistenceJsonB() {
        rewriteRun(
                spec -> spec.recipe(new JakartaJsonMigrationRecipe())
                        .expectedCyclesThatMakeChanges(1),
                mavenProject("optaplanner-persistence-jsonb",
                        pomXml(
                                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                        +
                                        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
                                        +
                                        "    <modelVersion>4.0.0</modelVersion>\n" +
                                        "    <groupId>org.optaplanner</groupId>\n" +
                                        "    <artifactId>optaplanner-persistence-jsonb</artifactId>\n" +
                                        "    <version>0.0.1-SNAPSHOT</version>\n" +
                                        "    <dependencies>\n" +
                                        "        <dependency>\n" +
                                        "            <groupId>org.glassfish</groupId>\n" +
                                        "            <artifactId>jakarta.json</artifactId>\n" +
                                        "            <scope>runtime</scope>\n" +
                                        "            <version>1.1.6</version>\n" +
                                        "        </dependency>\n" +
                                        "        <dependency>\n" +
                                        "            <groupId>org.eclipse</groupId>\n" +
                                        "            <artifactId>yasson</artifactId>\n" +
                                        "            <scope>test</scope>\n" +
                                        "            <version>1.0.11</version>\n" +
                                        "            <exclusions>\n" +
                                        "                <exclusion>\n" +
                                        "                    <groupId>jakarta.json</groupId>\n" +
                                        "                    <artifactId>jakarta.json-api</artifactId>\n" +
                                        "                </exclusion>\n" +
                                        "                <exclusion>\n" +
                                        "                    <groupId>org.glassfish</groupId>\n" +
                                        "                    <artifactId>jakarta.json</artifactId>\n" +
                                        "                </exclusion>\n" +
                                        "            </exclusions>\n" +
                                        "        </dependency>\n" +
                                        "    </dependencies>\n" +
                                        "</project>\n",
                                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                        +
                                        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
                                        +
                                        "    <modelVersion>4.0.0</modelVersion>\n" +
                                        "    <groupId>org.optaplanner</groupId>\n" +
                                        "    <artifactId>optaplanner-persistence-jsonb</artifactId>\n" +
                                        "    <version>0.0.1-SNAPSHOT</version>\n" +
                                        "    <dependencies>\n" +
                                        "        <dependency>\n" +
                                        "            <groupId>org.eclipse</groupId>\n" +
                                        "            <artifactId>yasson</artifactId>\n" +
                                        "            <scope>test</scope>\n" +
                                        "            <version>1.0.11</version>\n" +
                                        "        </dependency>\n" +
                                        "    </dependencies>\n" +
                                        "</project>\n"

                        )));
    }

    @Test
    void migrateToJakartaJson() {
        rewriteRun(
                spec -> spec.recipe(new JakartaJsonMigrationRecipe())
                        .expectedCyclesThatMakeChanges(1),
                mavenProject("any-project",
                        pomXml(
                                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                        +
                                        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
                                        +
                                        "    <modelVersion>4.0.0</modelVersion>\n" +
                                        "    <groupId>org.optaplanner</groupId>\n" +
                                        "    <artifactId>optaplanner-examples</artifactId>\n" +
                                        "    <version>0.0.1-SNAPSHOT</version>\n" +
                                        "    <dependencies>\n" +
                                        "        <dependency>\n" +
                                        "            <groupId>org.glassfish</groupId>\n" +
                                        "            <artifactId>jakarta.json</artifactId>\n" +
                                        "            <version>1.1.6</version>\n" +
                                        "        </dependency>\n" +
                                        "    </dependencies>\n" +
                                        "</project>\n",
                                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                        +
                                        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
                                        +
                                        "    <modelVersion>4.0.0</modelVersion>\n" +
                                        "    <groupId>org.optaplanner</groupId>\n" +
                                        "    <artifactId>optaplanner-examples</artifactId>\n" +
                                        "    <version>0.0.1-SNAPSHOT</version>\n" +
                                        "    <dependencies>\n" +
                                        "        <dependency>\n" +
                                        "            <groupId>jakarta.json</groupId>\n" +
                                        "            <artifactId>jakarta.json-api</artifactId>\n" +
                                        "            <version>1.1.6</version>\n" +
                                        "        </dependency>\n" +
                                        "    </dependencies>\n" +
                                        "</project>\n"

                        )));
    }
}
