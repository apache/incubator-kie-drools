package org.drools.guvnor.models.commons.backend.packages;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for PackageNameParser
 */
public class PackageNameParserTest {

    @Test
    public void testNullContent() {
        final String content = null;

        final String packageName = PackageNameParser.parsePackageName( content );
        assertNotNull( packageName );
        assertEquals( "",
                      packageName );
    }

    @Test
    public void testEmptyContent() {
        final String content = "";

        final String packageName = PackageNameParser.parsePackageName( content );
        assertNotNull( packageName );
        assertEquals( "",
                      packageName );
    }

    @Test
    public void testCommentedContent() {
        final String content = "#This is a comment";

        final String packageName = PackageNameParser.parsePackageName( content );
        assertNotNull( packageName );
        assertEquals( "",
                      packageName );
    }

    @Test
    public void testSinglePackageDeclarationContent() {
        final String content = "package org.drools.guvnor.models.commons.backend.packages;";

        final String packageName = PackageNameParser.parsePackageName( content );
        assertNotNull( packageName );
        assertEquals( "org.drools.guvnor.models.commons.backend.packages",
                      packageName );
    }

    @Test
    public void testMultiplePackageDeclarationsContent() {
        final String content = ""
                + "package org.drools.guvnor.models.commons.backend.packages;\n"
                + "package a.second.package.declaration;\n";

        final String packageName = PackageNameParser.parsePackageName( content );
        assertNotNull( packageName );
        assertEquals( "org.drools.guvnor.models.commons.backend.packages",
                      packageName );
    }

    @Test
    public void testMixedContent() {
        final String content = ""
                + "package org.drools.guvnor.models.commons.backend.packages;\n"
                + "#This is a comment\n"
                + "package a.second.package.declaration;\n"
                + "\n"
                + "package a.third.package.declaration;\n";

        final String packageName = PackageNameParser.parsePackageName( content );
        assertNotNull( packageName );
        assertEquals( "org.drools.guvnor.models.commons.backend.packages",
                      packageName );
    }

}
