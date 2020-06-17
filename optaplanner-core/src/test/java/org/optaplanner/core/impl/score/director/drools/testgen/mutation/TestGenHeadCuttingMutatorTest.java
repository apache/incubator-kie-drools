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
package org.optaplanner.core.impl.score.director.drools.testgen.mutation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestGenHeadCuttingMutatorTest {

    private ArrayList<Integer> list = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        for (int i = 0; i < 25; i++) {
            list.add(i);
        }
    }

    @Test
    public void mutateUntilListIsEmpty() {
        TestGenHeadCuttingMutator<Integer> m = new TestGenHeadCuttingMutator<>(list);
        assertThat(m.canMutate()).isTrue();

        // 0.8 * 25 = 20 cut
        assertThat(m.mutate().size()).isEqualTo(5);
        assertThat(m.canMutate()).isTrue();
        m.revert();
        assertThat(m.getResult().size()).isEqualTo(25);

        assertThat(m.canMutate()).isTrue();
        // 0.4 * 25 = 10 cut
        assertThat(m.mutate().size()).isEqualTo(15);
        assertThat(m.canMutate()).isTrue();
        // 10 + 0.4 * 15 = 16 cut
        assertThat(m.mutate().size()).isEqualTo(9);
        assertThat(m.canMutate()).isTrue();
        // 16 + 0.4 * 9 = 19 cut
        assertThat(m.mutate().size()).isEqualTo(6);
        assertThat(m.canMutate()).isTrue();
        m.revert();
        assertThat(m.getResult().size()).isEqualTo(9);

        assertThat(m.canMutate()).isTrue();
        // 16 + 0.2 * 9 = 17 cut
        assertThat(m.mutate().size()).isEqualTo(8);
        assertThat(m.canMutate()).isTrue();
        assertThat(m.mutate().size()).isEqualTo(7);
        assertThat(m.canMutate()).isTrue();
        assertThat(m.mutate().size()).isEqualTo(6);
        assertThat(m.canMutate()).isTrue();
        assertThat(m.mutate().size()).isEqualTo(5);
        assertThat(m.canMutate()).isTrue();
        assertThat(m.mutate().size()).isEqualTo(4);
        assertThat(m.canMutate()).isTrue();
        assertThat(m.mutate().size()).isEqualTo(3);
        assertThat(m.canMutate()).isTrue();
        assertThat(m.mutate().size()).isEqualTo(2);
        assertThat(m.canMutate()).isTrue();
        assertThat(m.mutate().size()).isEqualTo(1);
        assertThat(m.canMutate()).isTrue();
        assertThat(m.mutate().size()).isEqualTo(0);
        assertThat(m.canMutate()).isFalse();
        assertThat(m.getResult().size()).isEqualTo(0);
    }

    @Test
    public void testImpossibleMutation() {
        TestGenHeadCuttingMutator<Integer> m = new TestGenHeadCuttingMutator<>(list);
        assertThat(m.canMutate()).isTrue();

        assertThat(m.canMutate()).isTrue();
        // 0.8 * 25 = 20 cut
        assertThat(m.mutate().size()).isEqualTo(5);
        m.revert();

        assertThat(m.canMutate()).isTrue();
        // 0.4 * 25 = 10 cut
        assertThat(m.mutate().size()).isEqualTo(15);
        m.revert();

        assertThat(m.canMutate()).isTrue();
        // 0.2 * 25 = 5 cut
        assertThat(m.mutate().size()).isEqualTo(20);
        m.revert();

        assertThat(m.canMutate()).isTrue();
        // 0.1 * 25 = 2 cut
        assertThat(m.mutate().size()).isEqualTo(23);
        m.revert();

        assertThat(m.canMutate()).isTrue();
        // 0.05 * 25 = 1 cut
        assertThat(m.mutate().size()).isEqualTo(24);
        m.revert();

        // impossible to mutate to full list
        assertThat(m.canMutate()).isFalse();
        assertThat(m.getResult().size()).isEqualTo(25);
    }
}
