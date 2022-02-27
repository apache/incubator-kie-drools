/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.constraint.drl.testgen.mutation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestGenRemoveRandomBlockMutatorTest {

    private static final int LIST_SIZE = 500;
    private ArrayList<Integer> list = new ArrayList<>();

    @BeforeEach
    void setUp() {
        for (int i = 0; i < LIST_SIZE; i++) {
            list.add(i);
        }
    }

    @Test
    void testRemoveAll() {
        TestGenRemoveRandomBlockMutator<Integer> m = new TestGenRemoveRandomBlockMutator<>(list);
        ArrayList<Integer> removed = new ArrayList<>();
        while (m.canMutate()) {
            assertThat(m.canMutate()).isTrue();
            m.mutate();
            removed.addAll(m.getRemovedBlock());
        }
        assertThat(m.canMutate()).isFalse();

        for (int i = 0; i < LIST_SIZE; i++) {
            assertThat(removed.contains(list.get(i))).isTrue();
        }
    }

    @Test
    void testRevert() {
        TestGenRemoveRandomBlockMutator<Integer> m = new TestGenRemoveRandomBlockMutator<>(list);
        m.mutate();
        List<Integer> removedBlock = m.getRemovedBlock();
        m.revert();
        assertThat(m.getResult().containsAll(removedBlock)).isTrue();
        assertThat(m.getResult().size()).isEqualTo(LIST_SIZE);
    }

    @Test
    void testImpossibleMutation() {
        TestGenRemoveRandomBlockMutator<Integer> m = new TestGenRemoveRandomBlockMutator<>(list);
        ArrayList<Integer> removed = new ArrayList<>();
        while (m.canMutate()) {
            m.mutate();
            removed.addAll(m.getRemovedBlock());
            m.revert();
        }

        for (int i = 0; i < LIST_SIZE; i++) {
            assertThat(removed.contains(list.get(i))).isTrue();
        }
    }

}
