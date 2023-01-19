package org.optaplanner.migration.jakarta;

import static org.openrewrite.java.Assertions.mavenProject;
import static org.openrewrite.maven.Assertions.pomXml;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RewriteTest;

public class JakartaHibernateMigrationRecipeTest implements RewriteTest {

    @Test
    void migrateOptaPlannerPersistenceJpa() {
        rewriteRun(
                spec -> spec.recipe(new JakartaHibernateMigrationRecipe())
                        .expectedCyclesThatMakeChanges(1),
                mavenProject("optaplanner-persistence-jpa",
                        pomXml(
                                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                                        +
                                        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n"
                                        +
                                        "    <modelVersion>4.0.0</modelVersion>\n" +
                                        "    <groupId>org.optaplanner</groupId>\n" +
                                        "    <artifactId>optaplanner-persistence-jpa</artifactId>\n" +
                                        "    <version>0.0.1-SNAPSHOT</version>\n" +
                                        "    <dependencies>\n" +
                                        "        <dependency>\n" +
                                        "            <groupId>org.hibernate</groupId>\n" +
                                        "            <artifactId>hibernate-core</artifactId>\n" +
                                        "            <version>5.6.14.Final</version>\n" +
                                        "            <exclusions>\n" +
                                        "                <exclusion>\n" +
                                        "                    <groupId>javax.activation</groupId>\n" +
                                        "                    <artifactId>javax.activation-api</artifactId>\n" +
                                        "                </exclusion>\n" +
                                        "                <exclusion>\n" +
                                        "                    <groupId>javax.xml.bind</groupId>\n" +
                                        "                    <artifactId>jaxb-api</artifactId>\n" +
                                        "                </exclusion>\n" +
                                        "                <exclusion>\n" +
                                        "                    <groupId>org.jboss.spec.javax.transaction</groupId>\n" +
                                        "                    <artifactId>jboss-transaction-api_1.2_spec</artifactId>\n" +
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
                                        "    <artifactId>optaplanner-persistence-jpa</artifactId>\n" +
                                        "    <version>0.0.1-SNAPSHOT</version>\n" +
                                        "    <dependencies>\n" +
                                        "        <dependency>\n" +
                                        "            <groupId>org.hibernate</groupId>\n" +
                                        "            <artifactId>hibernate-core-jakarta</artifactId>\n" +
                                        "            <version>5.6.14.Final</version>\n" +
                                        "        </dependency>\n" +
                                        "    </dependencies>\n" +
                                        "</project>\n")));
    }

    @Test
    void migrateToHibernateJakarta() {
        rewriteRun(
                spec -> spec.recipe(new JakartaHibernateMigrationRecipe())
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
                                        "            <groupId>org.hibernate</groupId>\n" +
                                        "            <artifactId>hibernate-core</artifactId>\n" +
                                        "            <version>5.6.14.Final</version>\n" +
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
                                        "            <groupId>org.hibernate</groupId>\n" +
                                        "            <artifactId>hibernate-core-jakarta</artifactId>\n" +
                                        "            <version>5.6.14.Final</version>\n" +
                                        "        </dependency>\n" +
                                        "    </dependencies>\n" +
                                        "</project>\n")));
    }
}
