package org.drools.workbench.models.commons.backend.imports;

import org.drools.workbench.models.datamodel.imports.HasImports;
import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for ImportsWriter
 */
public class ImportsWriterTest {

    @Test
    public void testNullModel() {
        final HasImports model = new HasImports() {

            private Imports imports = null;

            @Override
            public Imports getImports() {
                return imports;
            }

            @Override
            public void setImports( final Imports imports ) {
                //Nothing to do here
            }

        };

        final StringBuilder sb = new StringBuilder();
        ImportsWriter.write( sb,
                             model );

        final String drl = sb.toString();
        assertNotNull( drl );

        assertTrue( drl.isEmpty() );
    }

    @Test
    public void testEmptyModel() {
        final HasImports model = new HasImports() {

            private Imports imports = new Imports();

            @Override
            public Imports getImports() {
                return imports;
            }

            @Override
            public void setImports( final Imports imports ) {
                //Nothing to do here
            }

        };

        final StringBuilder sb = new StringBuilder();
        ImportsWriter.write( sb,
                             model );

        final String drl = sb.toString();
        assertNotNull( drl );

        assertTrue( drl.isEmpty() );
    }

    @Test
    public void testSingleImportModel() {
        final String expectedDrl = "import java.lang.String;\n\n";

        final HasImports model = new HasImports() {

            private Imports imports = new Imports();

            {
                imports.addImport( new Import( "java.lang.String" ) );
            }

            @Override
            public Imports getImports() {
                return imports;
            }

            @Override
            public void setImports( final Imports imports ) {
                //Nothing to do here
            }

        };

        final StringBuilder sb = new StringBuilder();
        ImportsWriter.write( sb,
                             model );

        final String drl = sb.toString();
        assertNotNull( drl );

        assertEquals( expectedDrl,
                      drl );
    }

    @Test
    public void testMultipleImportsModel() {
        final String expectedDrl = ""
                + "import java.lang.String;\n"
                + "import java.lang.Double;\n\n";

        final HasImports model = new HasImports() {

            private Imports imports = new Imports();

            {
                imports.addImport( new Import( "java.lang.String" ) );
                imports.addImport( new Import( "java.lang.Double" ) );
            }

            @Override
            public Imports getImports() {
                return imports;
            }

            @Override
            public void setImports( final Imports imports ) {
                //Nothing to do here
            }

        };

        final StringBuilder sb = new StringBuilder();
        ImportsWriter.write( sb,
                             model );

        final String drl = sb.toString();
        assertNotNull( drl );

        assertEquals( expectedDrl,
                      drl );
    }

}
