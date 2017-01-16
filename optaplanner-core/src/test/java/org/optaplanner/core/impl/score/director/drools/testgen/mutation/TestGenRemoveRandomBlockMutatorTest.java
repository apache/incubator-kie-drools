/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.score.director.drools.testgen.mutation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestGenRemoveRandomBlockMutatorTest {

    private static final int LIST_SIZE = 500;
    private ArrayList<Integer> list = new ArrayList<>();

    @Before
    public void setUp() {
        for (int i = 0; i < LIST_SIZE; i++) {
            list.add(i);
        }
    }

    @Test
    public void testRemoveAll() {
        TestGenRemoveRandomBlockMutator<Integer> m = new TestGenRemoveRandomBlockMutator<>(list);
        ArrayList<Integer> removed = new ArrayList<>();
        while (m.canMutate()) {
            assertTrue(m.canMutate());
            m.mutate();
            removed.addAll(m.getRemovedBlock());
        }
        assertFalse(m.canMutate());

        for (int i = 0; i < LIST_SIZE; i++) {
            assertTrue(removed.contains(list.get(i)));
        }
    }

    @Test
    public void testRevert() {
        TestGenRemoveRandomBlockMutator<Integer> m = new TestGenRemoveRandomBlockMutator<>(list);
        m.mutate();
        List<Integer> removedBlock = m.getRemovedBlock();
        m.revert();
        assertTrue(m.getResult().containsAll(removedBlock));
        assertEquals(LIST_SIZE, m.getResult().size());
    }

    @Test
    public void testImpossibleMutation() {
        TestGenRemoveRandomBlockMutator<Integer> m = new TestGenRemoveRandomBlockMutator<>(list);
        ArrayList<Integer> removed = new ArrayList<>();
        while (m.canMutate()) {
            m.mutate();
            removed.addAll(m.getRemovedBlock());
            m.revert();
        }

        for (int i = 0; i < LIST_SIZE; i++) {
            assertTrue(removed.contains(list.get(i)));
        }
    }

}
