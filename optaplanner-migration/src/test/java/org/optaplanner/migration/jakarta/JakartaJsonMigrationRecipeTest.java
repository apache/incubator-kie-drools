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
