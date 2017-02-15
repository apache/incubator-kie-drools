/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.rule.builder;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests for syntactic analysis of XPath constraints.
 */
public class XpathAnalysisTest {

    @Test
    public void testEmptyInput() {
        final String xpath = "";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath has to start with '/'.", true, result.hasError());
        assertNotNull(result.getError());
        assertEquals(false, result.iterator().hasNext());
    }

    @Test
    public void testNotAnXPath() {
        final String xpath = "someAttribute";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath has to start with '/'.", true, result.hasError());
        assertNotNull(result.getError());
        assertEquals(false, result.iterator().hasNext());
    }

    @Test
    public void testEmptyXPath() {
        final String xpath = "/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The empty XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, false, new ArrayList<String>(), null, -1), iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testEmptyNonReactiveXPath() {
        final String xpath = "?/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The empty XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, true, new ArrayList<String>(), null, -1), iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testAttribute() {
        final String xpath = "/address";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1), iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testAttributeIterate() {
        final String xpath = "/address/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, false, new ArrayList<String>(), null, -1), iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testAttributeDereferenceDot() {
        final String xpath = "/address.";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("", false, false, new ArrayList<String>(), null, -1), iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testAttributeDereferenceMixed() {
        final String xpath = "/address.street/name";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", false, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("name", true, false, new ArrayList<String>(), null, -1), iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testAttributeDereferenceMixedIterate() {
        final String xpath = "/address.street/name/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", false, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("name", true, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, false, new ArrayList<String>(), null, -1), iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testRelativePathInCondition() {
        final String xpath = "/address.street{../city == \"The City\"}/name/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", false, false, new ArrayList<String>(Arrays.asList("../city == \"The City\"")), null, -1),
                        iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("name", true, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, false, new ArrayList<String>(), null, -1), iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testUnicode() {
        final String xpath = "/address.ulička{name == 'ěščřžýáíé'}/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("ulička", false, false, new ArrayList<String>(Arrays.asList("name == 'ěščřžýáíé'")), null, -1),
                        iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, false, new ArrayList<String>(), null, -1), iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testIndex() {
        final String xpath = "/address[0]";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, 0), iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testIndexIterate() {
        final String xpath = "/address[0]/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, 0), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, false, new ArrayList<String>(), null, -1), iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testCondition() {
        final String xpath = "/address/street{name == \"Elm\"}";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", true, false, new ArrayList<String>(Arrays.asList("name == \"Elm\"")), null, -1),
                        iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testThreeConditions() {
        final String xpath = "/address/street{name == \"Elm\", length <= 10, code == \"Something, \\\"and\\\" other thing\"}";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", true, false, new ArrayList<String>(Arrays.asList("name == \"Elm\"",
                                                                                                               "length <= 10",
                                                                                                               "code == \"Something, \\\"and\\\" other thing\"")),
                                                    null, -1),
                iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testConditionIterate() {
        final String xpath = "/address/street{name == \"Elm\"}/";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", true, false, new ArrayList<String>(Arrays.asList("name == \"Elm\"")), null, -1),
                iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("", true, false, new ArrayList<String>(), null, -1), iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testConditionIndexAndDereference() {
        final String xpath = "/address/street[1]{name == \"Elm\"}.city";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", true, false, new ArrayList<String>(Arrays.asList("name == \"Elm\"")), null, 1),
                iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("city", false, false, new ArrayList<String>(), null, -1), iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testBasicCast() {
        final String xpath = "/address/street{#MyStreetType, name.value == \"Elm\"}.city";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", true, false, new ArrayList<String>(Arrays.asList("name.value == \"Elm\"")), "MyStreetType", -1),
                iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("city", false, false, new ArrayList<String>(), null, -1), iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    @Test
    public void testComplexCast() {
        final String xpath = "/address/street[1]{#MyStreetType, name.value == \"Elm\"}.city{#MyCityType, value, #MyCityMoreSpecificType}";
        final XpathAnalysis result = XpathAnalysis.analyze(xpath);

        assertEquals("The XPath should be valid.", false, result.hasError());
        assertNull(result.getError());

        final Iterator<XpathAnalysis.XpathPart> iterator = getNonEmptyIterator(result);
        verifyXpathPart(new XpathAnalysis.XpathPart("address", true, false, new ArrayList<String>(), null, -1), iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("street", true, false, new ArrayList<String>(Arrays.asList("name.value == \"Elm\"")), "MyStreetType", 1),
                iterator.next());
        verifyXpathPart(new XpathAnalysis.XpathPart("city", false, false, new ArrayList<String>(Arrays.asList("value")), "MyCityMoreSpecificType", -1),
                iterator.next());
        assertEquals(false, iterator.hasNext());
    }

    private Iterator<XpathAnalysis.XpathPart> getNonEmptyIterator(final XpathAnalysis analysis) {
        final Iterator<XpathAnalysis.XpathPart> iterator = analysis.iterator();
        assertEquals(true, iterator.hasNext());
        return iterator;
    }

    private void verifyXpathPart(final XpathAnalysis.XpathPart expected, final XpathAnalysis.XpathPart actual) {
        if (expected == null) {
            assertNull(actual);
        } else {
            assertNotNull(actual);
        }
        assertEquals(expected.getField(), actual.getField());
        assertEquals(expected.isIterate(), actual.isIterate());
        assertEquals(expected.isLazy(), actual.isLazy());
        assertEquals(expected.getInlineCast(), actual.getInlineCast());
        assertEquals(expected.getIndex(), actual.getIndex());
        assertArrayEquals(expected.getConstraints().toArray(), actual.getConstraints().toArray());
    }
}
