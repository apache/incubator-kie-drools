/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.runtime.KieSession;

import static org.drools.core.reteoo.ReteDumper.getSinks;

public class ReteMemoryChecker {

    public static void checkNodeMemories(KieSession session) {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase)session.getKieBase();
        for (EntryPointNode entryPointNode : kbase.getRete().getEntryPointNodes().values()) {
            checkNodeMemory( (InternalWorkingMemory) session, entryPointNode );
        }
    }

    private static void checkNodeMemory(InternalWorkingMemory wm, BaseNode node) {
        if (node instanceof MemoryFactory) {
            Memory memory = wm.getNodeMemory( (MemoryFactory) node );
            if ( NodeTypeEnums.ObjectTypeNode == node.getType() ) {
                if ( !( memory instanceof ObjectTypeNode.ObjectTypeNodeMemory ) ) {
                    throw new RuntimeException( "Invalid memory type. Node: " + node + " has memory " + memory );
                }
            } else if ( NodeTypeEnums.LeftInputAdapterNode == node.getType() ) {
                if ( !( memory instanceof LeftInputAdapterNode.LiaNodeMemory ) ) {
                    throw new RuntimeException( "Invalid memory type. Node: " + node + " has memory " + memory );
                }
            } else if ( NodeTypeEnums.isBetaNode( node ) ) {
                if ( NodeTypeEnums.AccumulateNode == node.getType() ) {
                    if ( !( memory instanceof AccumulateNode.AccumulateMemory ) ) {
                        throw new RuntimeException( "Invalid memory type. Node: " + node + " has memory " + memory );
                    }
                } else if ( !( memory instanceof BetaMemory ) ) {
                    throw new RuntimeException( "Invalid memory type. Node: " + node + " has memory " + memory );
                }
            } else if ( NodeTypeEnums.FromNode == node.getType() ) {
                if ( !( memory instanceof FromNode.FromMemory ) ) {
                    throw new RuntimeException( "Invalid memory type. Node: " + node + " has memory " + memory );
                }
            } else if ( NodeTypeEnums.WindowNode == node.getType() ) {
                if ( !( memory instanceof WindowNode.WindowMemory ) ) {
                    throw new RuntimeException( "Invalid memory type. Node: " + node + " has memory " + memory );
                }
            } else if ( NodeTypeEnums.isTerminalNode( node ) ) {
                if ( !( memory instanceof PathMemory ) ) {
                    throw new RuntimeException( "Invalid memory type. Node: " + node + " has memory " + memory );
                }
                checkPathMemory((PathMemory)memory);
            }
        }

        Sink[] sinks = getSinks( node );
        if (sinks != null) {
            for (Sink sink : sinks) {
                if (sink instanceof BaseNode) {
                    checkNodeMemory( wm, (BaseNode) sink );
                }
            }
        }
    }

    private static void checkPathMemory( PathMemory pathMemory ) {
        SegmentMemory[] smems = pathMemory.getSegmentMemories();
        if ( !NodeTypeEnums.isLeftTupleSource( smems[0].getRootNode() ) ) {
            throw new RuntimeException( "The root node for path " + pathMemory + " has to be a LeftTupleSource but is a " + smems[0].getRootNode() );
        }
        if ( !NodeTypeEnums.isTerminalNode( smems[smems.length-1].getTipNode() ) ) {
            throw new RuntimeException( "The tip node for path " + pathMemory + " has to be a TerminalNode but is a " + smems[smems.length-1].getTipNode() );
        }
        for (int i = 0; i < smems.length; i++) {
            if (smems[i] == null) {
                throw new RuntimeException( "Missing segment in position " + i + " for " + pathMemory );
            }
            if (i != smems[i].getPos()) {
                throw new RuntimeException( "Segment " + smems[i] + " is expected to be in position " + i + " but it is in position " + smems[i].getPos() );
            }
        }
    }
}
