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

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.AllFieldsConstructor;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.observer.ObservableProperty;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.metamodel.BinaryExprMetaModel;
import com.github.javaparser.metamodel.JavaParserMetaModel;
import com.github.javaparser.printer.Printable;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;

import static com.github.javaparser.utils.Utils.assertNotNull;

public final class HalfBinaryExpr extends Expression {

    public enum Operator implements Printable {

        EQUALS("=="), NOT_EQUALS("!="), LESS("<"), GREATER(">"), LESS_EQUALS("<="), GREATER_EQUALS(">=");

        private final String codeRepresentation;

        Operator(String codeRepresentation) {
            this.codeRepresentation = codeRepresentation;
        }

        public String asString() {
            return codeRepresentation;
        }

        public BinaryExpr.Operator toBinaryExprOperator() {
            switch (this) {
                case EQUALS: return BinaryExpr.Operator.EQUALS;
                case NOT_EQUALS: return BinaryExpr.Operator.NOT_EQUALS;
                case LESS: return BinaryExpr.Operator.LESS;
                case GREATER: return BinaryExpr.Operator.GREATER;
                case LESS_EQUALS: return BinaryExpr.Operator.LESS_EQUALS;
                case GREATER_EQUALS: return BinaryExpr.Operator.GREATER_EQUALS;
            }
            throw new UnsupportedOperationException("Unknown operator " + this);
        }
    }

    private Expression right;

    private Operator operator;

    public HalfBinaryExpr() {
        this(null, new BooleanLiteralExpr(), Operator.EQUALS);
    }

    @AllFieldsConstructor
    public HalfBinaryExpr(Expression right, Operator operator) {
        this(null, right, operator);
    }

    /**This constructor is used by the parser and is considered private.*/
    public HalfBinaryExpr(TokenRange tokenRange, Expression right, Operator operator) {
        super(tokenRange);
        setRight(right);
        setOperator(operator);
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

    public Operator getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    public HalfBinaryExpr setOperator(final Operator operator) {
        assertNotNull(operator);
        if (operator == this.operator) {
            return (HalfBinaryExpr) this;
        }
        notifyPropertyChange(ObservableProperty.OPERATOR, this.operator, operator);
        this.operator = operator;
        return this;
    }

    public HalfBinaryExpr setRight(final Expression right) {
        assertNotNull(right);
        if (right == this.right) {
            return (HalfBinaryExpr) this;
        }
        notifyPropertyChange(ObservableProperty.RIGHT, this.right, right);
        if (this.right != null)
            this.right.setParentNode(null);
        this.right = right;
        setAsParentNodeOf(right);
        return this;
    }

    @Override
    public boolean remove(Node node) {
        if (node == null)
            return false;
        return super.remove(node);
    }

    @Override
    public HalfBinaryExpr clone() {
        return (HalfBinaryExpr) accept(new CloneVisitor(), null);
    }

    @Override
    public BinaryExprMetaModel getMetaModel() {
        return JavaParserMetaModel.binaryExprMetaModel;
    }

    @Override
    public boolean replace(Node node, Node replacementNode) {
        if (node == null)
            return false;
        if (node == right) {
            setRight((Expression) replacementNode);
            return true;
        }
        return super.replace(node, replacementNode);
    }
}
