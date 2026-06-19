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
import org.drools.core.reteoo.RuleTerminalNodeLeftTuple.MatchState;
import org.drools.core.reteoo.Tuple;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RuleExecutorDormantTupleTest {

    @Test
    public void removeTuple_whenDormant_removesFromListAndSetsRemoved() {
        final RuleExecutor executor = newRuleExecutor();
        final RuleTerminalNodeLeftTuple t1 = new RuleTerminalNodeLeftTuple();

        executor.addDormantTuple(t1);
        assertThat(executor.getDormantMatches().size()).isEqualTo(1);
        assertThat(t1.getMatchState()).isEqualTo(MatchState.DORMANT);

        executor.removeTuple(t1);
        assertThat(executor.getDormantMatches().size()).isZero();
        assertThat(t1.getMatchState()).isEqualTo(MatchState.REMOVED);
    }

    @Test
    public void removeTuple_whenNone_setsRemovedWithoutNPE() {
        final RuleExecutor executor = newRuleExecutor();
        final RuleTerminalNodeLeftTuple t1 = new RuleTerminalNodeLeftTuple();
        assertThat(t1.getMatchState()).isEqualTo(MatchState.NONE);

        assertThatNoException().isThrownBy(() -> executor.removeTuple(t1));
        assertThat(t1.getMatchState()).isEqualTo(MatchState.REMOVED);
    }

    @Test
    public void modifyActiveTuple_whenDormant_movesToActive() {
        final RuleExecutor executor = newRuleExecutor();
        final RuleTerminalNodeLeftTuple t1 = new RuleTerminalNodeLeftTuple();

        executor.addDormantTuple(t1);
        executor.modifyActiveTuple(t1);

        assertThat(executor.getActiveMatches().size()).isEqualTo(1);
        assertThat(executor.getDormantMatches().size()).isZero();
        assertThat(t1.getMatchState()).isEqualTo(MatchState.ACTIVE);
        assertThat(t1.isQueued()).isTrue();
    }

    @Test
    public void modifyActiveTuple_whenNone_movesToActiveWithoutNPE() {
        final RuleExecutor executor = newRuleExecutor();
        final RuleTerminalNodeLeftTuple t1 = new RuleTerminalNodeLeftTuple();

        assertThatNoException().isThrownBy(() -> executor.modifyActiveTuple(t1));

        assertThat(executor.getActiveMatches().size()).isEqualTo(1);
        assertThat(t1.getMatchState()).isEqualTo(MatchState.ACTIVE);
        assertThat(t1.isQueued()).isTrue();
    }

    @Test
    public void removeActiveTuple_whenStagedForDelete_setsRemoved() {
        final RuleExecutor executor = newRuleExecutor();
        final RuleTerminalNodeLeftTuple t1 = new RuleTerminalNodeLeftTuple();

        executor.addActiveTuple(t1);
        t1.setStagedType(Tuple.DELETE);

        executor.removeActiveTuple(t1);

        assertThat(executor.getActiveMatches().size()).isZero();
        assertThat(executor.getDormantMatches().size()).isZero();
        assertThat(t1.getMatchState()).isEqualTo(MatchState.REMOVED);
    }

    @Test
    public void removeActiveTuple_whenNotStagedForDelete_transitionsToDormant() {
        final RuleExecutor executor = newRuleExecutor();
        final RuleTerminalNodeLeftTuple t1 = new RuleTerminalNodeLeftTuple();

        executor.addActiveTuple(t1);

        executor.removeActiveTuple(t1);

        assertThat(executor.getActiveMatches().size()).isZero();
        assertThat(executor.getDormantMatches().size()).isEqualTo(1);
        assertThat(executor.getDormantMatches().getFirst()).isSameAs(t1);
        assertThat(t1.getMatchState()).isEqualTo(MatchState.DORMANT);
    }

    private static RuleExecutor newRuleExecutor() {
        final RuleImpl rule = mock(RuleImpl.class);
        when(rule.getSalience()).thenReturn(SalienceInteger.DEFAULT_SALIENCE);
        final RuleAgendaItem item = mock(RuleAgendaItem.class);
        when(item.getRule()).thenReturn(rule);
        return new RuleExecutor(null, item, false);
    }
}
