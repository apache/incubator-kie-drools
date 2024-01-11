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
package org.drools.mvelcompiler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.TextBlockLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.drools.mvel.parser.MvelParser;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.expr.ModifyStatement;
import org.drools.mvelcompiler.ast.MapGetExprT;
import org.drools.mvelcompiler.ast.MapPutExprT;

import static com.github.javaparser.ast.NodeList.nodeList;

// A special case of compiler in which
// * the modify statements are processed
// * multi line text blocks are converted to Strings
public class PreprocessCompiler {

    private static final PreprocessPhase preprocessPhase = new PreprocessPhase();

    public CompiledBlockResult compile(String mvelBlock, Set<String> prototypes) {

        BlockStmt mvelExpression = MvelParser.parseBlock(mvelBlock);

        preprocessPhase.removeEmptyStmt(mvelExpression);

        mvelExpression.findAll(TextBlockLiteralExpr.class).forEach(e -> {
            Optional<Node> parentNode = e.getParentNode();

            StringLiteralExpr stringLiteralExpr = preprocessPhase.replaceTextBlockWithConcatenatedStrings(e);

            parentNode.ifPresent(p -> {
                if (p instanceof VariableDeclarator) {
                    ((VariableDeclarator) p).setInitializer(stringLiteralExpr);
                } else if (p instanceof MethodCallExpr) {
                    // """exampleString""".formatted("arg0", 2);
                    ((MethodCallExpr) p).setScope(stringLiteralExpr);
                }
            });
        });

        Set<String> usedBindings = new HashSet<>();
        mvelExpression.findAll(ModifyStatement.class)
                .forEach(s -> {
                    Optional<Node> parentNode = s.getParentNode();
                    PreprocessPhase.PreprocessPhaseResult invoke = preprocessPhase.invoke(s);
                    usedBindings.addAll(invoke.getUsedBindings());
                    parentNode.ifPresent(p -> {
                        BlockStmt parentBlock = (BlockStmt) p;
                        for (String modifiedFact : invoke.getUsedBindings()) {
                            parentBlock.addStatement(new MethodCallExpr(null, "update", nodeList(new NameExpr(modifiedFact))));
                        }
                    });
                    s.remove();
                });

        if (!prototypes.isEmpty()) {
            rewriteConsequenceForPrototype(mvelExpression, prototypes);
        }

        return new CompiledBlockResult(mvelExpression.getStatements()).setUsedBindings(usedBindings);
    }

    private void rewriteConsequenceForPrototype(BlockStmt ruleConsequence, Set<String> prototypes) {
        for (AssignExpr assignExpr : ruleConsequence.findAll(AssignExpr.class)) {
            if (assignExpr.getTarget().isFieldAccessExpr()) {
                FieldAccessExpr fieldAccessExpr = assignExpr.getTarget().asFieldAccessExpr();
                String assignedVariable = getAssignedVariable(fieldAccessExpr);
                if (prototypes.contains(assignedVariable)) {
                    assignExpr.replace(new MapPutExprT(new NameExpr(assignedVariable), new StringLiteralExpr(fieldAccessExpr.getNameAsString()),
                                                       assignExpr.getValue(), Optional.empty()).toJavaExpression());
                }
            }
        }

        for (FieldAccessExpr fieldAccessExpr : ruleConsequence.findAll(FieldAccessExpr.class)) {
            String assignedVariable = getAssignedVariable(fieldAccessExpr);
            if (prototypes.contains(assignedVariable)) {
                fieldAccessExpr.replace( new MapGetExprT(new NameExpr(assignedVariable), fieldAccessExpr.getNameAsString() ).toJavaExpression() );
            }
        }
    }

    private static String getAssignedVariable(FieldAccessExpr fieldAccessExpr) {
        Expression scope = fieldAccessExpr.getScope();
        if (scope instanceof DrlNameExpr drlName) {
            return drlName.getName().toString();
        }
        return scope instanceof NameExpr ? scope.toString() : null;
    }
}
