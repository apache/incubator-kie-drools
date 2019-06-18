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
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithScope;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithTypeArguments;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.FieldAccessExprMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import com.github.javaparser.metamodel.OptionalProperty;
import com.github.javaparser.resolution.Resolvable;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;

import static com.github.javaparser.utils.Utils.assertNotNull;

public class NullSafeFieldAccessExpr extends Expression implements NodeWithSimpleName<NullSafeFieldAccessExpr>,
                                                                   NodeWithTypeArguments<NullSafeFieldAccessExpr>,
                                                                   NodeWithScope<NullSafeFieldAccessExpr>,
                                                                   Resolvable<ResolvedValueDeclaration> {

    private Expression scope;

    @OptionalProperty
    private NodeList<Type> typeArguments;

    private SimpleName name;

    public NullSafeFieldAccessExpr() {
        this(null, new ThisExpr(), null, new SimpleName());
    }

    public NullSafeFieldAccessExpr(final Expression scope, final String name) {
        this(null, scope, null, new SimpleName(name));
    }

    @AllFieldsConstructor
    public NullSafeFieldAccessExpr(final Expression scope, final NodeList<Type> typeArguments, final SimpleName name) {
        this(null, scope, typeArguments, name);
    }

    /**
     * This constructor is used by the parser and is considered private.
     */
    @Generated("com.github.javaparser.generator.core.node.MainConstructorGenerator")
    public NullSafeFieldAccessExpr(TokenRange tokenRange, Expression scope, NodeList<Type> typeArguments, SimpleName name) {
        super(tokenRange);
        setScope(scope);
        setTypeArguments(typeArguments);
        setName(name);
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
    public SimpleName getName() {
        return name;
    }

    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public NullSafeFieldAccessExpr setName(final SimpleName name) {
        assertNotNull(name);
        if (name == this.name) {
            return (NullSafeFieldAccessExpr) this;
        }
        notifyPropertyChange(ObservableProperty.NAME, this.name, name);
        if (this.name != null) {
            this.name.setParentNode(null);
        }
        this.name = name;
        setAsParentNodeOf(name);
        return this;
    }

    /**
     * Use {@link #getName} instead.
     */
    @Deprecated
    public SimpleName getField() {
        return name;
    }

    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public Expression getScope() {
        return scope;
    }

    /**
     * Use {@link #setName} with new SimpleName(field) instead.
     */
    @Deprecated
    public NullSafeFieldAccessExpr setField(final String field) {
        setName(new SimpleName(field));
        return this;
    }

    /**
     * Use {@link #setName} instead.
     */
    @Deprecated
    public NullSafeFieldAccessExpr setFieldExpr(SimpleName inner) {
        return setName(inner);
    }

    /**
     * Sets the scope
     *
     * @param scope the scope, can not be null
     * @return this, the NullSafeFieldAccessExpr
     */
    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public NullSafeFieldAccessExpr setScope(final Expression scope) {
        assertNotNull(scope);
        if (scope == this.scope) {
            return (NullSafeFieldAccessExpr) this;
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
     * Sets the type arguments
     *
     * @param typeArguments the type arguments, can be null
     * @return this, the NullSafeFieldAccessExpr
     */
    @Generated("com.github.javaparser.generator.core.node.PropertyGenerator")
    public NullSafeFieldAccessExpr setTypeArguments(final NodeList<Type> typeArguments) {
        if (typeArguments == this.typeArguments) {
            return (NullSafeFieldAccessExpr) this;
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
    @Generated("com.github.javaparser.generator.core.node.CloneGenerator")
    public NullSafeFieldAccessExpr clone() {
        return (NullSafeFieldAccessExpr) accept(new CloneVisitor(), null);
    }

    @Override
    @Generated("com.github.javaparser.generator.core.node.GetMetaModelGenerator")
    public FieldAccessExprMetaModel getMetaModel() {
        return JavaParserMetaModel.fieldAccessExprMetaModel;
    }

    @Override
    @Generated("com.github.javaparser.generator.core.node.RemoveMethodGenerator")
    public boolean remove(Node node) {
        if (node == null) {
            return false;
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
        if (node == scope) {
            setScope((Expression) replacementNode);
            return true;
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

    @Override
    @Generated("com.github.javaparser.generator.core.node.TypeCastingGenerator")
    public boolean isFieldAccessExpr() {
        return true;
    }

    /**
     * Attempts to resolve the declaration corresponding to the accessed field. If successful, a
     * {@link ResolvedValueDeclaration} representing the declaration of the value accessed by this
     * {@code NullSafeFieldAccessExpr} is returned. Otherwise, an {@link UnsolvedSymbolException} is thrown.
     *
     * @return a {@link ResolvedValueDeclaration} representing the declaration of the accessed value.
     * @throws UnsolvedSymbolException if the declaration corresponding to the field access expression could not be
     *                                 resolved.
     * @see NameExpr#resolve()
     * @see MethodCallExpr#resolve()
     * @see ObjectCreationExpr#resolve()
     * @see ExplicitConstructorInvocationStmt#resolve()
     */
    @Override
    public ResolvedValueDeclaration resolve() {
        return getSymbolResolver().resolveDeclaration(this, ResolvedValueDeclaration.class);
    }
    

    /**
     * Indicate if this NullSafeFieldAccessExpr is an element directly contained in a larger NullSafeFieldAccessExpr.
     */
    public boolean isInternal() {
        Optional<Node> parentNode = this.getParentNode();
        return parentNode.isPresent() && parentNode.get() instanceof NullSafeFieldAccessExpr;
    }

    /**
     * Indicate if this NullSafeFieldAccessExpr is top level, i.e., it is not directly contained in a larger NullSafeFieldAccessExpr.
     */
    public boolean isTopLevel() {
        return !isInternal();
    }
}
