/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import org.drools.core.InitialFact;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BaseNode;
import org.drools.core.common.NetworkNode;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.CompositePartitionAwareObjectSinkAdapter;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectSinkPropagator;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.TerminalNode;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.conf.MultithreadEvaluationOption;
import org.kie.internal.utils.KieHelper;

import static org.drools.core.reteoo.ReteDumper.getSinks;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class NodesPartitioningTest {

    @Test
    public void test2Partitions() {
        String drl = ruleA(1) + ruleB(2) + ruleC(2) + ruleD(1) +
                     ruleD(2) + ruleC(1) + ruleA(2) + ruleB(1);
        checkDrl( drl );
    }

    @Test
    public void testPartitioningWithSharedNodes() {
        StringBuilder sb = new StringBuilder( 400 );
        for (int i = 1; i < 4; i++) {
            sb.append( getRule( i ) );
        }
        for (int i = 1; i < 4; i++) {
            sb.append( getNotRule( i ) );
        }
        checkDrl( sb.toString() );
    }

    private void checkDrl(String drl) {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) new KieHelper().addContent( drl, ResourceType.DRL )
                                                                             .build( MultithreadEvaluationOption.YES );
        Rete rete = kbase.getRete();
        for (EntryPointNode entryPointNode : rete.getEntryPointNodes().values()) {
            traverse( entryPointNode );
        }
    }

    private void traverse(NetworkNode node ) {
        checkNode(node);
        Sink[] sinks = getSinks( node );
        if (sinks != null) {
            for (Sink sink : sinks) {
                if (sink instanceof BaseNode) {
                    traverse((BaseNode)sink);
                }
            }
        }
    }

    private void checkNode(NetworkNode node) {
        if (node instanceof EntryPointNode) {
            assertSame( RuleBasePartitionId.MAIN_PARTITION, node.getPartitionId() );
        } else if (node instanceof ObjectTypeNode) {
            assertSame( RuleBasePartitionId.MAIN_PARTITION, node.getPartitionId() );
            checkPartitionedSinks((ObjectTypeNode) node);
        } else if (node instanceof ObjectSource ) {
            ObjectSource source = ( (ObjectSource) node ).getParentObjectSource();
            if ( !(source instanceof ObjectTypeNode) ) {
                assertSame( source.getPartitionId(), node.getPartitionId() );
            }
        } else if (node instanceof BetaNode ) {
            ObjectSource rightInput = ( (BetaNode) node ).getRightInput();
            if ( !(rightInput instanceof ObjectTypeNode) ) {
                assertSame( rightInput.getPartitionId(), node.getPartitionId() );
            }
            LeftTupleSource leftInput = ( (BetaNode) node ).getLeftTupleSource();
            assertSame( leftInput.getPartitionId(), node.getPartitionId() );
        } else if (node instanceof TerminalNode ) {
            LeftTupleSource leftInput = ( (TerminalNode) node ).getLeftTupleSource();
            assertSame( leftInput.getPartitionId(), node.getPartitionId() );
        }
    }

    private void checkPartitionedSinks(ObjectTypeNode otn) {
        if ( InitialFact.class.isAssignableFrom( ( (ClassObjectType) otn.getObjectType() ).getClassType() ) ) {
            return;
        }
        CompositePartitionAwareObjectSinkAdapter sinkPropagator = (CompositePartitionAwareObjectSinkAdapter) otn.getObjectSinkPropagator();
        ObjectSinkPropagator[] propagators = sinkPropagator.getPartitionedPropagators();
        for (int i = 0; i < propagators.length; i++) {
            for (ObjectSink sink : propagators[i].getSinks()) {
                assertEquals( sink + " on " + sink.getPartitionId() + " is expcted to be on propagator " + i,
                              i, sink.getPartitionId().getId() % propagators.length );
            }
        }
    }

    private String ruleA(int i) {
        return "rule Ra" + i + " when\n" +
               "    $i : Integer( this == " + i + " )\n" +
               "    $s : String( length == $i )\n" +
               "    Integer( this == $s.length )\n" +
               "then end\n";
    }

    private String ruleB(int i) {
        return "rule Rb" + i + " when\n" +
               "    $i : Integer( this == " + i + " )\n" +
               "    $s : String( this == $i.toString )\n" +
               "    Integer( this == $s.length )\n" +
               "then end\n";
    }

    private String ruleC(int i) {
        return "rule Rc" + i + " when\n" +
               "    $i : Integer( this == " + i + " )\n" +
               "    $s : String( length == $i )\n" +
               "    Integer( this == $i+1 )\n" +
               "then end\n";
    }

    private String ruleD(int i) {
        return "rule Rd" + i + " when\n" +
               "    $i : Integer( this == " + i + " )\n" +
               "    $s : String( length == $i )\n" +
               "then end\n";
    }

    private String getRule(int i) {
        return  "rule R" + i + " when\n" +
                "    $i : Integer( this == " + i + " )" +
                "    String( this == $i.toString )\n" +
                "then end\n";
    }

    private String getNotRule(int i) {
        return  "rule Rnot" + i + " when\n" +
                "    String( this == \"" + i + "\" )\n" +
                "    not Integer( this == " + i + " )" +
                "then end\n";
    }
}
