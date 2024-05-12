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
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.core.SessionConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.MultiInputNode;
import org.drools.core.reteoo.MultiInputNode.DynamicFilter;
import org.drools.core.reteoo.MultiInputNode.DynamicFilterProto;
import org.drools.core.reteoo.MultiInputNode.MultiInputNodeMemory;
import org.drools.core.reteoo.MultiInputNode.SignalAdapter;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.sequencing.Gates;
import org.drools.core.reteoo.sequencing.LogicCircuit;
import org.drools.core.reteoo.sequencing.LogicGate;
import org.drools.core.reteoo.sequencing.Sequence;
import org.drools.core.reteoo.sequencing.Sequence.Step;
import org.drools.core.reteoo.sequencing.Sequencer;
import org.drools.core.reteoo.sequencing.Sequencer.SequenceMemory;
import org.drools.core.reteoo.sequencing.Sequencer.SequencerMemory;
import org.drools.core.reteoo.sequencing.TerminatingSignalProcessor;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.util.LinkedList;
import org.drools.kiesession.rulebase.SessionsAwareKnowledgeBase;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
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
import org.kie.internal.conf.CompositeBaseConfiguration;

import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class PhreakSequencerSubsequenceTest {
    BuildContext          buildContext;

    Sequence seq0;
    Sequence seq1;
    Sequence seq2;



    JoinNode              joinNode;
    JoinNode              sinkNode;
    InternalWorkingMemory wm;
    BetaMemory bm;
    
    BetaMemory bm0;

    MultiInputNode mnode;

    private StatefulKnowledgeSessionImpl session;

    private MultiInputNodeMemory nodeMemory;

    private SequencerMemory sequencerMemory;


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

        LogicGate gate1 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0}, // B
                                        new int[] {0}, //
                                        0);
        gate1.setOutput(TerminatingSignalProcessor.get());
        LogicCircuit circuit1 = new LogicCircuit(mnode, gate1);

        LogicGate gate2 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0}, // B
                                        new int[] {0}, //
                                        0);
        gate2.setOutput(TerminatingSignalProcessor.get());
        LogicCircuit circuit2 = new LogicCircuit(mnode, gate2);

        LogicGate gate3 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0}, // B
                                        new int[] {0}, //
                                        0);
        gate3.setOutput(TerminatingSignalProcessor.get());
        LogicCircuit circuit3 = new LogicCircuit(mnode, gate3);

        LogicGate gate4 = new LogicGate((inputMask, sourceMask) -> Gates.and(inputMask, sourceMask),0,
                                        new int[] {0}, // B
                                        new int[] {0}, //
                                        0);
        gate4.setOutput(TerminatingSignalProcessor.get());
        LogicCircuit circuit4 = new LogicCircuit(mnode, gate4);

        seq1 = new Sequence(circuit1, circuit2);
        seq2 = new Sequence(circuit3, circuit4);

        seq0 = new Sequence(Step.of(seq1), Step.of(seq2));
        mnode.setSequencer(new Sequencer(mnode, seq0));
        mnode.setDynamicFilters( new DynamicFilterProto[] {bfilter});

        SessionsAwareKnowledgeBase kbase       = new SessionsAwareKnowledgeBase(buildContext.getRuleBase());
        SessionConfiguration       sessionConf = kbase.getSessionConfiguration();
        sessionConf.setOption(ThreadSafeOption.YES);

        session = (StatefulKnowledgeSessionImpl) kbase.newKieSession(sessionConf, null);

        InternalFactHandle   fhA0   = (InternalFactHandle) session.insert(new A(0));
        nodeMemory = session.getNodeMemory(mnode);
        LeftTuple            lt     = new LeftTuple(fhA0, mnode, true);
        lt.setContextObject(mnode.createSequencerMemory(nodeMemory));
        nodeMemory.getLeftTupleMemory().add(lt);
        sequencerMemory = (SequencerMemory) lt.getContextObject();
    }

    @Test
    public void testSubSequence() {
        ArrayList<SequenceMemory> stack = sequencerMemory.getSequenceStack();
        assertThat(stack.size()).isEqualTo(0);

        mnode.getSequencer().start(sequencerMemory);
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

    public static BuildContext createContext() {

        CompositeBaseConfiguration conf = (CompositeBaseConfiguration) RuleBaseFactory.newKnowledgeBaseConfiguration();

        KnowledgeBaseImpl rbase = new KnowledgeBaseImpl("ID",
                                                        conf );
        BuildContext buildContext = new BuildContext( rbase, Collections.emptyList() );

        RuleImpl rule = new RuleImpl( "rule1").setPackage( "org.pkg1" );
        InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage( "org.pkg1" );
        pkg.getDialectRuntimeRegistry().setDialectData( "java", new JavaDialectRuntimeData() );

        pkg.addRule( rule );
        buildContext.setRule( rule );

        return buildContext;
    }

}
