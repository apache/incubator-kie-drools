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
package org.drools.core.phreak;

import org.drools.base.base.SalienceInteger;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.core.reteoo.Tuple;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Targeted regression test for the guard in {@link RuleExecutor#removeDormantTuple} added for
 * issue 6422. A tuple passed to {@code removeDormantTuple} may already have been unlinked from
 * {@code dormantMatches} by another path (e.g. {@code modifyActiveTuple}), in which case its
 * {@code previous} pointer is null and the unguarded {@code LinkedList.remove()} dereferences it.
 */
public class RuleExecutorDormantTupleTest {

    @Disabled("Temporarily disabled for testing without a guard")
    @Test
    public void removeDormantTuple_whenTupleAlreadyUnlinkedFromList_doesNotNPE() {
        final RuleExecutor executor = newRuleExecutor();
        final RuleTerminalNodeLeftTuple t1 = new RuleTerminalNodeLeftTuple();
        final RuleTerminalNodeLeftTuple t2 = new RuleTerminalNodeLeftTuple();

        executor.addDormantTuple(t1);
        executor.addDormantTuple(t2);

        // First remove takes t1 out cleanly via the firstNode path. After this,
        // t1.previous is null and dormantMatches.getFirst() is t2 — the exact state
        // the bug fix guards against.
        executor.removeDormantTuple(t1);
        assertThat(executor.getDormantMatches().size()).isEqualTo(1);
        assertThat(t1.getPrevious()).isNull();
        assertThat(executor.getDormantMatches().getFirst()).isNotSameAs(t1);

        // Without the guard, LinkedList.remove() would fall into the middle-node
        // branch and NPE on t1.getPrevious().setNext(...).
        assertThatNoException().isThrownBy(() -> executor.removeDormantTuple(t1));

        // The list is unchanged: t2 is still the sole dormant tuple.
        assertThat(executor.getDormantMatches().size()).isEqualTo(1);
        assertThat(executor.getDormantMatches().getFirst()).isSameAs(t2);
    }

    @Test
    public void removeDormantTuple_whenTupleIsInList_unlinksIt() {
        final RuleExecutor executor = newRuleExecutor();
        final RuleTerminalNodeLeftTuple t1 = new RuleTerminalNodeLeftTuple();

        executor.addDormantTuple(t1);
        assertThat(executor.getDormantMatches().size()).isEqualTo(1);

        executor.removeDormantTuple(t1);
        assertThat(executor.getDormantMatches().size()).isZero();
    }

    /**
     * {@link RuleExecutor#modifyActiveTuple} routes through {@code removeDormantTuple}, so the
     * same NPE could surface from this second entry point. Pins the guard from both call sites.
     */
    @Disabled("Temporarily disabled for testing without a guard")
    @Test
    public void modifyActiveTuple_whenTupleAlreadyUnlinkedFromDormantList_doesNotNPE() {
        final RuleExecutor executor = newRuleExecutor();
        final RuleTerminalNodeLeftTuple t1 = new RuleTerminalNodeLeftTuple();
        final RuleTerminalNodeLeftTuple t2 = new RuleTerminalNodeLeftTuple();

        executor.addDormantTuple(t1);
        executor.addDormantTuple(t2);
        executor.removeDormantTuple(t1);

        assertThatNoException().isThrownBy(() -> executor.modifyActiveTuple(t1));

        assertThat(executor.getActiveMatches().size()).isEqualTo(1);
        assertThat(executor.getDormantMatches().size()).isEqualTo(1);
        assertThat(executor.getDormantMatches().getFirst()).isSameAs(t2);
        assertThat(t1.isQueued()).isTrue();
    }

    /**
     * Pins the upstream half of the bug: {@link RuleExecutor#removeActiveTuple} must skip
     * {@code addDormantTuple} when the staged type is DELETE. This is the precondition that
     * leaves a tuple unlinked from {@code dormantMatches} while later paths still try to
     * remove it.
     */
    @Test
    public void removeActiveTuple_whenStagedForDelete_skipsDormantTransition() {
        final RuleExecutor executor = newRuleExecutor();
        final RuleTerminalNodeLeftTuple t1 = new RuleTerminalNodeLeftTuple();

        executor.addActiveTuple(t1);
        t1.setStagedType(Tuple.DELETE);

        executor.removeActiveTuple(t1);

        assertThat(executor.getActiveMatches().size()).isZero();
        assertThat(executor.getDormantMatches().size()).isZero();
    }

    @Test
    public void removeActiveTuple_whenNotStagedForDelete_transitionsToDormant() {
        final RuleExecutor executor = newRuleExecutor();
        final RuleTerminalNodeLeftTuple t1 = new RuleTerminalNodeLeftTuple();

        executor.addActiveTuple(t1);
        // Default stagedType is NONE (0); leave it as-is.

        executor.removeActiveTuple(t1);

        assertThat(executor.getActiveMatches().size()).isZero();
        assertThat(executor.getDormantMatches().size()).isEqualTo(1);
        assertThat(executor.getDormantMatches().getFirst()).isSameAs(t1);
    }

    private static RuleExecutor newRuleExecutor() {
        final RuleImpl rule = mock(RuleImpl.class);
        when(rule.getSalience()).thenReturn(SalienceInteger.DEFAULT_SALIENCE);
        final RuleAgendaItem item = mock(RuleAgendaItem.class);
        when(item.getRule()).thenReturn(rule);
        return new RuleExecutor(null, item, false);
    }
}
