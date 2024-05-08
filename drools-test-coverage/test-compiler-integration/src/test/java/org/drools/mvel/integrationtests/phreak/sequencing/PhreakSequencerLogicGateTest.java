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
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.DynamicFilterProto;
import org.drools.core.reteoo.sequencing.signalprocessors.LogicCircuit;
import org.drools.core.reteoo.sequencing.signalprocessors.Gates;
import org.drools.core.reteoo.sequencing.signalprocessors.LogicGate;
import org.drools.core.reteoo.sequencing.signalprocessors.LogicGateOutputSignalProcessor;
import org.drools.core.reteoo.sequencing.Sequence;
import org.drools.core.reteoo.sequencing.Sequencer;
import org.drools.core.reteoo.sequencing.signalprocessors.SignalIndex;
import org.drools.core.reteoo.sequencing.steps.Step;
import org.drools.core.reteoo.sequencing.signalprocessors.TerminatingSignalProcessor;
import org.drools.mvel.integrationtests.phreak.A;
import org.drools.mvel.integrationtests.phreak.B;
import org.drools.mvel.integrationtests.phreak.C;
import org.drools.mvel.integrationtests.phreak.D;
import org.drools.mvel.integrationtests.phreak.E;
import org.drools.mvel.integrationtests.phreak.sequencing.MultiInputNodeBuilder.AlphaConstraint;
import org.drools.mvel.integrationtests.phreak.sequencing.MultiInputNodeBuilder.Predicate1;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.conf.EventProcessingOption;

import static org.assertj.core.api.Assertions.assertThat;

public class PhreakSequencerLogicGateTest extends AbstractPhreakSequencerSubsequenceTest {

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
        final ObjectType eObjectType = new ClassObjectType(E.class);

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
        dpattern.addConstraint(new AlphaConstraint( (Predicate1<D>) d -> d.getText().equals("d")));

        final Pattern epattern = new Pattern(0,
                                             eObjectType,
                                             "e" );
        epattern.addConstraint(new AlphaConstraint( (Predicate1<E>) e -> e.getText().equals("e")));

        bfilter = new DynamicFilterProto((AlphaNodeFieldConstraint) bpattern.getConstraints().get(0), 0);
        cfilter = new DynamicFilterProto((AlphaNodeFieldConstraint) cpattern.getConstraints().get(0), 1);
        dfilter = new DynamicFilterProto((AlphaNodeFieldConstraint) dpattern.getConstraints().get(0), 2);
        efilter = new DynamicFilterProto((AlphaNodeFieldConstraint) epattern.getConstraints().get(0), 3);
    }

    @Test
    public void testAnd() {
        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0, 1}, // B and C
                                        new int[] {0, 1}, // Each SignalAdapter must be in a unique index  for the Sequence
                                        0);

        gate1.setOutput(TerminatingSignalProcessor.get());

        LogicCircuit circuit1 = new LogicCircuit(gate1);

        Sequence seq = new Sequence(0, Step.of(circuit1));
        mnode.setSequencer(new Sequencer(seq));
        mnode.setDynamicFilters( new DynamicFilterProto[] {bfilter, cfilter});

        createSession();

        mnode.getSequencer().start(sequencerMemory, session);
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        InternalFactHandle fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        InternalFactHandle fhC0 = (InternalFactHandle) session.insert(new C(0, "c"));
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(-1); // terminated

        // reverse B and C
        createSession();
        mnode.getSequencer().start(sequencerMemory, session);
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        fhC0 = (InternalFactHandle) session.insert(new C(0, "c"));

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(-1); // terminated
    }

    @Test
    public void testOr() {
        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.or(inputMask, sourceMask),0,
                                        new int[] {0, 1}, // B and C
                                        new int[] {0, 1}, // Each SignalAdapter must be in a unique index  for the Sequence
                                        0);

        gate1.setOutput(TerminatingSignalProcessor.get());

        LogicCircuit circuit1 = new LogicCircuit(gate1);

        Sequence seq = new Sequence(0, Step.of(circuit1));
        mnode.setSequencer(new Sequencer(seq));
        mnode.setDynamicFilters( new DynamicFilterProto[] {bfilter, cfilter});

        createSession();

        mnode.getSequencer().start(sequencerMemory, session);
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        InternalFactHandle fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(-1); // terminated

        // reverse B and C
        createSession();
        mnode.getSequencer().start(sequencerMemory, session);
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        InternalFactHandle fhC0 = (InternalFactHandle) session.insert(new C(0, "c"));

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(-1); // terminated
    }


    @Test
    public void testComposite() {
        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0, 1}, // B and C
                                        new int[] {0, 1}, // Each SignalAdapter must be in a unique index  for the Sequence
                                        0);

        LogicGate gate2 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),1,
                                        new int[] {2}, // D
                                        new int[] {2}, // D
                                        1);

        gate1.setOutput(new LogicGateOutputSignalProcessor(SignalIndex.of(gate2, 2))); // gate bits must come after signals
        gate2.setOutput(TerminatingSignalProcessor.get());
        gate2.setInputGates(gate1);

        LogicCircuit circuit1 = new LogicCircuit(gate1, gate2);

        Sequence seq = new Sequence(0, Step.of(circuit1));
        mnode.setSequencer(new Sequencer(seq));
        mnode.setDynamicFilters( new DynamicFilterProto[] {bfilter, cfilter, dfilter});

        // D last
        createSession();
        mnode.getSequencer().start(sequencerMemory, session);
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        InternalFactHandle fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        InternalFactHandle fhC0 = (InternalFactHandle) session.insert(new C(0, "c"));
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0

        // now it'll transition
        InternalFactHandle fhD0 = (InternalFactHandle) session.insert(new D(0, "d"));
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(-1); // terminated

        // change order, D first
        createSession();
        mnode.getSequencer().start(sequencerMemory, session);
        fhD0 = (InternalFactHandle) session.insert(new D(0, "d"));
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0

        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0
        fhB0 = (InternalFactHandle) session.insert(new B(0, "b"));
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(0); // step 0

        // now it'll transition
        fhC0 = (InternalFactHandle) session.insert(new C(0, "c"));
        assertThat(sequencerMemory.getCurrentStep()).isEqualTo(-1); // terminated
    }

}
