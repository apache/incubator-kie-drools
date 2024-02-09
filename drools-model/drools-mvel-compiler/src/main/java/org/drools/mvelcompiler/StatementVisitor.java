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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvel.parser.printer.PrintUtil;
import org.drools.mvelcompiler.ast.BlockStmtT;
import org.drools.mvelcompiler.ast.DoStmtT;
import org.drools.mvelcompiler.ast.ForEachDowncastStmtT;
import org.drools.mvelcompiler.ast.ForEachStmtT;
import org.drools.mvelcompiler.ast.ForStmtT;
import org.drools.mvelcompiler.ast.IfStmtT;
import org.drools.mvelcompiler.ast.SwitchEntryT;
import org.drools.mvelcompiler.ast.SwitchStmtT;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.ast.UnalteredTypedExpression;
import org.drools.mvelcompiler.ast.WhileStmtT;
import org.drools.mvelcompiler.context.Declaration;
import org.drools.mvelcompiler.context.MvelCompilerContext;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

public class StatementVisitor implements DrlGenericVisitor<TypedExpression, Void> {

    private MvelCompilerContext mvelCompilerContext;

    public StatementVisitor(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    @Override
    public TypedExpression visit(ExpressionStmt n, Void arg) {
        return compileMVEL(n);
    }

    private TypedExpression compileMVEL(Node n) {
        TypedExpression rhs = new RHSPhase(mvelCompilerContext).invoke(n);
        TypedExpression lhs = new LHSPhase(mvelCompilerContext, ofNullable(rhs)).invoke(n);

        Optional<TypedExpression> postProcessedRHS = new ReProcessRHSPhase(mvelCompilerContext).invoke(rhs, lhs);
        TypedExpression postProcessedLHS = postProcessedRHS.map(ppr -> new LHSPhase(mvelCompilerContext, of(ppr)).invoke(n)).orElse(lhs);

        return postProcessedLHS;
    }

    @Override
    public TypedExpression visit(ForEachStmt n, Void arg) {
        Expression iterable = n.getIterable();

        Optional<TypedExpression> convertedToDowncastStmt =
                iterable.toNameExpr()
                        .map(PrintUtil::printNode)
                        .flatMap(mvelCompilerContext::findDeclarations)
                        .filter(this::isDeclarationIterable)
                        .map(d -> toForEachDowncastStmtT(n, arg));

        if(convertedToDowncastStmt.isPresent()) {
            return convertedToDowncastStmt.get();
        }

        TypedExpression variableDeclarationExpr = new LHSPhase(mvelCompilerContext, Optional.empty()).invoke(n.getVariable());
        TypedExpression typedIterable = new RHSPhase(mvelCompilerContext).invoke(n.getIterable());
        TypedExpression body = n.getBody().accept(this, arg);

        return new ForEachStmtT(variableDeclarationExpr, typedIterable, body);
    }

    private ForEachDowncastStmtT toForEachDowncastStmtT(ForEachStmt n, Void arg) {
        TypedExpression child = this.visit((BlockStmt) n.getBody(), arg);
        return new ForEachDowncastStmtT(n.getVariable(), PrintUtil.printNode(n.getIterable().asNameExpr()), child);
    }

    @Override
    public TypedExpression visit(BlockStmt n, Void arg) {
        List<TypedExpression> compiledStatements = n.getStatements()
                .stream()
                .map(s -> s.accept(this, arg))
                .collect(Collectors.toList());

        return new BlockStmtT(compiledStatements);
    }

    @Override
    public TypedExpression visit(IfStmt n, Void arg) {
        TypedExpression typedCondition = new RHSPhase(mvelCompilerContext).invoke(n.getCondition());
        TypedExpression typedThen = n.getThenStmt().accept(this, arg);
        Optional<TypedExpression> typedElse = n.getElseStmt().map(e -> e.accept(this, arg));

        return new IfStmtT(typedCondition, typedThen, typedElse);
    }

    @Override
    public TypedExpression visit(WhileStmt n, Void arg) {
        TypedExpression typedCondition = new RHSPhase(mvelCompilerContext).invoke(n.getCondition());
        TypedExpression typedThen = n.getBody().accept(this, arg);

        return new WhileStmtT(typedCondition, typedThen);
    }

    @Override
    public TypedExpression visit(DoStmt n, Void arg) {
        TypedExpression typedCondition = new RHSPhase(mvelCompilerContext).invoke(n.getCondition());
        TypedExpression typedThen = n.getBody().accept(this, arg);

        return new DoStmtT(typedCondition, typedThen);
    }

    @Override
    public TypedExpression visit(ForStmt n, Void arg) {
        List<TypedExpression> typedInitialization = n.getInitialization().stream().map(this::compileMVEL).collect(Collectors.toList());
        Optional<TypedExpression> typedCompare = n.getCompare().map(c -> new RHSPhase(mvelCompilerContext).invoke(c));
        List<TypedExpression> typedUpdate = n.getUpdate().stream().map(this::compileMVEL).collect(Collectors.toList());
        TypedExpression body = n.getBody().accept(this, arg);

        return new ForStmtT(typedInitialization, typedCompare, typedUpdate, body);
    }

    @Override
    public TypedExpression visit(SwitchStmt n, Void arg) {
        TypedExpression typedSelector = new RHSPhase(mvelCompilerContext).invoke(n.getSelector());
        List<TypedExpression> typedEntries = n.getEntries().stream().map(e -> e.accept(this, arg)).collect(Collectors.toList());

        return new SwitchStmtT(typedSelector, typedEntries);
    }

    @Override
    public TypedExpression visit(SwitchEntry n, Void arg) {
        List<TypedExpression> typedStatements = n.getStatements().stream().map(this::compileMVEL).collect(Collectors.toList());

        return new SwitchEntryT(n.getLabels(), typedStatements);
    }

    private boolean isDeclarationIterable(Declaration declaration) {
        Class<?> declarationClazz = declaration.getClazz();
        return Iterable.class.isAssignableFrom(declarationClazz);
    }

    @Override
    public TypedExpression defaultMethod(Node n, Void context) {
        return new UnalteredTypedExpression(n);
    }
}
