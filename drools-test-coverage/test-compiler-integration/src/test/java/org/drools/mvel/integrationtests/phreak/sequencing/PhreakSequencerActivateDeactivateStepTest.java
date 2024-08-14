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
import org.drools.base.reteoo.DynamicFilter;
import org.drools.base.reteoo.sequencing.Sequence.SequenceMemory;
import org.drools.base.reteoo.DynamicFilterProto;
import org.drools.base.reteoo.SignalAdapter;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicCircuit;
import org.drools.base.reteoo.sequencing.signalprocessors.Gates;
import org.drools.base.reteoo.sequencing.signalprocessors.LogicGate;
import org.drools.base.reteoo.sequencing.Sequence;
import org.drools.base.reteoo.sequencing.Sequencer;
import org.drools.base.reteoo.sequencing.steps.Step;
import org.drools.base.reteoo.sequencing.signalprocessors.TerminatingSignalProcessor;
import org.drools.base.util.LinkedList;
import org.drools.kiesession.rulebase.SessionsAwareKnowledgeBase;
import org.drools.mvel.integrationtests.phreak.A;
import org.drools.mvel.integrationtests.phreak.B;
import org.drools.mvel.integrationtests.phreak.C;
import org.drools.mvel.integrationtests.phreak.D;
import org.drools.mvel.integrationtests.phreak.sequencing.MultiInputNodeBuilder.AlphaConstraint;
import org.drools.mvel.integrationtests.phreak.sequencing.MultiInputNodeBuilder.Predicate1;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ThreadSafeOption;

import static org.assertj.core.api.Assertions.assertThat;

public class PhreakSequencerActivateDeactivateStepTest extends AbstractPhreakSequencerSubsequenceTest {

    private SequenceMemory sequenceMemory;

    @Before
    public void setup() {
        buildContext = createContext();
        buildContext.getRuleBase().getRuleBaseConfiguration().setOption(EventProcessingOption.STREAM);

        MultiInputNodeBuilder builder = MultiInputNodeBuilder.create(buildContext);

        mnode = builder.buildNode(A.class, new Class[]{B.class, C.class, D.class});

        final ObjectType aObjectType = new ClassObjectType(A.class);
        final ObjectType bObjectType = new ClassObjectType(B.class);
        final ObjectType cObjectType = new ClassObjectType(C.class);
        final ObjectType dObjectType = new ClassObjectType(D.class);

        final Pattern bpattern = new Pattern(0,
                                             bObjectType,
                                             "b" );
        bpattern.addConstraint(new AlphaConstraint( (Predicate1<B>) b -> b.getText().equals("b")));

        final Pattern cpattern = new Pattern(0,
                                             cObjectType,
                                             "c" );
        cpattern.addConstraint(new AlphaConstraint( (Predicate1<C>) c -> c.getText().equals("c")));

        final Pattern dpattern = new Pattern(0,
                                             dObjectType,
                                             "d" );
        dpattern.addConstraint(new AlphaConstraint( (Predicate1<D>) c -> c.getText().equals("d")));

        DynamicFilterProto bfilter = new DynamicFilterProto((AlphaNodeFieldConstraint) bpattern.getConstraints().get(0), 0);
        DynamicFilterProto cfilter = new DynamicFilterProto((AlphaNodeFieldConstraint) cpattern.getConstraints().get(0), 1);
        DynamicFilterProto dfilter = new DynamicFilterProto((AlphaNodeFieldConstraint) dpattern.getConstraints().get(0), 2);

        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0, 1}, // B and C
                                        new int[] {0, 1}, // Each SignalAdapter must be in a unique index  for the Sequence
                                        0);
        gate1.setOutput(TerminatingSignalProcessor.get());

        LogicCircuit circuit1 = new LogicCircuit(gate1);

        LogicGate gate2 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),1,
                                        new int[] {1, 2}, // C and D, C Filter is re-used
                                        new int[] {2, 3}, // Each SignalAdapter must be in a unique index  for the Sequence
                                        0);
        gate2.setOutput(TerminatingSignalProcessor.get());

        LogicCircuit circuit2 = new LogicCircuit(gate2);

        seq0 = new Sequence(0, Step.of(circuit1), Step.of(circuit2));
        mnode.setSequencer(new Sequencer(seq0));
        mnode.setDynamicFilters( new DynamicFilterProto[] {bfilter, cfilter, dfilter});

        SessionsAwareKnowledgeBase kbase       = new SessionsAwareKnowledgeBase(buildContext.getRuleBase());
        SessionConfiguration       sessionConf = kbase.getSessionConfiguration();
        sessionConf.setOption(ThreadSafeOption.YES);

        createSession();

        sequenceMemory = sequencerMemory.getSequenceMemory(seq0);
    }

    @Test
    public void testInitialisedMemories() {
        // make sure these are empty
        assertThat(nodeMemory.getFilters()).usingRecursiveComparison().isEqualTo(new DynamicFilter[3]);
        assertThat(nodeMemory.getActiveFilters()).usingRecursiveComparison().isEqualTo(new LinkedList[3]);
        assertThat(nodeMemory.getActiveFilters()).usingRecursiveComparison().isEqualTo(new LinkedList[3]);
        assertThat(sequenceMemory.getActiveSignalAdapters()).usingRecursiveComparison().isEqualTo(new SignalAdapter[4]);
        assertThat(sequenceMemory.getSignalAdapters()).usingRecursiveComparison().isEqualTo(new SignalAdapter[4]);
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(-1); // sequence not yet started
    }

    @Test
    public void testActivateAfterFirstCircuitStep() {
        // activate LogicCircuit1 and check filters are adapters are created and made active
        mnode.getSequencer().start(sequencerMemory, session);

        DynamicFilter filter0 = nodeMemory.getFilters()[0]; // B
        DynamicFilter filter1 = nodeMemory.getFilters()[1]; // C
        // [2] D is null

        // DynamicFilters are created on demand from Protos
        assertThat(nodeMemory.getFilters()).usingRecursiveComparison().isEqualTo(new DynamicFilter[] {filter0, filter1, null });

        // Check correct activation of DynamicFilters
        assertThat(nodeMemory.getActiveFilters()[0].size()).isEqualTo(1); // B is active
        assertThat(nodeMemory.getActiveFilters()[0].getFirst()).isSameAs(filter0);
        assertThat(nodeMemory.getActiveFilters()[1].size()).isEqualTo(1); // C is active
        assertThat(nodeMemory.getActiveFilters()[1].getFirst()).isSameAs(filter1);
        assertThat(nodeMemory.getActiveFilters()[2]).isNull(); // D is not yet active

        // Check correct activation of SignalAdapters
        SignalAdapter signal0 = sequenceMemory.getSignalAdapters()[0];
        SignalAdapter signal1 = sequenceMemory.getSignalAdapters()[1];
        assertThat(sequenceMemory.getSignalAdapters()).usingRecursiveComparison().isEqualTo(new SignalAdapter[] {signal0, signal1, null, null });
        assertThat(sequenceMemory.getActiveSignalAdapters()).usingRecursiveComparison().isEqualTo(new SignalAdapter[] {signal0, signal1, null, null });
    }

    @Test
    public void testDeactivateAndActivateAfterSecondCircuitStep() {
        mnode.getSequencer().start(sequencerMemory, session);

        DynamicFilter filter0 = nodeMemory.getFilters()[0]; // B
        DynamicFilter filter1 = nodeMemory.getFilters()[1]; // C

        SignalAdapter signal0 = sequenceMemory.getSignalAdapters()[0];
        SignalAdapter signal1 = sequenceMemory.getSignalAdapters()[1];

        InternalFactHandle fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // still step 0

        InternalFactHandle fhC0 = (InternalFactHandle) session.insert(new C(0, "c"));
        // Next step, check everything was for LogicCircuit was de-activated and LogicCircuit1 was activated;
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(1); // Now step 1

        DynamicFilter filter2 = nodeMemory.getFilters()[2]; // D was created from its Proto

        assertThat(nodeMemory.getFilters()).usingRecursiveComparison().isEqualTo(new DynamicFilter[] {filter0, filter1, filter2 }); // all three now created

        // Check correct activation of DynamicFilters
        assertThat(nodeMemory.getActiveFilters()[0].size()).isEqualTo(0); // B is deactived and now empty
        assertThat(nodeMemory.getActiveFilters()[1].size()).isEqualTo(1); // C remains active
        assertThat(nodeMemory.getActiveFilters()[1].getFirst()).isSameAs(filter1);
        assertThat(nodeMemory.getActiveFilters()[2].size()).isEqualTo(1); // D is now active
        assertThat(nodeMemory.getActiveFilters()[2].getFirst()).isSameAs(filter2);

        // Check correct activation of SignalAdapters
        SignalAdapter signal2 = sequenceMemory.getSignalAdapters()[2];
        SignalAdapter signal3 = sequenceMemory.getSignalAdapters()[3];

        assertThat(sequenceMemory.getSignalAdapters()).usingRecursiveComparison().isEqualTo(new SignalAdapter[] {signal0, signal1, signal2, signal3 });
        assertThat(sequenceMemory.getActiveSignalAdapters()).usingRecursiveComparison().isEqualTo(new SignalAdapter[] {null, null, signal2, signal3 }); // 0 and 1 are no longer active

        // @formatter:on
    }

    @Test
    public void testDeactivateAfterEndStep() {
        mnode.getSequencer().start(sequencerMemory, session);

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        InternalFactHandle fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));
        InternalFactHandle fhC0 = (InternalFactHandle) session.insert(new C(0, "c"));
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(1); // step 1
        InternalFactHandle fhC1 = (InternalFactHandle) session.insert(new C(0, "c"));
        InternalFactHandle fhD0 = (InternalFactHandle) session.insert(new D(0, "d"));

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(-1); // Now step -1, which means it's finished.

        // make sure there are no active SignalAapters or active Filters.
        assertThat(sequenceMemory.getActiveSignalAdapters()).usingRecursiveComparison().isEqualTo(new SignalAdapter[] {null, null, null, null }); // 0 and 1 are no longer active
        assertThat(nodeMemory.getActiveFilters()[0].size()).isEqualTo(0);
        assertThat(nodeMemory.getActiveFilters()[1].size()).isEqualTo(0);
        assertThat(nodeMemory.getActiveFilters()[2].size()).isEqualTo(0);
    }
}
