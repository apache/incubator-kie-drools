/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
