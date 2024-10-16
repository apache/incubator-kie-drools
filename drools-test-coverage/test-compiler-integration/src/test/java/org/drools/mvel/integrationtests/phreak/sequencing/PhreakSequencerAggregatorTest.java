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
import org.drools.base.reteoo.sequencing.Sequence.LoopController;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.base.reteoo.sequencing.steps.Step;
import org.drools.base.reteoo.sequencing.signalprocessors.TerminatingSignalProcessor;
import org.drools.base.util.CircularArrayList;
import org.drools.mvel.integrationtests.phreak.B;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class PhreakSequencerAggregatorTest extends AbstractPhreakSequencerSubsequenceTest {

    private List<Object> recorder = new ArrayList<>();

    @Before
    public void setup() {
        initKBaseWithEmptyRule();

        LogicCircuit circuit1 = getLogicCircuit();
        LogicCircuit circuit2 = getLogicCircuit();
        LogicCircuit circuit3 = getLogicCircuit();
        LogicCircuit circuit4 = getLogicCircuit();

        seq1 = new Sequence(1, Step.of(circuit2), Step.of(circuit3));
        seq1.setController(new LoopController(m -> m.getCount() < 2));

        Consumer<SequenceMemory> aggregator = memory -> {
            CircularArrayList<Object> events = memory.getSequencerMemory().getEvents();
            int eventsStartPosition = memory.getEventsStartPosition();

            List<Object> r = (List<Object>) events.get(eventsStartPosition-1);
            if (r == null) {
                events.set(eventsStartPosition-1,  recorder);
                r = (List<Object>) events.get(eventsStartPosition-1);
            }

            int end = events.size();
            for (int i = eventsStartPosition; i < end; i++) {
                r.add(((FactHandle)(events.get(i))).getObject());
            }
        };

        seq1.setOutputSize(1);
        seq0 = new Sequence(0, Step.of(circuit1), Step.of(seq1, aggregator), Step.of(circuit4));
        seq0.setFilters(new Pattern[]{bpattern});
        rule.addSequence(seq0);
        kbase.addPackage(pkg);

        createSession();
    }

    private LogicCircuit getLogicCircuit() {
        LogicGate gate1 = get1InputLogicGate();
        gate1.setOutput(TerminatingSignalProcessor.get());
        LogicCircuit circuit1 = new LogicCircuit(gate1);
        return circuit1;
    }

    private static LogicGate get1InputLogicGate() {
        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask), 0,
                                        new int[]{0}, // B
                                        new int[]{0}, //
                                        0);
        return gate1;
    }

    @Test
    public void testSequenceEventsMemory() {
        CircularArrayList<Object> events = sequencerMemory.getEvents();
        assertThat(events.size()).isEqualTo(0);
        InternalFactHandle fhB0 = (InternalFactHandle) session.insert(b(0));
        assertThat(recorder.isEmpty()).isTrue();
        InternalFactHandle fhB1 = (InternalFactHandle) session.insert(b(1));
        assertThat(recorder.isEmpty()).isTrue();
        InternalFactHandle fhB2 = (InternalFactHandle) session.insert(b(2));
        assertThat(recorder).containsExactly(b(1, 2));
        InternalFactHandle fhB3 = (InternalFactHandle) session.insert(b(3));
        assertThat(recorder).containsExactly(b(1, 2));
        InternalFactHandle fhB4 = (InternalFactHandle) session.insert(b(4));
        assertThat(recorder).containsExactly(b(1, 2, 3, 4));
        InternalFactHandle fhB5 = (InternalFactHandle) session.insert(b(5));
        assertThat(recorder).containsExactly(b(1, 2, 3, 4));
        InternalFactHandle fhB6 = (InternalFactHandle) session.insert(b(6));
        assertThat(recorder).containsExactly(b(1, 2, 3, 4, 5, 6));
        InternalFactHandle fhB7 = (InternalFactHandle) session.insert(b(7));

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(-1); // terminated
        assertThat(recorder).containsExactly(b(1, 2, 3, 4, 5, 6));
        InternalFactHandle fhB8 = (InternalFactHandle) session.insert(b(8));
        assertThat(recorder).containsExactly(b(1, 2, 3, 4, 5, 6));

        assertThat(events.size()).isEqualTo(3);
        assertThat(((FactHandle)events.get(0)).getObject()).isEqualTo(b(0));
        assertThat(events.get(1)).isSameAs(recorder);
        assertThat(((FactHandle)events.get(2)).getObject()).isEqualTo(b(7));
    }

    public B b(int i) {
        return new B(i, "b");
    }

    public B[] b(int... nums) {
        B[] bs = new B[nums.length];
        for (int i = 0; i < nums.length; i++) {
            bs[i] = new B(nums[i], "b");
        }

        return bs;
    }

    public Object[] to(CircularArrayList<FactHandle> events) {
        FactHandle[] facts = events.toArray();
        Object[] objs = new Object[facts.length];
        for(int i = 0; i < facts.length; i++) {
            objs[i] = ((B)facts[i].getObject()).getObject();
        }

        return  objs;
    }

}
