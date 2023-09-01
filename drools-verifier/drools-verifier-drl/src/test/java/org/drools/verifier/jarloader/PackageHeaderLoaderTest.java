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
package org.drools.verifier.jarloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.jar.JarInputStream;

import org.drools.verifier.Verifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class PackageHeaderLoaderTest {

    private ArrayList<JarInputStream> jarInputStreams;

    @BeforeEach
    public void setUp() throws Exception {
        jarInputStreams = new ArrayList<JarInputStream>();
        jarInputStreams.add(new JarInputStream(Verifier.class.getResourceAsStream("model.jar")));
    }

    @AfterEach
    public void tearDown() throws Exception {
        for (JarInputStream jarInputStream : jarInputStreams) {
            jarInputStream.close();
        }
    }

    @Test
    void testListAddressAndPetImport() throws Exception {

        PackageHeaderLoader packageHeaderLoader = getPackageHeaderLoader("org.test.Rambo", "org.test.Pet");

        Collection<String> classNames = packageHeaderLoader.getClassNames();

        assertThat(classNames.size()).isEqualTo(2);
        assertThat(classNames.contains("org.test.Rambo")).isTrue();
        assertThat(classNames.contains("org.test.Pet")).isTrue();
    }

    @Test
    void testListFewClassesThatDoNotExist() throws Exception {
        PackageHeaderLoader packageHeaderLoader = getPackageHeaderLoader("org.test.Rambo", "i.do.not.Exist", "me.Neither");

        Collection<String> classNames = packageHeaderLoader.getClassNames();
        Collection<String> missingClasses = packageHeaderLoader.getMissingClasses();

        assertThat(classNames.size()).isEqualTo(3);
        assertThat(missingClasses.size()).isEqualTo(2);
    }

    @Test
    void testListFields() throws Exception {
        PackageHeaderLoader packageHeaderLoader = getPackageHeaderLoader("org.test.Person");

        Collection<String> fieldNames = packageHeaderLoader.getFieldNames("org.test.Person");

        assertThat(fieldNames.contains("birhtday")).isTrue(); // Yes it is a typo -Rikkola-
        assertThat(fieldNames.contains("firstName")).isTrue();
        assertThat(fieldNames.contains("lastName")).isTrue();
        assertThat(fieldNames.contains("pets")).isTrue();
        assertThat(fieldNames.contains("this")).isTrue();

        assertThat(packageHeaderLoader.getFieldType("org.test.Person", "firstName")).isEqualTo("java.lang.String");
        assertThat(packageHeaderLoader.getFieldType("org.test.Person", "firstName")).isEqualTo("java.lang.String");
        assertThat(packageHeaderLoader.getFieldType("org.test.Person", "pets")).isEqualTo("java.util.List");
        assertThat(packageHeaderLoader.getFieldType("org.test.Person", "birhtday")).isEqualTo("java.util.Calendar");
        assertThat(packageHeaderLoader.getFieldType("org.test.Person", "this")).isEqualTo("org.test.Person");
        assertThat(packageHeaderLoader.getFieldType("org.test.Person", "toString")).isNull();
        assertThat(packageHeaderLoader.getFieldType("org.test.Person", "class")).isNull();
        assertThat(packageHeaderLoader.getFieldType("org.test.Person", "hashCode")).isNull();
    }

    private PackageHeaderLoader getPackageHeaderLoader(String... imports) {
        try {
            return new PackageHeaderLoader(createImportsList(imports), jarInputStreams);
        } catch (IOException e) {
            fail("Failed to read the jar input streams.");
            return null;
        }
    }

    private Collection<String> createImportsList(String... list) {
        return Arrays.asList(list);
    }

}
