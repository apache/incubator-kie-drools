/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.KnowledgeRuntime;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class ReteDumper {

    private ReteDumper() { }

    public static void dumpRete(KieBase kbase ) {
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
            dumpNode( entryPointNode, "", new HashSet<BaseNode>() );
        }
    }

    private static void dumpNode(BaseNode node, String ident, Set<BaseNode> visitedNodes ) {
        System.out.print(ident + node + " on " + node.getPartitionId());
        try {
            Object declaredMask = node.getClass().getMethod("getDeclaredMask").invoke(node);
            Object inferreddMask = node.getClass().getMethod("getInferredMask").invoke(node);
            System.out.print(" d "+declaredMask + " i " + inferreddMask);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // do nothing.
        }
        try {
            Object declaredMask = node.getClass().getMethod("getLeftDeclaredMask").invoke(node);
            Object inferreddMask = node.getClass().getMethod("getLeftInferredMask").invoke(node);
            System.out.print(" Ld "+declaredMask + " Li " + inferreddMask);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // do nothing.
        }
        try {
            Object declaredMask = node.getClass().getMethod("getRightDeclaredMask").invoke(node);
            Object inferreddMask = node.getClass().getMethod("getRightInferredMask").invoke(node);
            System.out.print(" Rd "+declaredMask + " Ri " + inferreddMask);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // do nothing.
        }
        System.out.print("\n");
        if (!visitedNodes.add( node )) {
            return;
        }
        Sink[] sinks = node.getSinks();
        if (sinks != null) {
            for (Sink sink : sinks) {
                if (sink instanceof BaseNode) {
                    dumpNode((BaseNode)sink, ident + "    ", visitedNodes);
                }
            }
        }
    }
}
