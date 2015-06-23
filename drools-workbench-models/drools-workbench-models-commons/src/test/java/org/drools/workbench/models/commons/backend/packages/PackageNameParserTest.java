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
