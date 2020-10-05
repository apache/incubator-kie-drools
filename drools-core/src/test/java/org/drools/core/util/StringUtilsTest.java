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

package org.drools.core.util;

import org.junit.Assert;
import org.junit.Test;
import org.kie.api.builder.ReleaseId;

import static org.drools.core.util.StringUtils.getPkgUUID;
import static org.drools.core.util.StringUtils.indexOfOutOfQuotes;
import static org.drools.core.util.StringUtils.md5Hash;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class StringUtilsTest {

    @Test
    public void testFindEndOfMethodArgsIndex() {
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"myId\")", 12);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"myId\").call()", 12);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('myId')", 12);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('myId').call()", 12);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my'Id\")", 13);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my'Id\").call()", 13);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my'Id'\")", 14);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my'Id'\").call()", 14);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('my\"Id\"')", 14);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('my\"Id\"').call()", 14);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('my\"Id')", 13);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('my\"Id').call()", 13);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my\\\"Id\")", 14);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my\\\"Id\").call()", 14);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('myId', 'something')", 25);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"myId\", \"something\")", 25);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my'Id\", \"somet'hing\")", 27);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my'Id'\", \"somet'hing\")", 28);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my'(Id\", \"somet'(hing'\")", 30);

        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setObject(new Object())", 22);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setObject(new Object(\"string param\"))", 36);
    }

    private void findEndOfMethodArgsIndexAndAssertItEqualsToExpected(String strExpr, int expectedIndex) {
        int actualIndex = StringUtils.findEndOfMethodArgsIndex(strExpr, strExpr.indexOf('('));
        Assert.assertEquals("Expected and actual end of method args index for expr '" + strExpr + "' are not equal!",
                expectedIndex, actualIndex);
    }
    
    @Test
    public void test_codeAwareEqualsIgnoreSpaces() {
        Assert.assertTrue( StringUtils.codeAwareEqualsIgnoreSpaces( null, null ) );
        Assert.assertTrue( StringUtils.codeAwareEqualsIgnoreSpaces( "", "") );
        Assert.assertFalse( StringUtils.codeAwareEqualsIgnoreSpaces( "", null ) );
        Assert.assertFalse( StringUtils.codeAwareEqualsIgnoreSpaces( null, "" ) );
        
        Assert.assertTrue( StringUtils.codeAwareEqualsIgnoreSpaces( " ", "" ) );
        Assert.assertTrue( StringUtils.codeAwareEqualsIgnoreSpaces( "", " " ) );
        
        Assert.assertTrue( StringUtils.codeAwareEqualsIgnoreSpaces( " ", "  " ) );
        
        Assert.assertTrue(
                StringUtils.codeAwareEqualsIgnoreSpaces(
                        "rule Rx when then end",
                        " rule Rx  when then end " // <<- DIFF 3x 
                )
            );
        Assert.assertTrue(
                StringUtils.codeAwareEqualsIgnoreSpaces(
                        "rule Rx when then end\n",
                        " rule Rx  when then end\n " // <<- DIFF, both terminate with whitespace but different types
                )
            );
        
        Assert.assertFalse(
                StringUtils.codeAwareEqualsIgnoreSpaces(
                        "package org.drools.compiler\n",
                        
                        "package org.drools.compiler\n " +
                        "rule Rx when\n" +
                        "   $m : Message( message == \"Hello World\" )\n" +
                        "then\n" +
                        "end\n"
                )
            );
        
        Assert.assertTrue(
                StringUtils.codeAwareEqualsIgnoreSpaces(
                        "package org.drools.compiler\n" +
                        "rule Rx when\n" +
                        "   $m : Message( message == \"Hello World\" )\n" +
                        "then\n" +
                        "end\n",
                        
                        "package org.drools.compiler\n " +  // <<- DIFF
                        "rule Rx when\n" +
                        "   $m : Message( message == \"Hello World\" )\n" +
                        "then\n" +
                        "end\n"
                )
            );
        Assert.assertTrue(
                StringUtils.codeAwareEqualsIgnoreSpaces(
                        "package org.drools.compiler\n" +
                        "rule Rx when\n" +
                        "   $m : Message( message == \"Hello World\" )\n" +
                        "then\n" +
                        "end\n",
                        
                        " package org.drools.compiler\n" +  // <<- DIFF (at beginning of this line)
                        "rule Rx when\n" +
                        "   $m : Message( message == \"Hello World\" )\n" +
                        "then\n" +
                        "end\n"
                )
            );
        Assert.assertTrue(
                StringUtils.codeAwareEqualsIgnoreSpaces(
                        "package org.drools.compiler\n" +
                        "rule Rx when\n" +
                        "   $m : Message( message == \"Hello World\" )\n" +
                        "then\n" +
                        "end\n",
                        
                        " package org.drools.compiler\n " +  // <<- DIFF 2x
                        "rule Rx when\n" +
                        "   $m : Message( message == \"Hello World\" )\n" +
                        "then\n" +
                        "end\n " // <<- DIFF 
                )
            );
        Assert.assertTrue(
                StringUtils.codeAwareEqualsIgnoreSpaces(
                        "package org.drools.compiler\n" +
                        "rule Rx when\n" +
                        "   $m : Message( message == \"Hello World\" )\n" +
                        "then\n" +
                        "end\n",
                        
                        "package org.drools.compiler\n" +  
                        "rule Rx when\n" +
                        "   $m : Message( message == \"Hello World\"  )\n" + // <<- DIFF
                        "then\n" +
                        "end\n"
                )
            );
        Assert.assertFalse(
                StringUtils.codeAwareEqualsIgnoreSpaces(
                        "package org.drools.compiler\n" +
                        "rule Rx when\n" +
                        "   $m : Message( message == \"Hello World\" )\n" +
                        "then\n" +
                        "end\n",
                        
                        "package org.drools.compiler\n" +  
                        "rule Rx when\n" +
                        "   $m : Message( message == \"Hello    World\" )\n" + // <<- DIFF
                        "then\n" +
                        "end\n"
                )
            );
        Assert.assertFalse(
                StringUtils.codeAwareEqualsIgnoreSpaces(
                        "package org.drools.compiler\n" +
                        "rule Rx when\n" +
                        "   $m : Message( message == \"Hello' World\" )\n" +
                        "then\n" +
                        "end\n",
                        
                        "package org.drools.compiler\n" +  
                        "rule Rx when\n" +
                        "   $m : Message( message == \"Hello'    World\" )\n" + // <<- DIFF
                        "then\n" +
                        "end\n"
                )
            );
        Assert.assertFalse(
                StringUtils.codeAwareEqualsIgnoreSpaces(
                        "package org.drools.compiler\n" +
                        "rule Rx when\n" +
                        "   $m : Message( message == 'Hello World' )\n" +
                        "then\n" +
                        "end\n",
                        
                        "package org.drools.compiler\n" +  
                        "rule Rx when\n" +
                        "   $m : Message( message == 'Hello    World' )\n" + // <<- DIFF
                        "then\n" +
                        "end\n"
                )
            );
        Assert.assertFalse(
                StringUtils.codeAwareEqualsIgnoreSpaces(
                        "package org.drools.compiler\n" +
                        "rule Rx when\n" +
                        "   $m : Message( message == 'Hello\" World' )\n" +
                        "then\n" +
                        "end\n",
                        
                        "package org.drools.compiler\n" +  
                        "rule Rx when\n" +
                        "   $m : Message( message == 'Hello\"    World' )\n" + // <<- DIFF
                        "then\n" +
                        "end\n"
                )
            );
        Assert.assertFalse(
                StringUtils.codeAwareEqualsIgnoreSpaces(
                        "package org.drools.compiler\n" +
                        "rule Rx when\n" +
                        "   $m : Message( message == 'Hello\\' World' )\n" +
                        "then\n" +
                        "end\n",
                        
                        "package org.drools.compiler\n" +  
                        "rule Rx when\n" +
                        "   $m : Message( message == 'Hello\\'    World' )\n" + // <<- DIFF
                        "then\n" +
                        "end\n"
                )
            );
    }

    @Test
    public void test_indexOfOutOfQuotes() {
        assertEquals(0, indexOfOutOfQuotes("bla\"bla\"bla", "bla"));
        assertEquals(5, indexOfOutOfQuotes("\"bla\"bla", "bla"));
        assertEquals(-1, indexOfOutOfQuotes("\"bla\"", "bla"));
        assertEquals(0, indexOfOutOfQuotes("bla\"bla\"bla", "bla", 0));
        assertEquals(8, indexOfOutOfQuotes("bla\"bla\"bla", "bla", 1));
        assertEquals(-1, indexOfOutOfQuotes("bla\"bla\"bla", "bla", 9));
    }

    @Test
    public void getPkgUUIDFromReleaseIdNotNullNotSnapshot() {
        ReleaseId releaseId = new TestingReleaseId(false);
        String packageName = "apackage";
        String retrieved = getPkgUUID(releaseId, packageName);
        String expected = md5Hash(releaseId.toString()+packageName);
        assertEquals(expected, retrieved);
    }

    @Test
    public void getPkgUUIDFromReleaseIdNotNullSnapshot() {
        ReleaseId releaseId = new TestingReleaseId(true);
        String packageName = "apackage";
        String retrieved = getPkgUUID(releaseId, packageName);
        String unexpected = md5Hash(releaseId.toString()+packageName);
        assertNotEquals(unexpected, retrieved);
    }

    @Test
    public void getPkgUUIDFromReleaseIdNull() {
        ReleaseId releaseId = null;
        String packageName = "apackage";
        String retrieved = getPkgUUID(releaseId, packageName);
        assertNotNull(retrieved);
    }

    @Test
    public void getPkgUUIDFromGAV() {
        String gav = "group:artifact:version";
        String packageName = "apackage";
        String retrieved = getPkgUUID(gav, packageName);
        String expected = md5Hash(gav+packageName);
        assertEquals(expected, retrieved);
    }


    private static class TestingReleaseId implements ReleaseId {

        final boolean snapshot;

        public TestingReleaseId(boolean snapshot) {
            this.snapshot = snapshot;
        }

        @Override
        public String getGroupId() {
            return "group";
        }

        @Override
        public String getArtifactId() {
            return "artifact";
        }

        @Override
        public String getVersion() {
            return "version";
        }

        @Override
        public String toExternalForm() {
            return "externalForm";
        }

        @Override
        public boolean isSnapshot() {
            return snapshot;
        }

    }
}
