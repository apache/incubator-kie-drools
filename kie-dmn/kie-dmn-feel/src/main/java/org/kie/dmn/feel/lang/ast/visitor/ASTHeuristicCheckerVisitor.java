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
package org.kie.dmn.feel.lang.ast.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.runtime.events.ASTHeuristicCheckEvent;
import org.kie.dmn.feel.util.Msg;

public class ASTHeuristicCheckerVisitor extends DefaultedVisitor<List<FEELEvent>> {

    @Override
    public List<FEELEvent> defaultVisit(ASTNode n) {
        List<FEELEvent> result = new ArrayList<>();
        for (ASTNode node : n.getChildrenNode()) {
            if (node != null) {
                result.addAll(node.accept(this));
            }
        }
        return result;
    }

    @Override
    public List<FEELEvent> visit(InfixOpNode n) {
        if (!(n.getLeft() instanceof UnaryTestNode || n.getLeft() instanceof RangeNode) && (n.getRight() instanceof UnaryTestNode || n.getRight() instanceof RangeNode)) {
            return List.of(new ASTHeuristicCheckEvent(Severity.WARN, Msg.createMessage(Msg.COMPARING_TO_UT, n.getOperator().symbol + " (" + n.getRight().getText()) + ")", n));
        }
        return defaultVisit(n);
    }

    @Override
    public List<FEELEvent> visit(UnaryTestNode n) {
        if (n.getValue() instanceof UnaryTestNode || n.getValue() instanceof RangeNode) {
            return List.of(new ASTHeuristicCheckEvent(Severity.WARN, Msg.createMessage(Msg.UT_OF_UT, n.getOperator().symbol + " (" + n.getValue().getText()) + ")", n));
        }
        return defaultVisit(n);
    }

    @Override
    public List<FEELEvent> visit(RangeNode n) {
        if ((n.getStart() instanceof NullNode && n.getEnd() instanceof RangeNode)
                || (n.getStart() instanceof RangeNode && n.getEnd() instanceof NullNode)) {
            return List.of(new ASTHeuristicCheckEvent(Severity.WARN, Msg.createMessage(Msg.UT_OF_UT, n.getText()), n));
        }
        return defaultVisit(n);
    }
}
