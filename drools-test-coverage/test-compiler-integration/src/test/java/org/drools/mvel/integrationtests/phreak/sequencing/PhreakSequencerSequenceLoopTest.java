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
import org.drools.base.reteoo.DynamicFilterProto;
import org.drools.base.reteoo.sequencing.signalprocessors.Gates;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicCircuit;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicGate;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.Sequence.LoopController;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.base.reteoo.sequencing.Sequencer;
import org.drools.base.reteoo.sequencing.steps.Step;
import org.drools.base.reteoo.sequencing.signalprocessors.TerminatingSignalProcessor;
import org.drools.kiesession.rulebase.SessionsAwareKnowledgeBase;
import org.drools.mvel.integrationtests.phreak.A;
import org.drools.mvel.integrationtests.phreak.B;
import org.drools.mvel.integrationtests.phreak.C;
import org.drools.mvel.integrationtests.phreak.sequencing.MultiInputNodeBuilder.AlphaConstraint;
import org.drools.mvel.integrationtests.phreak.sequencing.MultiInputNodeBuilder.Predicate1;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ThreadSafeOption;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class PhreakSequencerSequenceLoopTest extends AbstractPhreakSequencerSubsequenceTest {

    @Before
    public void setup() {
        buildContext = createContext();
        buildContext.getRuleBase().getRuleBaseConfiguration().setOption(EventProcessingOption.STREAM);

        MultiInputNodeBuilder builder = MultiInputNodeBuilder.create(buildContext);

        mnode = builder.buildNode(A.class, new Class[]{B.class, C.class});

        final ObjectType aObjectType = new ClassObjectType(A.class);
        final ObjectType bObjectType = new ClassObjectType(B.class);
        final ObjectType cObjectType = new ClassObjectType(C.class);

        final Pattern bpattern = new Pattern(0,
                                             bObjectType,
                                             "b" );
        bpattern.addConstraint(new AlphaConstraint( (Predicate1<B>) b -> b.getText().equals("b")));

        final Pattern cpattern = new Pattern(0,
                                             cObjectType,
                                             "c" );
        cpattern.addConstraint(new AlphaConstraint( (Predicate1<C>) c -> c.getText().equals("c")));

        DynamicFilterProto bfilter = new DynamicFilterProto((AlphaNodeFieldConstraint) bpattern.getConstraints().get(0), 0);
        DynamicFilterProto cfilter = new DynamicFilterProto((AlphaNodeFieldConstraint) cpattern.getConstraints().get(0), 1);

        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0}, // B
                                        new int[] {0}, //
                                        0);
        gate1.setOutput(TerminatingSignalProcessor.get());
        LogicCircuit circuit1 = new LogicCircuit(gate1);

        LogicGate gate2 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),1,
                                        new int[] {1}, // C
                                        new int[] {1}, //
                                        0);
        gate2.setOutput(TerminatingSignalProcessor.get());
        LogicCircuit circuit2 = new LogicCircuit(gate2);

        seq0 = new Sequence(0, Step.of(circuit1), Step.of(circuit2));
        mnode.setSequencer(new Sequencer(seq0));
        mnode.setDynamicFilters( new DynamicFilterProto[] {bfilter, cfilter});

        SessionsAwareKnowledgeBase kbase       = new SessionsAwareKnowledgeBase(buildContext.getRuleBase());
        SessionConfiguration       sessionConf = kbase.getSessionConfiguration();
        sessionConf.setOption(ThreadSafeOption.YES);

        createSession();
    }

    @Test
    public void testSequenceLoopConstraint() {
        seq0.setController(new LoopController(m -> m.getCount() < 2));

        ArrayList<SequenceMemory> stack = sequencerMemory.getSequenceStack();
        assertThat(stack.size()).isEqualTo(0);

        mnode.getSequencer().start(sequencerMemory, session);

        assertThat(sequencerMemory.getSequenceMemory(seq0).getCount()).isEqualTo(0);
        InternalFactHandle fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));
        assertThat(sequencerMemory.getSequenceMemory(seq0).getCount()).isEqualTo(0);
        InternalFactHandle fhC0 = (InternalFactHandle) session.insert(new C(0, "c"));
        assertThat(sequencerMemory.getSequenceMemory(seq0).getCount()).isEqualTo(1);

        InternalFactHandle fhB1 = (InternalFactHandle) session.insert(new B(0, "b"));
        assertThat(sequencerMemory.getSequenceMemory(seq0).getCount()).isEqualTo(1);
        InternalFactHandle fhC1 = (InternalFactHandle) session.insert(new C(0, "c"));
        assertThat(sequencerMemory.getSequenceMemory(seq0).getCount()).isEqualTo(2);

        InternalFactHandle fhB2 = (InternalFactHandle) session.insert(new B(0, "b"));
        assertThat(sequencerMemory.getSequenceMemory(seq0).getCount()).isEqualTo(2);
        InternalFactHandle fhC2 = (InternalFactHandle) session.insert(new C(0, "c"));
        assertThat(sequencerMemory.getSequenceMemory(seq0).getCount()).isEqualTo(3);

        // Sequence is ended, so this does nothing
        InternalFactHandle fhB4 = (InternalFactHandle) session.insert(new B(0, "b"));
        InternalFactHandle fhC4 = (InternalFactHandle) session.insert(new C(0, "c"));

        assertThat(sequencerMemory.getSequenceMemory(seq0).getCount()).isEqualTo(3);

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(-1); // terminated
    }

}
