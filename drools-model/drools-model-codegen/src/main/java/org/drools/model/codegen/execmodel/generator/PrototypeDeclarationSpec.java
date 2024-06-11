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

package org.drools.model.codegen.execmodel.generator;

import java.util.Optional;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.prototype.PrototypeVariable;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.PROTOTYPE_FACT_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.PROTOTYPE_VARIABLE_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createProtoDslTopLevelMethod;

public class PrototypeDeclarationSpec implements DeclarationSpec {

    private final String bindingId;
    private final String prototypeType;
    private final boolean isGlobal;

    private MethodCallExpr bindingExpr;

    PrototypeDeclarationSpec(String bindingId, String prototypeType, boolean isGlobal) {
        this.bindingId = bindingId;
        this.prototypeType = prototypeType;
        this.isGlobal = isGlobal;
    }

    @Override
    public String getBindingId() {
        return bindingId;
    }

    @Override
    public Optional<String> getVariableName() {
        return Optional.empty();
    }

    @Override
    public Class<?> getDeclarationClass() {
        return null;
    }

    @Override
    public boolean isGlobal() {
        return isGlobal;
    }

    @Override
    public boolean isPrototypeDeclaration() {
        return true;
    }

    @Override
    public MethodCallExpr getBindingExpr() {
        return bindingExpr;
    }

    @Override
    public void setBindingExpr( MethodCallExpr bindingExpr ) {
        this.bindingExpr = bindingExpr;
    }

    @Override
    public void registerOnPackage(PackageModel packageModel, RuleContext context, BlockStmt ruleBlock) {
        ClassOrInterfaceType varType = toClassOrInterfaceType(PrototypeVariable.class);
        VariableDeclarationExpr varExpr = new VariableDeclarationExpr(varType, context.getVar(getBindingId()), Modifier.finalModifier());

        MethodCallExpr prototypeCall = createProtoDslTopLevelMethod(PROTOTYPE_FACT_CALL);
        prototypeCall.addArgument(new StringLiteralExpr(prototypeType));
        MethodCallExpr variableCall = createProtoDslTopLevelMethod(PROTOTYPE_VARIABLE_CALL);
        variableCall.addArgument(prototypeCall);

        AssignExpr varAssign = new AssignExpr(varExpr, variableCall, AssignExpr.Operator.ASSIGN);
        ruleBlock.addStatement(varAssign);
    }
}
