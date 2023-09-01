/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.template.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SnippetBuilderTest {

    @Test
    public void testBuildSnippet() {
        final String snippet = "something.param.getAnother().equals($param);";
        final SnippetBuilder snip = new SnippetBuilder(snippet);
        final String cellValue = "$42";
        final String result = snip.build(cellValue);
        assertThat(result).isNotNull();

        assertThat(result).isEqualTo("something.param.getAnother().equals($42);");
    }

    @Test
    public void testBuildSnippetNoPlaceHolder() {
        final String snippet = "something.getAnother().equals(blah);";
        final SnippetBuilder snip = new SnippetBuilder(snippet);
        final String cellValue = "this is ignored...";
        final String result = snip.build(cellValue);

        assertThat(result).isEqualTo(snippet);
    }

    @Test
    public void testSingleParamMultipleTimes() {
        final String snippet = "something.param.getAnother($param).equals($param);";
        final SnippetBuilder snip = new SnippetBuilder(snippet);
        final String cellValue = "42";
        final String result = snip.build(cellValue);
        assertThat(result).isNotNull();

        assertThat(result).isEqualTo("something.param.getAnother(42).equals(42);");

    }

    @Test
    public void testMultiPlaceHolder() {
        final String snippet = "something.getAnother($1,$2).equals($2, '$2');";
        final SnippetBuilder snip = new SnippetBuilder(snippet);
        final String result = snip.build("x, y");
        assertThat(result).isEqualTo("something.getAnother(x,y).equals(y, 'y');");

    }

    @Test
    public void testMultiPlaceHolderSingle() {
        final String snippet = "something.getAnother($1).equals($1);";
        final SnippetBuilder snip = new SnippetBuilder(snippet);
        final String result = snip.build("x");
        assertThat(result).isEqualTo("something.getAnother(x).equals(x);");

    }

    @Test
    public void testStartWithParam() {
        final String snippet = "$1 goo $2";
        final SnippetBuilder snip = new SnippetBuilder(snippet);
        final String result = snip.build("x, y");
        assertThat(result).isEqualTo("x goo y");

    }

    @Test
    public void testMultiPlaceHolderEscapedComma() {
        final String snippet = "rulesOutputRouting.set( $1, $2, $3, $4, $5 );";
        final SnippetBuilder snip = new SnippetBuilder(snippet);
        final String result = snip.build("\"80\",\"Department Manager\",toa.getPersonExpense().getEntityCode(\"Part Of\",\"Office\"),10004,30");
        assertThat(result).isEqualTo("rulesOutputRouting.set( \"80\", \"Department Manager\", toa.getPersonExpense().getEntityCode(\"Part Of\",\"Office\"), 10004, 30 );");

    }

    @Test
    public void testForAllAnd() {
        final String snippet = "forall(&&){something == $}";
        final SnippetBuilder snip = new SnippetBuilder(snippet);
        final String result = snip.build("x");
        assertThat(result).isEqualTo("something == x");
    }

    @Test
    public void testForAllAndCSV() {
        final String snippet = "forall(&&){something == $}";
        final SnippetBuilder snip = new SnippetBuilder(snippet);
        final String result = snip.build("x, y");
        assertThat(result).isEqualTo("something == x && something == y");
    }

    @Test
    public void testForAllAndNone() {
        final String snippet = "forall(&&){something == $}";
        final SnippetBuilder snip = new SnippetBuilder(snippet);
        final String result = snip.build("");
        assertThat(result).isEqualTo("forall(&&){something == $}");
    }

    @Test
    public void testForAllAndCSVMultiple() {
        final String snippet = "forall(&&){something == $ || something == $}";
        final SnippetBuilder snip = new SnippetBuilder(snippet);
        final String result = snip.build("x, y");
        assertThat(result).isEqualTo("something == x || something == x && something == y || something == y");
    }

    @Test
    public void testForAllOr() {
        final String snippet = "forall(||){something == $}";
        final SnippetBuilder snip = new SnippetBuilder(snippet);
        final String result = snip.build("x");
        assertThat(result).isEqualTo("something == x");
    }

    @Test
    public void testForAllOrMultiple() {
        final String snippet = "forall(||){something == $} && forall(||){something < $}";
        final SnippetBuilder snip = new SnippetBuilder(snippet);
        final String result = snip.build("x, y");
        assertThat(result).isEqualTo("something == x || something == y && something < x || something < y");
    }

    @Test
    public void testForAllOrAndMultipleWithPrefix() {
        final String snippet = "something == this && forall(||){something == $} && forall(&&){something < $}";
        final SnippetBuilder snip = new SnippetBuilder(snippet);
        final String result = snip.build("x, y");
        assertThat(result).isEqualTo("something == this && something == x || something == y && something < x && something < y");
    }
}
