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
