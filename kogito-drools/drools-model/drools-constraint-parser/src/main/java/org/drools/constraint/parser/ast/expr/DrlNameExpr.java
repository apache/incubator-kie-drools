/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2016 The JavaParser Team.
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * This file is part of JavaParser.
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * Modified by Red Hat, Inc.
 */
package org.drools.constraint.parser.ast.expr;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Generated;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.visitor.CloneVisitor;
import org.drools.constraint.parser.ast.visitor.DrlGenericVisitor;
import org.drools.constraint.parser.ast.visitor.DrlVoidVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import com.github.javaparser.metamodel.NameExprMetaModel;
import com.github.javaparser.resolution.Resolvable;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;

import static com.github.javaparser.utils.Utils.assertNotNull;

/**
 * Whenever a SimpleName is used in an expression, it is wrapped in DrlNameExpr.
 * <br/>In <code>int x = a + 3;</code> a is a SimpleName inside a DrlNameExpr.
 *
 * @author Julio Vilmar Gesser
 */
public final class DrlNameExpr extends Expression implements NodeWithSimpleName<DrlNameExpr>, Resolvable<ResolvedValueDeclaration> {

    private SimpleName name;

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
        super(tokenRange);
        setName(name);
        customInitialization();
        this.backReferencesCount = backReferencesCount;
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return ((DrlGenericVisitor<R, A>)v).visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        ((DrlVoidVisitor<A>)v).visit(this, arg);
    }

    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public SimpleName getName() {
        return name;
    }

    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public DrlNameExpr setName(final SimpleName name) {
        assertNotNull(name);
        if (name == this.name) {
            return (DrlNameExpr) this;
        }
        notifyPropertyChange(ObservableProperty.NAME, this.name, name);
        if (this.name != null) {
            this.name.setParentNode(null);
        }
        this.name = name;
        setAsParentNodeOf(name);
        return this;
    }

    @Override
    @Generated("com.github.javaparser.generator.core.node.RemoveMethodGenerator")
    public boolean remove(Node node) {
        if (node == null) {
            return false;
        }
        return super.remove(node);
    }

    @Override
    @Generated("com.github.javaparser.generator.core.node.CloneGenerator")
    public DrlNameExpr clone() {
        return (DrlNameExpr) accept(new CloneVisitor(), null);
    }

    @Override
    @Generated("com.github.javaparser.generator.core.node.GetMetaModelGenerator")
    public NameExprMetaModel getMetaModel() {
        return JavaParserMetaModel.nameExprMetaModel;
    }

    @Override
    @Generated("com.github.javaparser.generator.core.node.ReplaceMethodGenerator")
    public boolean replace(Node node, Node replacementNode) {
        if (node == null) {
            return false;
        }
        if (node == name) {
            setName((SimpleName) replacementNode);
            return true;
        }
        return super.replace(node, replacementNode);
    }

    @Override
    @Generated("com.github.javaparser.generator.core.node.TypeCastingGenerator")
    public boolean isNameExpr() {
        return true;
    }

    /**
     * Attempts to resolve the declaration corresponding to the accessed name. If successful, a
     * {@link ResolvedValueDeclaration} representing the declaration of the value accessed by this {@code DrlNameExpr} is
     * returned. Otherwise, an {@link UnsolvedSymbolException} is thrown.
     *
     * @return a {@link ResolvedValueDeclaration} representing the declaration of the accessed value.
     * @throws UnsolvedSymbolException if the declaration corresponding to the name expression could not be resolved.
     * @see FieldAccessExpr#resolve()
     * @see MethodCallExpr#resolve()
     * @see ObjectCreationExpr#resolve()
     * @see ExplicitConstructorInvocationStmt#resolve()
     */
    @Override
    public ResolvedValueDeclaration resolve() {
        return getSymbolResolver().resolveDeclaration(this, ResolvedValueDeclaration.class);
    }


    private int backReferencesCount = 0;

    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public int getBackReferencesCount() {
        return backReferencesCount;
    }

    public static DrlNameExpr fromNameExpr(NameExpr nameExpr) {
        return new DrlNameExpr(nameExpr.toString());
    }

    public NameExpr safeToNameExpr() {
        return new NameExpr(getName());
    }
}
