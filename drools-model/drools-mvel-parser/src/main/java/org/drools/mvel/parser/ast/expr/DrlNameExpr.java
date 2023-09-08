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
package org.drools.mvel.parser.ast.expr;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Generated;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.resolution.Resolvable;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;

/**
 * Whenever a SimpleName is used in an expression, it is wrapped in DrlNameExpr.
 * <br/>In <code>int x = a + 3;</code> a is a SimpleName inside a DrlNameExpr.
 * @author Julio Vilmar Gesser
 */
public final class DrlNameExpr extends NameExpr implements NodeWithSimpleName<NameExpr>,
                                                           Resolvable<ResolvedValueDeclaration> {

    private int backReferencesCount;

    public DrlNameExpr() {
        this(null, new SimpleName(), 0);
    }

    public DrlNameExpr(final String name) {
        this(null, new SimpleName(name), 0);
    }

    @AllFieldsConstructor
    public DrlNameExpr(final SimpleName name, int backReferencesCount) {
        this(name.getTokenRange().orElse(null), name, backReferencesCount);
        setRange(name.getRange().orElse(null));
    }

    public DrlNameExpr(final SimpleName name) {
        this(name, 0);
    }

    /**
     * This constructor is used by the parser and is considered private.
     */
    @Generated("com.github.javaparser.generator.core.node.MainConstructorGenerator")
    public DrlNameExpr(TokenRange tokenRange, SimpleName name, int backReferencesCount) {
        super(tokenRange, name);
        setName(name);
        customInitialization();
        this.backReferencesCount = backReferencesCount;
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return ((DrlGenericVisitor<R, A>) v).visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        ((DrlVoidVisitor<A>) v).visit(this, arg);
    }

    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public int getBackReferencesCount() {
        return backReferencesCount;
    }
}
