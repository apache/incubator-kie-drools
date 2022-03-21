/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.constraint.streams.bavet.common.index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

class EqualsIndexerTest {

    @Test
    void getEmpty() {
        Indexer<UniTuple<String>, String> indexer = new EqualsIndexer<>();
        assertThat(indexer.get(new Object[] { "F", Integer.valueOf(40) }))
                .isNotNull()
                .isEmpty();
    }

    @Test
    void putTwice() {
        Indexer<UniTuple<String>, String> indexer = new EqualsIndexer<>();
        UniTuple<String> annTuple = new UniTuple<>("Ann-F-40");
        indexer.put(new Object[] { "F", Integer.valueOf(40) }, annTuple, "Ann value");
        assertThatThrownBy(() -> indexer.put(new Object[] { "F", Integer.valueOf(40) }, annTuple, "Ann value"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void removeTwice() {
        Indexer<UniTuple<String>, String> indexer = new EqualsIndexer<>();
        UniTuple<String> annTuple = new UniTuple<>("Ann-F-40");
        indexer.put(new Object[] { "F", Integer.valueOf(40) }, annTuple, "Ann value");

        UniTuple<String> ednaTuple = new UniTuple<>("Edna-F-40");
        assertThatThrownBy(() -> indexer.remove(new Object[] { "F", Integer.valueOf(40) }, ednaTuple))
                .isInstanceOf(IllegalStateException.class);
        assertThat(indexer.remove(new Object[] { "F", Integer.valueOf(40) }, annTuple))
                .isEqualTo("Ann value");
        assertThatThrownBy(() -> indexer.remove(new Object[] { "F", Integer.valueOf(40) }, annTuple))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void get() {
        Indexer<UniTuple<String>, String> indexer = new EqualsIndexer<>();

        UniTuple<String> annTuple = new UniTuple<>("Ann-F-40");
        indexer.put(new Object[] { "F", Integer.valueOf(40) }, annTuple, "Ann value");
        UniTuple<String> bethTuple = new UniTuple<>("Beth-F-30");
        indexer.put(new Object[] { "F", Integer.valueOf(30) }, bethTuple, "Beth value");
        indexer.put(new Object[] { "M", Integer.valueOf(40) }, new UniTuple<>("Carl-M-40"), "Carl value");
        indexer.put(new Object[] { "M", Integer.valueOf(30) }, new UniTuple<>("Dan-M-30"), "Dan value");
        UniTuple<String> ednaTuple = new UniTuple<>("Edna-F-40");
        indexer.put(new Object[] { "F", Integer.valueOf(40) }, ednaTuple, "Edna value");

        assertThat(indexer.get(new Object[] { "F", Integer.valueOf(40) }))
                .isNotNull()
                .containsOnlyKeys(annTuple, ednaTuple);
        assertThat(indexer.get(new Object[] { "F", Integer.valueOf(30) }))
                .isNotNull()
                .containsOnlyKeys(bethTuple);
        assertThat(indexer.get(new Object[] { "F", Integer.valueOf(20) }))
                .isNotNull()
                .isEmpty();
    }

}
