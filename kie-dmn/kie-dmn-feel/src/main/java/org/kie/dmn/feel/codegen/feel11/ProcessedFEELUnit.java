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
package org.kie.dmn.feel.codegen.feel11;

import java.util.List;
import java.util.UUID;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.impl.CompiledExecutableExpression;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;

public abstract class ProcessedFEELUnit implements CompiledFEELExpression {

    public enum DefaultMode {
        Compiled,
        Interpreted;

        public static DefaultMode of(boolean doCompile) {
            return doCompile ? Compiled : Interpreted;
        }

    }

    protected final String packageName;
    protected final String expression;
    protected final String templateResource;
    protected final String templateClass;

    protected BaseNode ast;
    protected BlockStmt codegenResult;
    protected final SyntaxErrorListener errorListener = new SyntaxErrorListener();
    protected final CompilerBytecodeLoader compiler =
            new CompilerBytecodeLoader();

    ProcessedFEELUnit(String expression,
                      CompilerContext ctx,
                      List<FEELProfile> profiles,
                      String templateResource,
                      String templateClass) {

        this.expression = expression;
        this.packageName = generateRandomPackage();
        this.templateResource = templateResource;
        this.templateClass = templateClass;
    }

    public CompilationUnit getSourceCode() {
        ASTCompilerVisitor astVisitor = new ASTCompilerVisitor();
        BlockStmt directCodegenResult = getCodegenResult(astVisitor);
        return compiler.getCompilationUnit(
                templateResource,
                packageName,
                templateClass,
                expression,
                directCodegenResult,
                astVisitor.getLastVariableName());
    }

    public <T> T getCommonCompiled() {
        return compiler.compileUnit(
                        packageName,
                        templateClass,
                        getSourceCode());
    }

    protected FEEL_1_1Parser getFEELParser(String expression, CompilerContext ctx, List<FEELProfile> profiles) {
        FEELEventListenersManager eventsManager =
                new FEELEventListenersManager();

        eventsManager.addListeners(ctx.getListeners());
        eventsManager.addListener(errorListener);

        return FEELParser.parse(
                eventsManager,
                expression,
                ctx.getInputVariableTypes(),
                ctx.getInputVariables(),
                ctx.getFEELFunctions(),
                profiles,
                ctx.getFEELFeelTypeRegistry());
    }

    protected BlockStmt getCodegenResult(ASTCompilerVisitor astVisitor) {
        if (codegenResult == null) {
            if (errorListener.isError()) {
                return astVisitor.returnError(errorListener.event().getMessage());
            } else {
                try {
                    codegenResult = ast.accept(astVisitor);
                } catch (FEELCompilationError e) {
                    return astVisitor.returnError(e.getMessage());
                }
            }
        }
        return codegenResult;
    }

    private String generateRandomPackage() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return this.getClass().getPackage().getName() + ".gen" + uuid;
    }
}
