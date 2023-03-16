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

import java.util.List;

import org.junit.Test;
import org.kie.api.builder.ReleaseId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.core.util.StringUtils.getPkgUUID;
import static org.drools.core.util.StringUtils.indexOfOutOfQuotes;
import static org.drools.core.util.StringUtils.md5Hash;
import static org.drools.core.util.StringUtils.splitStatements;

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
        assertThat(actualIndex).as("Expected and actual end of method args index for expr '" + strExpr + "' are not equal!").isEqualTo(expectedIndex);
    }
    
    @Test
    public void test_codeAwareEqualsIgnoreSpaces() {
        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(null, null)).isTrue();
        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces("", "")).isTrue();
        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces("", null)).isFalse();
        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(null, "")).isFalse();

        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(" ", "")).isTrue();
        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces("", " ")).isTrue();

        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(" ", "  ")).isTrue();

        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(
                "rule Rx when then end",
                " rule Rx  when then end " // <<- DIFF 3x 
        )).isTrue();
        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(
                "rule Rx when then end\n",
                " rule Rx  when then end\n " // <<- DIFF, both terminate with whitespace but different types
        )).isTrue();

        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(
                "package org.drools.compiler\n",

                "package org.drools.compiler\n " +
                        "rule Rx when\n" +
                        "   $m : Message( message == \"Hello World\" )\n" +
                        "then\n" +
                        "end\n"
        )).isFalse();

        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(
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
        )).isTrue();
        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(
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
        )).isTrue();
        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(
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
        )).isTrue();
        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(
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
        )).isTrue();
        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(
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
        )).isFalse();
        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(
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
        )).isFalse();
        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(
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
        )).isFalse();
        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(
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
        )).isFalse();
        assertThat(StringUtils.codeAwareEqualsIgnoreSpaces(
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
        )).isFalse();
    }

    @Test
    public void test_indexOfOutOfQuotes() {
        assertThat(indexOfOutOfQuotes("bla\"bla\"bla", "bla")).isEqualTo(0);
        assertThat(indexOfOutOfQuotes("\"bla\"bla", "bla")).isEqualTo(5);
        assertThat(indexOfOutOfQuotes("\"bla\"", "bla")).isEqualTo(-1);
        assertThat(indexOfOutOfQuotes("bla\"bla\"bla", "bla", 0)).isEqualTo(0);
        assertThat(indexOfOutOfQuotes("bla\"bla\"bla", "bla", 1)).isEqualTo(8);
        assertThat(indexOfOutOfQuotes("bla\"bla\"bla", "bla", 9)).isEqualTo(-1);
    }

    @Test
    public void getPkgUUIDFromReleaseIdNotNullNotSnapshot() {
        ReleaseId releaseId = new TestingReleaseId(false);
        String packageName = "apackage";
        String retrieved = getPkgUUID(releaseId, packageName);
        String expected = md5Hash(releaseId.toString()+packageName);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    public void getPkgUUIDFromReleaseIdNotNullSnapshot() {
        ReleaseId releaseId = new TestingReleaseId(true);
        String packageName = "apackage";
        String retrieved = getPkgUUID(releaseId, packageName);
        String unexpected = md5Hash(releaseId.toString()+packageName);
        assertThat(retrieved).isNotEqualTo(unexpected);
    }

    @Test
    public void getPkgUUIDFromReleaseIdNull() {
        ReleaseId releaseId = null;
        String packageName = "apackage";
        String retrieved = getPkgUUID(releaseId, packageName);
        assertThat(retrieved).isNotNull();
    }

    @Test
    public void getPkgUUIDFromGAV() {
        String gav = "group:artifact:version";
        String packageName = "apackage";
        String retrieved = getPkgUUID(gav, packageName);
        String expected = md5Hash(gav+packageName);
        assertThat(retrieved).isEqualTo(expected);
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

    @Test
    public void testSplitStatements() {
        String text =
                "System.out.println(\"'\");" +
                "$visaApplication.setValidation( Validation.FAILED );" +
                "drools.update($visaApplication);";
        List<String> statements = splitStatements(text);
        assertThat(statements.size()).isEqualTo(3);
        assertThat(statements.get(0)).isEqualTo("System.out.println(\"'\")");
        assertThat(statements.get(1)).isEqualTo("$visaApplication.setValidation( Validation.FAILED )");
        assertThat(statements.get(2)).isEqualTo("drools.update($visaApplication)");
    }
}
