package org.drools.guvnor.models.commons.backend.packages;

import org.drools.guvnor.models.commons.shared.packages.HasPackageName;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for PackageNameWriter
 */
public class PackageNameWriterTest {

    @Test
    public void testNullModel() {
        final HasPackageName model = new HasPackageName() {

            private String packageName = null;

            @Override
            public String getPackageName() {
                return packageName;
            }

            @Override
            public void setPackageName( final String packageName ) {
                this.packageName = packageName;
            }
        };

        final StringBuilder sb = new StringBuilder();
        PackageNameWriter.write( sb,
                                 model );

        final String drl = sb.toString();
        assertNotNull( drl );

        assertTrue( drl.isEmpty() );
    }

    @Test
    public void testEmptyModel() {
        final HasPackageName model = new HasPackageName() {

            private String packageName = "";

            @Override
            public String getPackageName() {
                return packageName;
            }

            @Override
            public void setPackageName( final String packageName ) {
                this.packageName = packageName;
            }
        };

        final StringBuilder sb = new StringBuilder();
        PackageNameWriter.write( sb,
                                 model );

        final String drl = sb.toString();
        assertNotNull( drl );

        assertTrue( drl.isEmpty() );
    }

    @Test
    public void testModel() {
        final String expectedDrl = "package org.drools.guvnor.models.commons.backend.packages;\n\n";

        final HasPackageName model = new HasPackageName() {

            private String packageName = "org.drools.guvnor.models.commons.backend.packages";

            @Override
            public String getPackageName() {
                return packageName;
            }

            @Override
            public void setPackageName( final String packageName ) {
                this.packageName = packageName;
            }
        };

        final StringBuilder sb = new StringBuilder();
        PackageNameWriter.write( sb,
                                 model );

        final String drl = sb.toString();
        assertNotNull( drl );

        assertEquals( expectedDrl,
                      drl );
    }

}
