package org.drools.workbench.models.commons.backend.imports;

import org.drools.workbench.models.datamodel.imports.Imports;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for ImportsParser
 */
public class ImportsParserTest {

    @Test
    public void testNullContent() {
        final String content = null;

        final Imports imports = ImportsParser.parseImports( content );
        assertNotNull( imports );
        assertTrue( imports.getImports().isEmpty() );
    }

    @Test
    public void testEmptyContent() {
        final String content = "";

        final Imports imports = ImportsParser.parseImports( content );
        assertNotNull( imports );
        assertTrue( imports.getImports().isEmpty() );
    }

    @Test
    public void testCommentedContent() {
        final String content = "#This is a comment";

        final Imports imports = ImportsParser.parseImports( content );
        assertNotNull( imports );
        assertTrue( imports.getImports().isEmpty() );
    }

    @Test
    public void testSingleImportContent() {
        final String content = "import java.lang.String;";

        final Imports imports = ImportsParser.parseImports( content );
        assertNotNull( imports );
        assertEquals( 1,
                      imports.getImports().size() );
        assertEquals( "java.lang.String",
                      imports.getImports().get( 0 ).getType() );
    }

    @Test
    public void testMultipleImportsContent() {
        final String content = ""
                + "import java.lang.String;\n"
                + "import java.lang.Double;\n";

        final Imports imports = ImportsParser.parseImports( content );
        assertNotNull( imports );
        assertEquals( 2,
                      imports.getImports().size() );
        assertEquals( "java.lang.String",
                      imports.getImports().get( 0 ).getType() );
        assertEquals( "java.lang.Double",
                      imports.getImports().get( 1 ).getType() );
    }

    @Test
    public void testMixedContent() {
        final String content = ""
                + "import java.lang.String;\n"
                + "#This is a comment\n"
                + "import java.lang.Double;\n"
                + "\n"
                + "import java.lang.Byte;\n";

        final Imports imports = ImportsParser.parseImports( content );
        assertNotNull( imports );
        assertEquals( 3,
                      imports.getImports().size() );
        assertEquals( "java.lang.String",
                      imports.getImports().get( 0 ).getType() );
        assertEquals( "java.lang.Double",
                      imports.getImports().get( 1 ).getType() );
        assertEquals( "java.lang.Byte",
                      imports.getImports().get( 2 ).getType() );
    }

}
