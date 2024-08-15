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
package org.drools.mvel.integrationtests.phreak.sequencing;

import org.drools.base.rule.Pattern;
import org.drools.core.common.InternalFactHandle;
import org.drools.base.reteoo.sequencing.signalprocessors.Gates;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicCircuit;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicGate;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.steps.Step;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.base.reteoo.sequencing.signalprocessors.TerminatingSignalProcessor;
import org.drools.mvel.integrationtests.phreak.B;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class PhreakSequencerSubsequenceTest extends AbstractPhreakSequencerSubsequenceTest {

    @Before
    public void setup() {
        initKBaseWithEmptyRule();

        LogicGate gate1 = get1InputLogicGate();
        gate1.setOutput(TerminatingSignalProcessor.get());
        LogicCircuit circuit1 = new LogicCircuit(gate1);

        LogicGate gate2 = get1InputLogicGate();
        gate2.setOutput(TerminatingSignalProcessor.get());
        LogicCircuit circuit2 = new LogicCircuit(gate2);

        LogicGate gate3 = get1InputLogicGate();
        gate3.setOutput(TerminatingSignalProcessor.get());
        LogicCircuit circuit3 = new LogicCircuit(gate3);

        LogicGate gate4 = get1InputLogicGate();
        gate4.setOutput(TerminatingSignalProcessor.get());
        LogicCircuit circuit4 = new LogicCircuit(gate4);

        seq1 = new Sequence(1, Step.of(circuit1), Step.of(circuit2));
        seq2 = new Sequence(2, Step.of(circuit3), Step.of(circuit4));

        seq0 = new Sequence(0, Step.of(seq1), Step.of(seq2));
        seq0.setFilters(new Pattern[]{bpattern});
        rule.addSequence(seq0);
        kbase.addPackage(pkg);

        createSession2();
    }

    private static LogicGate get1InputLogicGate() {
        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0}, // B
                                        new int[] {0}, //
                                        0);
        return gate1;
    }

    @Test
    public void testSubSequence() {
        ArrayList<SequenceMemory> stack = sequencerMemory.getSequenceStack();
        assertThat(stack.size()).isEqualTo(2);

        assertThat(stack.get(0).getSequence()).isSameAs(seq0);
        assertThat(stack.get(1).getSequence()).isSameAs(seq1);
        assertThat(sequencerMemory.getCurrentSequence().getSequence()).isSameAs(seq1);
        assertThat(sequencerMemory.getCurrentStep()).isSameAs(0);

        InternalFactHandle fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));
        assertThat(sequencerMemory.getCurrentStep()).isSameAs(1);

        // After this B it should transition to the next step, which is a subsequence
        InternalFactHandle fhB1 = (InternalFactHandle) session.insert(new B(0, "b"));
        assertThat(stack.get(0).getSequence()).isSameAs(seq0);
        assertThat(stack.get(1).getSequence()).isSameAs(seq2);
        assertThat(sequencerMemory.getCurrentSequence().getSequence()).isSameAs(seq2);
        assertThat(sequencerMemory.getCurrentStep()).isSameAs(0);

        InternalFactHandle fhB2 = (InternalFactHandle) session.insert(new B(0, "b"));
        assertThat(stack.get(0).getSequence()).isSameAs(seq0);
        assertThat(stack.get(1).getSequence()).isSameAs(seq2);
        assertThat(sequencerMemory.getCurrentSequence().getSequence()).isSameAs(seq2);
        assertThat(sequencerMemory.getCurrentStep()).isSameAs(1);

        InternalFactHandle fhB3 = (InternalFactHandle) session.insert(new B(0, "b"));
        assertThat(stack.isEmpty()).isTrue();

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(-1); // terminated
    }

}
