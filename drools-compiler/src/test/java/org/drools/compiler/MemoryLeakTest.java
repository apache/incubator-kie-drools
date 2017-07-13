/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Predicate;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.NodeMemories;
import org.drools.core.common.TupleSets;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.JoinNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Rete;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.SegmentMemory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.*;

public class MemoryLeakTest {

    @Test
    public void testStagedTupleLeak() throws Exception {
        // BZ-1056599
        String str =
                "rule R1 when\n" +
                "    $i : Integer()\n" +
                "then\n" +
                "    insertLogical( $i.toString() );\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    $i : Integer()\n" +
                "then\n" +
                "    delete( $i );\n" +
                "end\n" +
                "\n" +
                "rule R3 when\n" +
                "    $l : Long()\n" +
                "    $s : String( this == $l.toString() )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = new KieHelper().addContent( str, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();

        for ( int i = 0; i < 10; i++ ) {
            ksession.insert( i );
            ksession.fireAllRules();
        }

        Rete rete = ( (KnowledgeBaseImpl) kbase ).getRete();
        JoinNode joinNode = null;
        for ( ObjectTypeNode otn : rete.getObjectTypeNodes() ) {
            if ( String.class == otn.getObjectType().getValueType().getClassType() ) {
                joinNode = (JoinNode) otn.getObjectSinkPropagator().getSinks()[0];
                break;
            }
        }

        assertNotNull( joinNode );
        InternalWorkingMemory wm = (InternalWorkingMemory) ksession;
        BetaMemory memory = (BetaMemory) wm.getNodeMemory( joinNode );
        TupleSets<RightTuple> stagedRightTuples = memory.getStagedRightTuples();
        assertNull( stagedRightTuples.getDeleteFirst() );
        assertNull( stagedRightTuples.getInsertFirst() );
    }

    @Test
    public void testStagedLeftTupleLeak() throws Exception {
        // BZ-1058874
        String str =
                "rule R1 when\n" +
                "    String( this == \"this\" )\n" +
                "    String( this == \"that\" )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = new KieHelper().addContent( str, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();
        ksession.fireAllRules();

        for ( int i = 0; i < 10; i++ ) {
            FactHandle fh = ksession.insert( "this" );
            ksession.fireAllRules();
            ksession.delete( fh );
            ksession.fireAllRules();
        }

        Rete rete = ( (KnowledgeBaseImpl) kbase ).getRete();
        LeftInputAdapterNode liaNode = null;
        for ( ObjectTypeNode otn : rete.getObjectTypeNodes() ) {
            if ( String.class == otn.getObjectType().getValueType().getClassType() ) {
                AlphaNode alphaNode = (AlphaNode) otn.getObjectSinkPropagator().getSinks()[0];
                liaNode = (LeftInputAdapterNode) alphaNode.getObjectSinkPropagator().getSinks()[0];
                break;
            }
        }

        assertNotNull( liaNode );
        InternalWorkingMemory wm = (InternalWorkingMemory) ksession;
        LeftInputAdapterNode.LiaNodeMemory memory = (LeftInputAdapterNode.LiaNodeMemory) wm.getNodeMemory( liaNode );
        TupleSets<LeftTuple> stagedLeftTuples = memory.getSegmentMemory().getStagedLeftTuples();
        assertNull( stagedLeftTuples.getDeleteFirst() );
        assertNull( stagedLeftTuples.getInsertFirst() );
    }

    @Test
    public void testBetaMemoryLeakOnFactDelete() {
        // DROOLS-913
        String drl =
                "rule R1 when\n" +
                "    $a : Integer(this == 1)\n" +
                "    $b : String()\n" +
                "    $c : Integer(this == 2)\n" +
                "then \n" +
                "end\n" +
                "rule R2 when\n" +
                "    $a : Integer(this == 1)\n" +
                "    $b : String()\n" +
                "    $c : Integer(this == 3)\n" +
                "then \n" +
                "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        FactHandle fh1 = ksession.insert( 1 );
        FactHandle fh2 = ksession.insert( 3 );
        FactHandle fh3 = ksession.insert( "test" );
        ksession.fireAllRules();

        ksession.delete( fh1 );
        ksession.delete( fh2 );
        ksession.delete( fh3 );
        ksession.fireAllRules();

        NodeMemories nodeMemories = ( (InternalWorkingMemory) ksession ).getNodeMemories();
        for ( int i = 0; i < nodeMemories.length(); i++ ) {
            Memory memory = nodeMemories.peekNodeMemory( i );
            if ( memory != null && memory.getSegmentMemory() != null ) {
                SegmentMemory segmentMemory = memory.getSegmentMemory();
                System.out.println( memory );
                LeftTuple deleteFirst = memory.getSegmentMemory().getStagedLeftTuples().getDeleteFirst();
                if ( segmentMemory.getRootNode() instanceof JoinNode ) {
                    BetaMemory bm = (BetaMemory) segmentMemory.getNodeMemories().getFirst();
                    assertEquals( 0, bm.getLeftTupleMemory().size() );
                }
                System.out.println( deleteFirst );
                assertNull( deleteFirst );
            }
        }
    }

    @Test
    public void testLeakAfterSessionDispose() {
        // DROOLS-1655
        String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "    $p : Person()\n" +
                "then\n" +
                "end\n";

        KieContainer kContainer = new KieHelper().addContent( drl, ResourceType.DRL ).getKieContainer();
        KieSession ksession = kContainer.newKieSession();
        KieBase kBase = ksession.getKieBase();

        ksession.insert( new Person("Mario", 40) );
        ksession.fireAllRules();

        checkReachability( ksession, Person.class::isInstance, true );
        checkReachability( kBase, ksession::equals, true );
        checkReachability( kContainer, ksession::equals, true );

        ksession.dispose();

        checkReachability( ksession, Person.class::isInstance, false );
        checkReachability( kBase, ksession::equals, false );
        checkReachability( kContainer, ksession::equals, false );
    }

    private static void checkReachability( Object root, Predicate<Object> condition, boolean reachable ) {
        Collection<Object> results = collectObjects( root, condition );
        assertTrue( reachable ^ results.isEmpty() );
    }

    private static Collection<Object> collectObjects( Object root, Predicate<Object> condition ) {
        Collection<Object> results = new ArrayList<>();
        collectObjects( root, condition, results, Collections.newSetFromMap( new IdentityHashMap<>() ) );
        return results;
    }

    private static void collectObjects( Object root, Predicate<Object> condition, Collection<Object> results, Set<Object> visited ) {
        if (root == null) {
            return;
        }
        if (!visited.add(root)) {
            return;
        }
        if (condition.test(root)) {
            results.add(root);
        } else {
            if (root instanceof Object[]) {
                for (Object child: (Object[]) root) {
                    collectObjects(child, condition, results, visited);
                }
            }
            if (!root.getClass().isArray()) {// ignore primitive arrays
                for (Class c = root.getClass(); c != Object.class; c = c.getSuperclass()) {
                    for (Field field: c.getDeclaredFields()) {
                        if ( Modifier.isStatic( field.getModifiers() )) {
                            continue;
                        }
                        if (field.getType().isPrimitive()) {
                            continue;
                        }
                        field.setAccessible(true);
                        Object child;
                        try {
                            child = field.get(root);
                        } catch (IllegalAccessException e) {
                            throw new IllegalStateException(e);
                        }
                        collectObjects(child, condition, results, visited);
                    }
                }
            }
        }
    }
}
