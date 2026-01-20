/*
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
package org.kie.dmn.feel.lang.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UnaryTestNodeTest {

    @Test
    void testAreCollectionsEqualInOrder_withEqualCollections() {
        List<Integer> left = Arrays.asList(1, 2, 3);
        List<Integer> right = Arrays.asList(1, 2, 3);

        Boolean result = UnaryTestNode.areCollectionsEqual(left, right);
        assertThat(result).isTrue();
    }

    @Test
    void testAreCollectionsEqualInOrder_withDifferentSizes() {
        List<Integer> left = Arrays.asList(1, 2, 3);
        List<Integer> right = Arrays.asList(1, 2, 3, 4);

        Boolean result = UnaryTestNode.areCollectionsEqual(left, right);
        assertThat(result).isFalse();
    }

    @Test
    void testAreCollectionsEqualInOrder_withDifferentElements() {
        List<Integer> left = Arrays.asList(1, 2, 3);
        List<Integer> right = Arrays.asList(1, 5, 3);

        Boolean result = UnaryTestNode.areCollectionsEqual(left, right);
        assertThat(result).isFalse();
    }

    @Test
    void testAreCollectionsEqualInOrder_withDifferentOrder() {
        List<Integer> left = Arrays.asList(1, 2, 3);
        List<Integer> right = Arrays.asList(3, 2, 1);

        Boolean result = UnaryTestNode.areCollectionsEqual(left, right);
        assertThat(result).isFalse();
    }

    @Test
    void testAreCollectionsEqualInOrder_withEmptyCollections() {
        List<Integer> left = Collections.emptyList();
        List<Integer> right = Collections.emptyList();

        Boolean result = UnaryTestNode.areCollectionsEqual(left, right);
        assertThat(result).isTrue();
    }

    @Test
    void testAreCollectionsEqualInOrder_withNullElements() {
        List<Integer> left = Arrays.asList(1, null, 3);
        List<Integer> right = Arrays.asList(1, null, 3);

        Boolean result = UnaryTestNode.areCollectionsEqual(left, right);
        assertThat(result).isTrue();
    }

    @Test
    void testAreCollectionsEqualInOrder_withMismatchedNulls() {
        List<Integer> left = Arrays.asList(1, null, 3);
        List<Integer> right = Arrays.asList(1, 2, 3);

        Boolean result = UnaryTestNode.areCollectionsEqual(left, right);
        assertThat(result).isFalse();
    }


    @Test
    void testAreCollectionsEqualInOrder_withEqualCollections1() {
        List<String> left = Arrays.asList("a", "b", "c");
        List<String> right = Arrays.asList("a", "b", "c");

        Boolean result = UnaryTestNode.areCollectionsEqual(left, right);
        assertThat(result).isTrue();
    }

    @Test
    void testAreCollectionsEqualInOrder_withEqualCollections2() {
        List<Integer> left = Arrays.asList(1, 2, 3);
        List<Integer> right = Arrays.asList(1, 2, 3);

        Boolean result = UnaryTestNode.areCollectionsEqual(left, right);
        assertThat(result).isTrue();
    }

    @Test
    void testAreCollectionsEqualInOrder_withEqualCollections3() {
        List<Integer> left = Arrays.asList(1, 2, 3);
        List<Integer> right = Arrays.asList(1, 2, 3);

        Boolean result = UnaryTestNode.areCollectionsEqual(left, right);
        assertThat(result).isTrue();
    }



    @Test
    void testCollectionContainsElement_whenElementExists() {
        List<Integer> collection = Arrays.asList(1, 2, 3, 4, 5);
        Integer element = 3;

        Boolean result = UnaryTestNode.isElementInCollection(collection, element);
        assertThat(result).isTrue();
    }

    @Test
    void testCollectionContainsElement_whenElementDoesNotExist() {
        List<Integer> collection = Arrays.asList(1, 2, 3, 4, 5);
        Integer element = 10;

        Boolean result = UnaryTestNode.isElementInCollection(collection, element);
        assertThat(result).isFalse();
    }

    @Test
    void testCollectionContainsElement_withNullElement() {
        List<Integer> collection = Arrays.asList(1, null, 3);
        Object element = null;

        Boolean result = UnaryTestNode.isElementInCollection(collection, element);
        assertThat(result).isTrue();
    }

    @Test
    void testCollectionContainsElement_withEmptyCollection() {
        List<Integer> collection = Collections.emptyList();
        Integer element = 1;

        Boolean result = UnaryTestNode.isElementInCollection(collection, element);
        assertThat(result).isFalse();
    }

    @Test
    void testAreElementsEqual_withEqualIntegers() {
        Integer left = 42;
        Integer right = 42;

        Boolean result = UnaryTestNode.areElementsEqual(left, right);
        assertThat(result).isTrue();
    }

    @Test
    void testAreElementsEqual_withDifferentIntegers() {
        Integer left = 42;
        Integer right = 24;

        Boolean result = UnaryTestNode.areElementsEqual(left, right);
        assertThat(result).isFalse();
    }

    @Test
    void testAreElementsEqual_withEqualStrings() {
        String left = "hello";
        String right = "hello";

        Boolean result = UnaryTestNode.areElementsEqual(left, right);
        assertThat(result).isTrue();
    }

    @Test
    void testAreElementsEqual_withDifferentStrings() {
        String left = "hello";
        String right = "world";

        Boolean result = UnaryTestNode.areElementsEqual(left, right);
        assertThat(result).isFalse();
    }

    @Test
    void testAreElementsEqual_withBothNull() {
        Object left = null;
        Object right = null;

        Boolean result = UnaryTestNode.areElementsEqual(left, right);
        assertThat(result).isTrue();
    }

    @Test
    void testAreElementsEqual_withOneNull() {
        Integer left = 42;
        Object right = null;

        Boolean result = UnaryTestNode.areElementsEqual(left, right);
        assertThat(result).isFalse();
    }

    @Test
    void testAreElementsEqual_withNullLeft() {
        Object left = null;
        Integer right = 42;

        Boolean result = UnaryTestNode.areElementsEqual(left, right);
        assertThat(result).isFalse();
    }
}