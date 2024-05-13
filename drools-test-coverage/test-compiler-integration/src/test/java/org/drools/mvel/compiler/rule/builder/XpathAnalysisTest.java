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
package org.drools.mvel.compiler.rule.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.drools.drl.parser.lang.XpathAnalysis;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for syntactic analysis of XPath constraints.
 */
public class XpathAnalysisTest {

    @Test
    public void testEmptyInput() {
        final String xpath = "";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath has to start with '/'.").isEqualTo(true);
        assertThat(result.getError()).isNotNull();
        assertThat(result.iterator().hasNext()).isEqualTo(false);
    }

    @Test
    public void testNotAnXPath() {
        final String xpath = "someAttribute";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath has to start with '/'.").isEqualTo(true);
        assertThat(result.getError()).isNotNull();
        assertThat(result.iterator().hasNext()).isEqualTo(false);
    }

    @Test
    public void testEmptyXPath() {
        final String xpath = "/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The empty XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testEmptyNonReactiveXPath() {
        final String xpath = "?/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The empty XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, true, new ArrayList<String>(), null, -1, 0), iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testAttribute() {
        final String xpath = "/address";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testAttributeIterate() {
        final String xpath = "/address/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testAttributeDereferenceDot() {
        final String xpath = "/address.";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("", false, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testAttributeDereferenceMixed() {
        final String xpath = "/address.street/name";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", false, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("name", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testAttributeDereferenceMixedIterate() {
        final String xpath = "/address.street/name/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", false, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("name", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testRelativePathInCondition() {
        final String xpath = "/address.street[../city == \"The City\"]/name/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", false, false, new ArrayList<String>(List.of("../city == \"The City\"")), null, -1, 0),
                        iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("name", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testUnicode() {
        final String xpath = "/address.ulička[name == 'ěščřžýáíé']/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("ulička", false, false, new ArrayList<String>(List.of("name == 'ěščřžýáíé'")), null, -1, 0),
                        iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testIndex() {
        final String xpath = "/address[0]";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, 0, 0), iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testIndexIterate() {
        final String xpath = "/address[0]/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, 0, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testCondition() {
        final String xpath = "/address/street[name == \"Elm\"]";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", true, false, new ArrayList<String>(List.of("name == \"Elm\"")), null, -1, 0),
                        iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testThreeConditions() {
        final String xpath = "/address/street[name == \"Elm\", length <= 10, code == \"Something, \\\"and\\\" other thing\"]";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", true, false, new ArrayList<String>(Arrays.asList("name == \"Elm\"",
                                                                                                               "length <= 10",
                                                                                                               "code == \"Something, \\\"and\\\" other thing\"")),
                                                    null, -1, 0),
                iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testConditionIterate() {
        final String xpath = "/address/street[name == \"Elm\"]/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", true, false, new ArrayList<String>(List.of("name == \"Elm\"")), null, -1, 0),
                iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testBasicCast() {
        final String xpath = "/address/street#MyStreetType[name.value == \"Elm\"].city";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", true, false, new ArrayList<String>(List.of("name.value == \"Elm\"")), "MyStreetType", -1, 0),
                iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("city", false, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    @Test
    public void testComplexCast() {
        final String xpath = "/address/street#MyStreetType[name.value == \"Elm\"].city#MyCityMoreSpecificType[ value ]";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertThat(result.hasError()).as("The XPath should be valid.").isEqualTo(false);
        assertThat(result.getError()).isNull();

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", true, false, new ArrayList<String>(List.of("name.value == \"Elm\"")), "MyStreetType", -1, 0),
                iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("city", false, false, new ArrayList<String>(List.of("value")), "MyCityMoreSpecificType", -1, 0),
                iterator.next());
        assertThat(iterator.hasNext()).isEqualTo(false);
    }

    private Iterator<XpathAnalysis.XpathPart> getNonEmptyIterator(final XpathAnalysis analysis) {
        final Iterator<XpathAnalysis.XpathPart> iterator = analysis.iterator();
        assertThat(iterator.hasNext()).isEqualTo(true);
        return iterator;
    }

    private void verifyXpathPart(final XpathAnalysis.XpathPart expected, final XpathAnalysis.XpathPart actual) {
        if (expected == null) {
            assertThat(actual).isNull();
        } else {
            assertThat(actual).isNotNull();
        }
        assertThat(actual.getField()).isEqualTo(expected.getField());
        assertThat(actual.isIterate()).isEqualTo(expected.isIterate());
        assertThat(actual.isLazy()).isEqualTo(expected.isLazy());
        assertThat(actual.getInlineCast()).isEqualTo(expected.getInlineCast());
        assertThat(actual.getIndex()).isEqualTo(expected.getIndex());
        assertThat(actual.getConstraints().toArray()).isEqualTo(expected.getConstraints().toArray());
    }
}
