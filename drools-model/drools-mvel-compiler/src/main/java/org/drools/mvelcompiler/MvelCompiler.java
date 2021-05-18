/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvelcompiler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.mvel.parser.MvelParser;
import org.drools.mvel.parser.ast.expr.ModifyStatement;
import org.drools.mvel.parser.ast.expr.WithStatement;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;

import static java.util.stream.Collectors.toList;

public class MvelCompiler {

    private final PreprocessPhase preprocessPhase = new PreprocessPhase();
    private final StatementVisitor statementVisitor;
    private MvelCompilerContext mvelCompilerContext;

    public MvelCompiler(MvelCompilerContext mvelCompilerContext) {
        this.statementVisitor = new StatementVisitor(mvelCompilerContext);
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public CompiledBlockResult compileStatement(String mvelBlock) {

        BlockStmt mvelExpression = MvelParser.parseBlock(mvelBlock);

        preprocessPhase.removeEmptyStmt(mvelExpression);

        Set<String> allUsedBindings = new HashSet<>();

        List<String> modifyUsedBindings = mvelExpression.findAll(ModifyStatement.class)
                .stream()
                .flatMap(this::transformStatementWithPreprocessing)
                .collect(toList());

        List<String> withUsedBindings = mvelExpression.findAll(WithStatement.class)
                .stream()
                .flatMap(this::transformStatementWithPreprocessing)
                .collect(toList());

        allUsedBindings.addAll(modifyUsedBindings);
        allUsedBindings.addAll(withUsedBindings);

        // Entry point of the compiler
        TypedExpression compiledRoot = mvelExpression.accept(statementVisitor, null);
        allUsedBindings.addAll(mvelCompilerContext.getUsedBindings());

        Node javaRoot = compiledRoot.toJavaExpression();

        if(!(javaRoot instanceof BlockStmt)) {
            throw new MvelCompilerException("With a BlockStmt as a input I was expecting a BlockStmt output");
        }

        BlockStmt compiledBlockStatement = (BlockStmt) javaRoot;
        return new CompiledBlockResult(compiledBlockStatement.getStatements())
                .setUsedBindings(allUsedBindings);
    }

    private Stream<String> transformStatementWithPreprocessing(Statement s) {
        PreprocessPhase.PreprocessPhaseResult invoke = preprocessPhase.invoke(s);
        s.remove();
        return invoke.getUsedBindings().stream();
    }

}
