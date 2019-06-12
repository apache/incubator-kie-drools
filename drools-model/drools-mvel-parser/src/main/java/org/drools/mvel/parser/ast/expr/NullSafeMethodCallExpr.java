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

package org.drools.mvel.parser.ast.expr;

import java.util.Optional;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Generated;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithTypeArguments;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.CloneVisitor;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.OptionalProperty;
import com.github.javaparser.resolution.Resolvable;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

import static com.github.javaparser.utils.Utils.assertNotNull;

public class NullSafeMethodCallExpr extends Expression implements NodeWithTypeArguments<NullSafeMethodCallExpr>,
                                                                  NodeWithArguments<NullSafeMethodCallExpr>,
                                                                  NodeWithSimpleName<NullSafeMethodCallExpr>,
                                                                  NodeWithOptionalScope<NullSafeMethodCallExpr>,
                                                                  Resolvable<ResolvedMethodDeclaration> {

    @OptionalProperty
    private Expression scope;

    @OptionalProperty
    private NodeList<Type> typeArguments;

    private SimpleName name;

    private NodeList<Expression> arguments;

    public NullSafeMethodCallExpr() {
        this(null, null, null, new SimpleName(), new NodeList<>());
    }

    public NullSafeMethodCallExpr(String name, Expression... arguments) {
        this(null, null, null, new SimpleName(name), new NodeList<>(arguments));
    }

    public NullSafeMethodCallExpr(final Expression scope, final String name) {
        this(null, scope, null, new SimpleName(name), new NodeList<>());
    }

    public NullSafeMethodCallExpr(final Expression scope, final SimpleName name) {
        this(null, scope, null, name, new NodeList<>());
    }

    public NullSafeMethodCallExpr(final Expression scope, final String name, final NodeList<Expression> arguments) {
        this(null, scope, null, new SimpleName(name), arguments);
    }

    public NullSafeMethodCallExpr(final Expression scope, final NodeList<Type> typeArguments, final String name, final NodeList<Expression> arguments) {
        this(null, scope, typeArguments, new SimpleName(name), arguments);
    }

    public NullSafeMethodCallExpr(final Expression scope, final SimpleName name, final NodeList<Expression> arguments) {
        this(null, scope, null, name, arguments);
    }

    @AllFieldsConstructor
    public NullSafeMethodCallExpr(final Expression scope, final NodeList<Type> typeArguments, final SimpleName name, final NodeList<Expression> arguments) {
        this(null, scope, typeArguments, name, arguments);
    }

    /**
     * This constructor is used by the parser and is considered private.
     */
    @Generated("com.github.javaparser.generator.core.node.MainConstructorGenerator")
    public NullSafeMethodCallExpr(TokenRange tokenRange, Expression scope, NodeList<Type> typeArguments, SimpleName name, NodeList<Expression> arguments) {
        super(tokenRange);
        setScope(scope);
        setTypeArguments(typeArguments);
        setName(name);
        setArguments(arguments);
        customInitialization();
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
    public NodeList<Expression> getArguments() {
        return arguments;
    }

    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public SimpleName getName() {
        return name;
    }

    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public Optional<Expression> getScope() {
        return Optional.ofNullable(scope);
    }

    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public NullSafeMethodCallExpr setArguments(final NodeList<Expression> arguments) {
        assertNotNull(arguments);
        if (arguments == this.arguments) {
            return (NullSafeMethodCallExpr) this;
        }
        notifyPropertyChange(ObservableProperty.ARGUMENTS, this.arguments, arguments);
        if (this.arguments != null) {
            this.arguments.setParentNode(null);
        }
        this.arguments = arguments;
        setAsParentNodeOf(arguments);
        return this;
    }

    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public NullSafeMethodCallExpr setName(final SimpleName name) {
        assertNotNull(name);
        if (name == this.name) {
            return (NullSafeMethodCallExpr) this;
        }
        notifyPropertyChange(ObservableProperty.NAME, this.name, name);
        if (this.name != null) {
            this.name.setParentNode(null);
        }
        this.name = name;
        setAsParentNodeOf(name);
        return this;
    }

    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public NullSafeMethodCallExpr setScope(final Expression scope) {
        if (scope == this.scope) {
            return (NullSafeMethodCallExpr) this;
        }
        notifyPropertyChange(ObservableProperty.SCOPE, this.scope, scope);
        if (this.scope != null) {
            this.scope.setParentNode(null);
        }
        this.scope = scope;
        setAsParentNodeOf(scope);
        return this;
    }

    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public Optional<NodeList<Type>> getTypeArguments() {
        return Optional.ofNullable(typeArguments);
    }

    /**
     * Sets the typeArguments
     *
     * @param typeArguments the typeArguments, can be null
     * @return this, the NullSafeMethodCallExpr
     */
    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public NullSafeMethodCallExpr setTypeArguments(final NodeList<Type> typeArguments) {
        if (typeArguments == this.typeArguments) {
            return (NullSafeMethodCallExpr) this;
        }
        notifyPropertyChange(ObservableProperty.TYPE_ARGUMENTS, this.typeArguments, typeArguments);
        if (this.typeArguments != null) {
            this.typeArguments.setParentNode(null);
        }
        this.typeArguments = typeArguments;
        setAsParentNodeOf(typeArguments);
        return this;
    }

    @Override
    @Generated("com.github.javaparser.generator.core.node.RemoveMethodGenerator")
    public boolean remove(Node node) {
        if (node == null) {
            return false;
        }
        for (int i = 0; i < arguments.size(); i++) {
            if (arguments.get(i) == node) {
                arguments.remove(i);
                return true;
            }
        }
        if (scope != null) {
            if (node == scope) {
                removeScope();
                return true;
            }
        }
        if (typeArguments != null) {
            for (int i = 0; i < typeArguments.size(); i++) {
                if (typeArguments.get(i) == node) {
                    typeArguments.remove(i);
                    return true;
                }
            }
        }
        return super.remove(node);
    }

    @Generated("com.github.javaparser.generator.core.node.RemoveMethodGenerator")
    public NullSafeMethodCallExpr removeScope() {
        return setScope((Expression) null);
    }

    @Override
    @Generated("com.github.javaparser.generator.core.node.CloneGenerator")
    public NullSafeMethodCallExpr clone() {
        return (NullSafeMethodCallExpr) accept(new CloneVisitor(), null);
    }

    @Override
    @Generated("com.github.javaparser.generator.core.node.ReplaceMethodGenerator")
    public boolean replace(Node node, Node replacementNode) {
        if (node == null) {
            return false;
        }
        for (int i = 0; i < arguments.size(); i++) {
            if (arguments.get(i) == node) {
                arguments.set(i, (Expression) replacementNode);
                return true;
            }
        }
        if (node == name) {
            setName((SimpleName) replacementNode);
            return true;
        }
        if (scope != null) {
            if (node == scope) {
                setScope((Expression) replacementNode);
                return true;
            }
        }
        if (typeArguments != null) {
            for (int i = 0; i < typeArguments.size(); i++) {
                if (typeArguments.get(i) == node) {
                    typeArguments.set(i, (Type) replacementNode);
                    return true;
                }
            }
        }
        return super.replace(node, replacementNode);
    }

    /**
     * Attempts to resolve the declaration corresponding to the invoked method. If successful, a
     * {@link ResolvedMethodDeclaration} representing the declaration of the constructor invoked by this
     * {@code NullSafeMethodCallExpr} is returned. Otherwise, an {@link UnsolvedSymbolException} is thrown.
     *
     * @return a {@link ResolvedMethodDeclaration} representing the declaration of the invoked method.
     * @throws UnsolvedSymbolException if the declaration corresponding to the method call expression could not be
     *                                 resolved.
     * @see NameExpr#resolve()
     * @see FieldAccessExpr#resolve()
     * @see ObjectCreationExpr#resolve()
     * @see ExplicitConstructorInvocationStmt#resolve()
     */
    @Override
    public ResolvedMethodDeclaration resolve() {
        return getSymbolResolver().resolveDeclaration(this, ResolvedMethodDeclaration.class);
    }

    /**
     * @deprecated Call {@link #resolve()} instead.
     */
    @Deprecated
    public ResolvedMethodDeclaration resolveInvokedMethod() {
        return resolve();
    }

}
