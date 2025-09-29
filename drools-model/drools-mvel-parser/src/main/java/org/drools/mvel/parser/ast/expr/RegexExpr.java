/*
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
package org.drools.mvel.parser.ast.expr;

import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

public final class RegexExpr extends LiteralExpr {

    private final BinaryExpr matchesEvaluationExpr;
    private final FieldDeclaration compiledRegexMember;

    @AllFieldsConstructor
    public RegexExpr(FieldDeclaration compiledRegexMember, BinaryExpr matchesEvaluationExpr) {
        super();

        this.matchesEvaluationExpr = matchesEvaluationExpr;
        this.compiledRegexMember = compiledRegexMember;
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        if (v instanceof CloneVisitor) {
            return (R) new RegexExpr(compiledRegexMember.clone(), matchesEvaluationExpr.clone());
        } else {
            return v.visit(matchesEvaluationExpr, arg);
        }
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(matchesEvaluationExpr, arg);
    }

    @Override
    public boolean remove(Node node) {
        if (node == null) {
            return false;
        }
        return super.remove(node);
    }

    public FieldDeclaration getCompiledRegexMember() {
        return compiledRegexMember;
    }
}
