/*
 * Copyright 2005 JBoss Inc
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
import org.kie.api.KieBase;

public class SegmentMemoryInitializer {

    public static void createAllSegmentPrototypes(KieBase kieBase) {
        InternalWorkingMemory wm = ( InternalWorkingMemory ) kieBase.newKieSession();
        Rete rete = (( InternalKnowledgeBase ) kieBase).getRete();
        for (EntryPointNode entryPointNode : rete.getEntryPointNodes().values()) {
            visitNode( wm, entryPointNode );
        }
    }

    private static void visitNode(InternalWorkingMemory wm, BaseNode node) {
        Sink[] sinks = node.getSinks();
        if (sinks != null) {
            for (Sink sink : sinks) {
                if ( sink instanceof LeftInputAdapterNode ) {
                    visitSegmentRoot(wm, (LeftInputAdapterNode) sink);
                } else if ( NodeTypeEnums.isBetaNode(sink) ) {
                    continue;
                } else if ( sink instanceof BaseNode ) {
                    visitNode(wm, (BaseNode) sink);
                }
            }
        }
    }

    private static void visitSegmentRoot( InternalWorkingMemory wm, LeftTupleSource rootNode) {
        Memory nodeMem = wm.getNodeMemory( (MemoryFactory) rootNode );
        SegmentMemory smem = nodeMem.getOrCreateSegmentMemory( rootNode, wm );
        BaseNode tipNode = (BaseNode) smem.getTipNode();

        Sink[] sinks = tipNode.getSinks();
        if (sinks != null) {
            for (Sink sink : sinks) {
                if ( sink instanceof LeftTupleSource ) {
                    visitSegmentRoot(wm, (LeftTupleSource) sink);
                }
            }
        }
    }
}
