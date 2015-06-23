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

package org.drools.workbench.models.commons.backend.packages;

import org.drools.workbench.models.datamodel.packages.HasPackageName;
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
