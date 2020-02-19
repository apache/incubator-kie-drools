/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.drlxparse;

import com.github.javaparser.ast.expr.BinaryExpr;
import org.drools.compiler.compiler.DroolsError;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.DRLIdGenerator;

public class DrlxParseFail implements DrlxParseResult {

    private DroolsError specificError;
    private String originalDrlConstraint;

    public DrlxParseFail() {
    }

    public DrlxParseFail(DroolsError specificError) {
        this.specificError = specificError;
    }

    @Override
    public void accept(ParseResultVoidVisitor parseVisitor) {
        parseVisitor.onFail(this);
    }

    @Override
    public <T> T acceptWithReturnValue(ParseResultVisitor<T> visitor) {
        return visitor.onFail(this);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public DrlxParseResult combineWith(DrlxParseResult other, BinaryExpr.Operator operator) {
        return this;
    }

    @Override
    public String getExprId(DRLIdGenerator exprIdGenerator) {
        return "invalidEpxr";
    }

    @Override
    public DrlxParseResult setOriginalDrlConstraint(String originalDrlConstraint) {
        this.originalDrlConstraint = originalDrlConstraint;
        return this;
    }

    public DroolsError getError() {
        if(specificError != null) {
            return specificError;
        } else {
            return new InvalidExpressionErrorResult("Unable to parse left part of expression: " + originalDrlConstraint);
        }
    }
}
