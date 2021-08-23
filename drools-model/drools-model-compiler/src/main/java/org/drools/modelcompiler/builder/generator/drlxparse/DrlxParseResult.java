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
import org.drools.modelcompiler.builder.generator.DRLIdGenerator;

public interface DrlxParseResult {

    void accept( ParseResultVoidVisitor visitor );

    <T> T acceptWithReturnValue( ParseResultVisitor<T> visitor );

    boolean isSuccess();

    DrlxParseResult combineWith( DrlxParseResult other, BinaryExpr.Operator operator);

    String getExprId(DRLIdGenerator exprIdGenerator);

    DrlxParseResult setOriginalDrlConstraint(String originalDrlConstraint);

    String getOriginalDrlConstraint();

    default boolean isOOPath() {
        return false;
    }
}
