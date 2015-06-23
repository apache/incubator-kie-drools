/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.verifier.jarloader;

import org.drools.verifier.Verifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.jar.JarInputStream;

import static org.junit.Assert.*;


public class PackageHeaderLoaderTest {

    private ArrayList<JarInputStream> jarInputStreams;

    @Before
    public void setUp() throws Exception {
        jarInputStreams = new ArrayList<JarInputStream>();
        jarInputStreams.add(new JarInputStream(Verifier.class.getResourceAsStream("model.jar")));
    }

    @After
    public void tearDown() throws Exception {
        for (JarInputStream jarInputStream : jarInputStreams) {
            jarInputStream.close();
        }
    }

    @Test
    public void testListAddressAndPetImport() throws Exception {

        PackageHeaderLoader packageHeaderLoader = getPackageHeaderLoader("org.test.Rambo", "org.test.Pet");

        Collection<String> classNames = packageHeaderLoader.getClassNames();

        assertEquals(2, classNames.size());
        assertTrue(classNames.contains("org.test.Rambo"));
        assertTrue(classNames.contains("org.test.Pet"));
    }

    @Test
    public void testListFewClassesThatDoNotExist() throws Exception {
        PackageHeaderLoader packageHeaderLoader = getPackageHeaderLoader("org.test.Rambo", "i.do.not.Exist", "me.Neither");

        Collection<String> classNames = packageHeaderLoader.getClassNames();
        Collection<String> missingClasses = packageHeaderLoader.getMissingClasses();

        assertEquals(3, classNames.size());
        assertEquals(2, missingClasses.size());
    }

    @Test
    public void testListFields() throws Exception {
        PackageHeaderLoader packageHeaderLoader = getPackageHeaderLoader("org.test.Person");

        Collection<String> fieldNames = packageHeaderLoader.getFieldNames("org.test.Person");

        assertTrue(fieldNames.contains("birhtday")); // Yes it is a typo -Rikkola-
        assertTrue(fieldNames.contains("firstName"));
        assertTrue(fieldNames.contains("lastName"));
        assertTrue(fieldNames.contains("pets"));
        assertTrue(fieldNames.contains("this"));

        assertEquals("java.lang.String", packageHeaderLoader.getFieldType("org.test.Person", "firstName"));
        assertEquals("java.lang.String", packageHeaderLoader.getFieldType("org.test.Person", "firstName"));
        assertEquals("java.util.List", packageHeaderLoader.getFieldType("org.test.Person", "pets"));
        assertEquals("java.util.Calendar", packageHeaderLoader.getFieldType("org.test.Person", "birhtday"));
        assertEquals("org.test.Person", packageHeaderLoader.getFieldType("org.test.Person", "this"));
        assertNull(packageHeaderLoader.getFieldType("org.test.Person", "toString"));
        assertNull(packageHeaderLoader.getFieldType("org.test.Person", "class"));
        assertNull(packageHeaderLoader.getFieldType("org.test.Person", "hashCode"));
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
        Collection<String> imports = new ArrayList<String>();

        for (String s : list) {
            imports.add(s);
        }

        return imports;
    }

}
