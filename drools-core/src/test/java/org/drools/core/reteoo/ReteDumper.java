/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.KnowledgeRuntime;

import java.util.Collection;

public class ReteDumper {

    private ReteDumper() { }

    public static void dumpRete(KnowledgeBase kbase) {
        dumpRete((InternalKnowledgeBase) kbase);
    }

    public static void dumpRete(KnowledgeRuntime session) {
        dumpRete((InternalKnowledgeBase)session.getKieBase());
    }

    public static void dumpRete(KieSession session) {
        dumpRete((InternalKnowledgeBase)session.getKieBase());
    }

    public static void dumpRete(InternalKnowledgeBase kBase) {
        dumpRete(kBase.getRete());
    }

    public static void dumpRete(Rete rete) {
        for (EntryPointNode entryPointNode : rete.getEntryPointNodes().values()) {
            dumpNode( entryPointNode, "" );
        }
    }

    private static void dumpNode(BaseNode node, String ident) {
        System.out.println(ident + node);
        Sink[] sinks = null;
        if (node instanceof EntryPointNode) {
            EntryPointNode source = (EntryPointNode) node;
            Collection<ObjectTypeNode> otns = source.getObjectTypeNodes().values();
            sinks = otns.toArray(new Sink[otns.size()]);
        } else if (node instanceof ObjectSource) {
            ObjectSource source = (ObjectSource) node;
            sinks = source.getSinkPropagator().getSinks();
        } else if (node instanceof LeftTupleSource) {
            LeftTupleSource source = (LeftTupleSource) node;
            sinks = source.getSinkPropagator().getSinks();
        }
        if (sinks != null) {
            for (Sink sink : sinks) {
                if (sink instanceof BaseNode) {
                    dumpNode((BaseNode)sink, ident + "    ");
                }
            }
        }
    }
}
