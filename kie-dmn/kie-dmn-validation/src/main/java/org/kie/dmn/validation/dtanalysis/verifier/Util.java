/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.validation.dtanalysis.verifier;

import java.util.List;
import java.util.stream.Collectors;

import org.drools.verifier.core.index.keys.Values;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.codegen.feel11.ProcessedExpression;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.ast.Visitor;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.validation.dtanalysis.DMNDTAnalyserValueFromNodeVisitor;

public class Util {

    private final org.kie.dmn.feel.FEEL feel;
    private final DMNDTAnalyserValueFromNodeVisitor valueFromNodeVisitor;
    private DMNDTAnalyserValueFromNodeVisitor.DMNDTAnalyserOutputClauseVisitor outputClauseVisitor;

    public Util(final FEEL feel,
                final DMNDTAnalyserValueFromNodeVisitor valueFromNodeVisitor,
                final DMNDTAnalyserValueFromNodeVisitor.DMNDTAnalyserOutputClauseVisitor outputClauseVisitor) {
        this.feel = feel;
        this.valueFromNodeVisitor = valueFromNodeVisitor;
        this.outputClauseVisitor = outputClauseVisitor;
    }

    public Values valuesFromNode(final BaseNode node) {
        if (node instanceof NullNode) {
            return Values.nullValue();
        } else {
            return new Values<>(valueFromNode(node, valueFromNodeVisitor));
        }
    }

    private Comparable<?> valueFromNode(final BaseNode node, Visitor<Comparable<?>> visitor) {
        return node.accept(visitor);
    }

    public Values valuesFromNode(LiteralExpression literalExpression) {
        final ProcessedExpression compileUnaryTests = (ProcessedExpression) feel.compile(literalExpression.getText(),
                                                                                         feel.newCompilerContext());
        final BaseNode baseNode = (BaseNode) compileUnaryTests.getInterpreted().getASTNode();
        return new Values<>(valueFromNode(baseNode, outputClauseVisitor));
    }

    public Values valuesFromUnaryTestListNode(UnaryTestListNode unaryTestListNodet) {
        List<? extends Comparable<?>> collect = unaryTestListNodet.getElements().stream().map(x -> valueFromNode(((UnaryTestNode) x).getValue(), valueFromNodeVisitor)).collect(Collectors.toList());
        return new Values<>(collect.toArray(new Comparable[collect.size()]));
    }
}
