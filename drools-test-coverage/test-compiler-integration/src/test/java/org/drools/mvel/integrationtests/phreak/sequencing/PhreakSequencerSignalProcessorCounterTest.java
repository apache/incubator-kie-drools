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
import org.drools.base.reteoo.sequencing.signalprocessors.LogicCircuit;
import org.drools.base.reteoo.sequencing.signalprocessors.ConditionalSignalCounter;
import org.drools.base.reteoo.sequencing.signalprocessors.Gates;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicGate;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicGateOutputSignalProcessor;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.base.reteoo.sequencing.signalprocessors.SignalIndex;
import org.drools.base.reteoo.sequencing.steps.Step;
import org.drools.base.reteoo.sequencing.signalprocessors.TerminatingSignalProcessor;
import org.drools.mvel.integrationtests.phreak.B;
import org.drools.mvel.integrationtests.phreak.C;
import org.drools.mvel.integrationtests.phreak.D;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PhreakSequencerSignalProcessorCounterTest extends AbstractPhreakSequencerSubsequenceTest {

    @Before
    public void setup() {
        initKBaseWithEmptyRule();
    }

    @Test
    public void testEventCountEqual() {
        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0}, // B
                                        new int[] {0},
                                        0);

        gate1.setOutput(TerminatingSignalProcessor.get());

        ConditionalSignalCounter counter = new ConditionalSignalCounter(0, 0, c -> c == 3);
        counter.setOutput(gate1);
        gate1.setInputSignalCounters(new ConditionalSignalCounter[] {counter});

        LogicCircuit circuit1 = new LogicCircuit(gate1);

        Sequence seq = new Sequence(0, Step.of(circuit1));
        seq.setFilters(new Pattern[]{bpattern, cpattern});
        rule.addSequence(seq);
        kbase.addPackage(pkg);

        createSession();

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0);
        InternalFactHandle fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));

        InternalFactHandle fhB1 = (InternalFactHandle) session.insert(new B(1, "b"));

        InternalFactHandle fhB2 = (InternalFactHandle) session.insert(new B(2, "b"));
    }

    @Test
    public void testEventCountLessThan() {
        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0, 1}, // B, C
                                        new int[] {0, 1},
                                        0);

        gate1.setOutput(TerminatingSignalProcessor.get());

        ConditionalSignalCounter counter = new ConditionalSignalCounter(0, 0, c -> c < 3);
        counter.setOutput(gate1);
        gate1.setInputSignalCounters(new ConditionalSignalCounter[] {counter });

        LogicCircuit circuit1 = new LogicCircuit(gate1);

        Sequence seq = new Sequence(0, Step.of(circuit1));
        seq.setFilters(new Pattern[]{bpattern, cpattern});
        rule.addSequence(seq);
        kbase.addPackage(pkg);

        createSession();
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0);
        InternalFactHandle fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));

        InternalFactHandle fhB1 = (InternalFactHandle) session.insert(new B(1, "b"));

        InternalFactHandle fhC0 = (InternalFactHandle) session.insert(new C(0, "c"));

        InternalFactHandle fhB2 = (InternalFactHandle) session.insert(new B(2, "b"));

        createSession(); // fail
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0);
        fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));

        fhB1 = (InternalFactHandle) session.insert(new B(1, "b"));

        fhB2 = (InternalFactHandle) session.insert(new B(2, "b"));
    }


    @Test
    public void testEventCountBetween() {
        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0, 1}, // B, C
                                        new int[] {0, 1},
                                        0);

        gate1.setOutput(TerminatingSignalProcessor.get());

        ConditionalSignalCounter counter = new ConditionalSignalCounter(0, 0, c -> c > 1 && c < 3);
        counter.setOutput(gate1);
        gate1.setInputSignalCounters(new ConditionalSignalCounter[] {counter });

        LogicCircuit circuit1 = new LogicCircuit(gate1);

        Sequence seq = new Sequence(0, Step.of(circuit1));
        seq.setFilters(new Pattern[]{bpattern, cpattern});
        rule.addSequence(seq);
        kbase.addPackage(pkg);

        createSession(); // pass

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0);
        InternalFactHandle fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));

        InternalFactHandle fhB1 = (InternalFactHandle) session.insert(new B(1, "b"));

        InternalFactHandle fhC0 = (InternalFactHandle) session.insert(new C(0, "c"));

        InternalFactHandle fhB2 = (InternalFactHandle) session.insert(new B(2, "b"));

        createSession(); // fail
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0);
        fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));

        fhB1 = (InternalFactHandle) session.insert(new B(1, "b"));

        fhB2 = (InternalFactHandle) session.insert(new B(2, "b"));
    }

    @Test
    public void testSingleOutputCounter() {
        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0, 1}, // B, C
                                        new int[] {0, 1},
                                        0);

        ConditionalSignalCounter counter = new ConditionalSignalCounter(0, 0, c -> c == 3);
        counter.setOutput(TerminatingSignalProcessor.get());
        gate1.setOutput(counter);

        LogicCircuit circuit1 = new LogicCircuit(gate1);

        Sequence seq = new Sequence(0, Step.of(circuit1));
        seq.setFilters(new Pattern[]{bpattern, cpattern});
        rule.addSequence(seq);
        kbase.addPackage(pkg);

        createSession(); // pass

        SequenceMemory sequenceMemory = sequencerMemory.getSequenceMemory(seq);

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0);
        assertThat(sequenceMemory.getCounterMemories()[counter.getCounterIndex()]).isEqualTo(0);
        InternalFactHandle fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));
        InternalFactHandle fhB1 = (InternalFactHandle) session.insert(new B(1, "b"));
        InternalFactHandle fhC0 = (InternalFactHandle) session.insert(new C(0, "c"));

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0);
        assertThat(sequenceMemory.getCounterMemories()[counter.getCounterIndex()]).isEqualTo(1);
        InternalFactHandle fhC1 = (InternalFactHandle) session.insert(new C(0, "c"));
        InternalFactHandle fhC2 = (InternalFactHandle) session.insert(new C(0, "c"));
        InternalFactHandle fhB3 = (InternalFactHandle) session.insert(new B(1, "b"));

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0);
        assertThat(sequenceMemory.getCounterMemories()[counter.getCounterIndex()]).isEqualTo(2);
        InternalFactHandle fhB4 = (InternalFactHandle) session.insert(new B(0, "b"));
        InternalFactHandle fhB5 = (InternalFactHandle) session.insert(new B(1, "b"));
        InternalFactHandle fhC3 = (InternalFactHandle) session.insert(new C(0, "c"));

        // now attempts next step, which is finished
        assertThat(sequenceMemory.getCounterMemories()[counter.getCounterIndex()]).isEqualTo(3);
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(-1); // terminated
    }

    @Test
    public void testCompositeInputAndOutputCounters() {
        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0, 1}, // B and C
                                        new int[] {0, 1}, // Each SignalAdapter must be in a unique index  for the Sequence
                                        0);

        LogicGate gate2 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),1,
                                        new int[] {2}, // D
                                        new int[] {2}, // D
                                        1);

        ConditionalSignalCounter counter1 = new ConditionalSignalCounter(0, 0, c -> c == 2);
        counter1.setOutput(new LogicGateOutputSignalProcessor(SignalIndex.of(gate2, 2)));
        gate1.setOutput(counter1);
        gate2.setInputGates(gate1);

        ConditionalSignalCounter counter2 = new ConditionalSignalCounter(0, 1, c -> c == 2);
        counter2.setOutput(TerminatingSignalProcessor.get());
        gate2.setOutput(counter2);

        LogicCircuit circuit1 = new LogicCircuit(gate1, gate2);

        Sequence seq = new Sequence(0, Step.of(circuit1));
        seq.setFilters(new Pattern[]{bpattern, cpattern, dpattern});
        rule.addSequence(seq);
        kbase.addPackage(pkg);

        // D First
        createSession();
        SequenceMemory sequenceMemory = sequencerMemory.getSequenceMemory(seq);

        InternalFactHandle fhD0 = (InternalFactHandle) session.insert(new D(0, "d"));
        assertThat(sequenceMemory.getCounterMemories()[counter1.getCounterIndex()]).isEqualTo(0);

        InternalFactHandle fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));
        InternalFactHandle fhC0 = (InternalFactHandle) session.insert(new C(0, "c"));
        assertThat(sequenceMemory.getCounterMemories()[counter1.getCounterIndex()]).isEqualTo(1);
        assertThat(sequenceMemory.getCounterMemories()[counter2.getCounterIndex()]).isEqualTo(0);

        InternalFactHandle fhB1 = (InternalFactHandle) session.insert(new B(0, "b"));
        InternalFactHandle fhC1 = (InternalFactHandle) session.insert(new C(0, "c"));
        assertThat(sequenceMemory.getCounterMemories()[counter1.getCounterIndex()]).isEqualTo(0);
        assertThat(sequenceMemory.getCounterMemories()[counter2.getCounterIndex()]).isEqualTo(1);

        InternalFactHandle fhB2 = (InternalFactHandle) session.insert(new B(0, "b"));
        InternalFactHandle fhC2 = (InternalFactHandle) session.insert(new C(0, "c"));
        assertThat(sequenceMemory.getCounterMemories()[counter1.getCounterIndex()]).isEqualTo(1);
        assertThat(sequenceMemory.getCounterMemories()[counter2.getCounterIndex()]).isEqualTo(1);

        InternalFactHandle fhB3 = (InternalFactHandle) session.insert(new B(0, "b"));
        InternalFactHandle fhC = (InternalFactHandle) session.insert(new C(0, "c"));
        assertThat(sequenceMemory.getCounterMemories()[counter1.getCounterIndex()]).isEqualTo(2);
        assertThat(sequenceMemory.getCounterMemories()[counter2.getCounterIndex()]).isEqualTo(1);

        // Needs a final D to terminate
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0);
        InternalFactHandle fhD1 = (InternalFactHandle) session.insert(new D(0, "d"));
        assertThat(sequenceMemory.getCounterMemories()[counter1.getCounterIndex()]).isEqualTo(0);
        assertThat(sequenceMemory.getCounterMemories()[counter2.getCounterIndex()]).isEqualTo(2);
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(-1); // terminated
    }
}
