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

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.core.SessionConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.DynamicFilterProto;
import org.drools.core.reteoo.sequencing.signalprocessors.Gates;
import org.drools.core.reteoo.sequencing.signalprocessors.LogicCircuit;
import org.drools.core.reteoo.sequencing.signalprocessors.LogicGate;
import org.drools.core.reteoo.sequencing.Sequence;
import org.drools.core.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.core.reteoo.sequencing.Sequencer;
import org.drools.core.reteoo.sequencing.steps.Step;
import org.drools.core.reteoo.sequencing.signalprocessors.TerminatingSignalProcessor;
import org.drools.core.util.CircularArrayList;
import org.drools.kiesession.rulebase.SessionsAwareKnowledgeBase;
import org.drools.mvel.integrationtests.phreak.A;
import org.drools.mvel.integrationtests.phreak.B;
import org.drools.mvel.integrationtests.phreak.sequencing.MultiInputNodeBuilder.AlphaConstraint;
import org.drools.mvel.integrationtests.phreak.sequencing.MultiInputNodeBuilder.Predicate1;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ThreadSafeOption;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class PhreakSequencerEventsMemoryTest extends AbstractPhreakSequencerSubsequenceTest {


    @Before
    public void setup() {
        buildContext = createContext();
        buildContext.getRuleBase().getRuleBaseConfiguration().setOption(EventProcessingOption.STREAM);

        MultiInputNodeBuilder builder = MultiInputNodeBuilder.create(buildContext);

        mnode = builder.buildNode(A.class, new Class[]{B.class});

        final ObjectType aObjectType = new ClassObjectType(A.class);
        final ObjectType bObjectType = new ClassObjectType(B.class);

        final Pattern bpattern = new Pattern(0,
                                             bObjectType,
                                             "b" );
        bpattern.addConstraint(new AlphaConstraint( (Predicate1<B>) b -> b.getText().equals("b")));

        DynamicFilterProto bfilter = new DynamicFilterProto((AlphaNodeFieldConstraint) bpattern.getConstraints().get(0), 0);

        LogicCircuit circuit1 = getLogicCircuit();
        LogicCircuit circuit2 = getLogicCircuit();
        LogicCircuit circuit3 = getLogicCircuit();
        LogicCircuit circuit4 = getLogicCircuit();
        LogicCircuit circuit5 = getLogicCircuit();
        LogicCircuit circuit6 = getLogicCircuit();

        seq1 = new Sequence(1, Step.of(circuit2));
        seq2 = new Sequence(2, Step.of(circuit4), Step.of(circuit5));

        seq0 = new Sequence(0, Step.of(circuit1), Step.of(seq1), Step.of(circuit3), Step.of(seq2), Step.of(circuit6));
        mnode.setSequencer(new Sequencer(seq0));
        mnode.setDynamicFilters( new DynamicFilterProto[] {bfilter});

        SessionsAwareKnowledgeBase kbase       = new SessionsAwareKnowledgeBase(buildContext.getRuleBase());
        SessionConfiguration       sessionConf = kbase.getSessionConfiguration();
        sessionConf.setOption(ThreadSafeOption.YES);

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
        ArrayList<SequenceMemory> stack = sequencerMemory.getSequenceStack();
        assertThat(stack.size()).isEqualTo(0);

        mnode.getSequencer().start(sequencerMemory, session);

        CircularArrayList<Object> events = sequencerMemory.getEvents();
        assertThat(events.size()).isEqualTo(0);
        InternalFactHandle fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));
        assertThat(to(events)).isEqualTo(new Object[] {0});

        InternalFactHandle fhB1 = (InternalFactHandle) session.insert(new B(1, "b"));
        assertThat(to(events)).isEqualTo(new Object[] {0}); // It's 0, because the subsequence of 1 input finished and it rewound.

        InternalFactHandle fhB2 = (InternalFactHandle) session.insert(new B(2, "b"));
        assertThat(to(events)).isEqualTo(new Object[] {0, 2});

        InternalFactHandle fhB3 = (InternalFactHandle) session.insert(new B(3, "b"));
        assertThat(to(events)).isEqualTo(new Object[] {0, 2, 3}); // This subsequence has two inputs, so its still in the subsequence.

        InternalFactHandle fhB4 = (InternalFactHandle) session.insert(new B(4, "b"));
        assertThat(to(events)).isEqualTo(new Object[] {0, 2}); // The subsequence has finished and it's rewound.

        InternalFactHandle fhB5 = (InternalFactHandle) session.insert(new B(5, "b"));
        assertThat(to(events)).isEqualTo(new Object[] {0, 2, 5}); // everything has finished and it's added the last input

        InternalFactHandle fhB6 = (InternalFactHandle) session.insert(new B(6, "b"));
        assertThat(to(events)).isEqualTo(new Object[] {0, 2, 5}); // nothing is added as the sequence is finished.

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(-1); // terminated
    }

    public Object[] to(CircularArrayList<Object> events) {
        Object[] facts = events.toArray();
        Object[] objs = new Object[facts.length];
        for(int i = 0; i < facts.length; i++) {
            if (facts[i] == null) {
                continue; // there are null entries sub sequence steps
            }
            objs[i] = ((B)((FactHandle)facts[i]).getObject()).getObject();
        }

        return  objs;
    }

}
