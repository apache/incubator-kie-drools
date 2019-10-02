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

package org.drools.mvel.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Problem;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.CommentsCollection;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.utils.Pair;

import static com.github.javaparser.GeneratedJavaParserConstants.EOF;
import static com.github.javaparser.ast.type.ArrayType.unwrapArrayTypes;
import static com.github.javaparser.ast.type.ArrayType.wrapInArrayTypes;
import static com.github.javaparser.utils.Utils.assertNotNull;

/**
 * Base class for {@link GeneratedJavaParser}
 */
abstract class GeneratedMvelParserBase {
    //// Interface with the generated code
    abstract GeneratedMvelParserTokenManager getTokenSource();

    abstract void ReInit(Provider provider);

    /* Returns the JavaParser specific token type of the last matched token */
    abstract JavaToken token();

    abstract Token getNextToken();

    ////

    /* The problems encountered while parsing */
    List<Problem> problems = new ArrayList<>();
    /* Configuration flag whether we store tokens and tokenranges */
    boolean storeTokens;

    /* Resets the parser for reuse, gaining a little performance */
    void reset(Provider provider) {
        ReInit(provider);
        problems = new ArrayList<>();
        getTokenSource().reset();
    }

    /**
     * Return the list of JavaParser specific tokens that have been encountered while parsing code using this parser.
     *
     * @return a list of tokens
     */
    public List<JavaToken> getTokens() {
        return getTokenSource().getTokens();
    }

    /* The collection of comments encountered */
    CommentsCollection getCommentsCollection() {
        return getTokenSource().getCommentsCollection();
    }

    /* Reports a problem to the user */
    void addProblem(String message) {
        // TODO tokenRange only takes the final token. Need all the tokens.
        problems.add(new Problem(message, tokenRange(), null));
    }

    /* Returns a tokenRange that spans the last matched token */
    TokenRange tokenRange() {
        if (storeTokens) {
            return new TokenRange(token(), token());
        }
        return null;
    }

    /**
     * Return a TokenRange spanning from begin to end
     */
    TokenRange range(JavaToken begin, JavaToken end) {
        if (storeTokens) {
            return new TokenRange(begin, end);
        }
        return null;
    }

    /**
     * Return a TokenRange spanning from begin to end
     */
    TokenRange range(Node begin, JavaToken end) {
        if (storeTokens) {
            return new TokenRange(begin.getTokenRange().get().getBegin(), end);
        }
        return null;
    }

    /**
     * Return a TokenRange spanning from begin to end
     */
    TokenRange range(JavaToken begin, Node end) {
        if (storeTokens) {
            return new TokenRange(begin, end.getTokenRange().get().getEnd());
        }
        return null;
    }

    /**
     * Return a TokenRange spanning from begin to end
     */
    TokenRange range(Node begin, Node end) {
        if (storeTokens) {
            return new TokenRange(begin.getTokenRange().get().getBegin(), end.getTokenRange().get().getEnd());
        }
        return null;
    }

    /**
     * @return secondChoice if firstChoice is JavaToken.UNKNOWN, otherwise firstChoice
     */
    JavaToken orIfInvalid(JavaToken firstChoice, JavaToken secondChoice) {
        if (storeTokens) {
            assertNotNull(firstChoice);
            assertNotNull(secondChoice);
            if (firstChoice.valid() || secondChoice.invalid()) {
                return firstChoice;
            }
            return secondChoice;
        }
        return null;
    }

    /**
     * @return the begin-token secondChoice if firstChoice is JavaToken.UNKNOWN, otherwise firstChoice
     */
    JavaToken orIfInvalid(JavaToken firstChoice, Node secondChoice) {
        if (storeTokens) {
            return orIfInvalid(firstChoice, secondChoice.getTokenRange().get().getBegin());
        }
        return null;
    }

    /* Sets the kind of the last matched token to newKind */
    void setTokenKind(int newKind) {
        org.drools.mvel.parser.JavaToken token = (org.drools.mvel.parser.JavaToken)token();
        token.setKind(newKind);
    }

    /* Makes the parser keep a list of tokens */
    void setStoreTokens(boolean storeTokens) {
        this.storeTokens = storeTokens;
        getTokenSource().setStoreTokens(storeTokens);
    }

    /* Called from within a catch block to skip forward to a known token,
        and report the occurred exception as a problem. */
    TokenRange recover(int recoveryTokenType, ParseException p) {
        JavaToken begin = null;
        if (p.currentToken != null) {
            begin = token();
        }
        Token t;
        do {
            t = getNextToken();
        } while (t.kind != recoveryTokenType && t.kind != EOF);

        JavaToken end = token();

        TokenRange tokenRange = null;
        if (begin != null && end != null) {
            tokenRange = range(begin, end);
        }

        problems.add(new Problem(makeMessageForParseException(p), tokenRange, p));
        return tokenRange;
    }

    /**
     * Quickly create a new NodeList
     */
    <T extends Node> NodeList<T> emptyList() {
        return new NodeList<>();
    }

    /**
     * Add obj to list and return it. Create a new list if list is null
     */
    <T extends Node> NodeList<T> add(NodeList<T> list, T obj) {
        if (list == null) {
            list = new NodeList<>();
        }
        list.add(obj);
        return list;
    }

    /**
     * Add obj to list
     */
    <T> List<T> add(List<T> list, T obj) {
        if (list == null) {
            list = new LinkedList<>();
        }
        list.add(obj);
        return list;
    }

    /**
     * Propagate expansion of the range on the right to the parent. This is necessary when the right border of the child
     * is determining the right border of the parent (i.e., the child is the last element of the parent). In this case
     * when we "enlarge" the child we should enlarge also the parent.
     */
    private void propagateRangeGrowthOnRight(Node node, Node endNode) {
        if (storeTokens) {
            node.getParentNode().ifPresent(nodeParent -> {
                boolean isChildOnTheRightBorderOfParent = node.getTokenRange().get().getEnd().equals(nodeParent.getTokenRange().get().getEnd());
                if (isChildOnTheRightBorderOfParent) {
                    propagateRangeGrowthOnRight(nodeParent, endNode);
                }
            });
            node.setTokenRange(range(node, endNode));
        }
    }

    /**
     * Workaround for rather complex ambiguity that lambda's create
     */
    Expression generateLambda(Expression ret, Statement lambdaBody) {
        if (ret instanceof EnclosedExpr) {
            Expression inner = ((EnclosedExpr) ret).getInner();
            SimpleName id = ((NameExpr) inner).getName();
            NodeList<Parameter> params = add(new NodeList<>(), new Parameter(ret.getTokenRange().orElse(null), new NodeList<>(), new NodeList<>(), new UnknownType(), false, new NodeList<>(), id));
            ret = new LambdaExpr(range(ret, lambdaBody), params, lambdaBody, true);
        } else if (ret instanceof NameExpr) {
            SimpleName id = ((NameExpr) ret).getName();
            NodeList<Parameter> params = add(new NodeList<>(), new Parameter(ret.getTokenRange().orElse(null), new NodeList<>(), new NodeList<>(), new UnknownType(), false, new NodeList<>(), id));
            ret = new LambdaExpr(range(ret, lambdaBody), params, lambdaBody, false);
        } else if (ret instanceof LambdaExpr) {
            ((LambdaExpr) ret).setBody(lambdaBody);
            propagateRangeGrowthOnRight(ret, lambdaBody);
        } else if (ret instanceof CastExpr) {
            CastExpr castExpr = (CastExpr) ret;
            Expression inner = generateLambda(castExpr.getExpression(), lambdaBody);
            castExpr.setExpression(inner);
        } else {
            addProblem("Failed to parse lambda expression! Please create an issue at https://github.com/javaparser/javaparser/issues");
        }
        return ret;
    }


    /**
     * Throws together an ArrayCreationExpr from a lot of pieces
     */
    ArrayCreationExpr juggleArrayCreation(TokenRange range, List<TokenRange> levelRanges, Type type, NodeList<Expression> dimensions, List<NodeList<AnnotationExpr>> arrayAnnotations, ArrayInitializerExpr arrayInitializerExpr) {
        NodeList<ArrayCreationLevel> levels = new NodeList<>();

        for (int i = 0; i < arrayAnnotations.size(); i++) {
            levels.add(new ArrayCreationLevel(levelRanges.get(i), dimensions.get(i), arrayAnnotations.get(i)));
        }
        return new ArrayCreationExpr(range, type, levels, arrayInitializerExpr);
    }

    /**
     * Throws together a Type, taking care of all the array brackets
     */
    Type juggleArrayType(Type partialType, List<ArrayType.ArrayBracketPair> additionalBrackets) {
        Pair<Type, List<ArrayType.ArrayBracketPair>> partialParts = unwrapArrayTypes(partialType);
        Type elementType = partialParts.a;
        List<ArrayType.ArrayBracketPair> leftMostBrackets = partialParts.b;
        return wrapInArrayTypes(elementType, leftMostBrackets, additionalBrackets).clone();
    }


    /**
     * This is the code from ParseException.initialise, modified to be more horizontal.
     */
    private String makeMessageForParseException(ParseException exception) {
        final StringBuilder sb = new StringBuilder("Parse error. Found ");
        final StringBuilder expected = new StringBuilder();

        int maxExpectedTokenSequenceLength = 0;
        TreeSet<String> sortedOptions = new TreeSet<>();
        for (int i = 0; i < exception.expectedTokenSequences.length; i++) {
            if (maxExpectedTokenSequenceLength < exception.expectedTokenSequences[i].length) {
                maxExpectedTokenSequenceLength = exception.expectedTokenSequences[i].length;
            }
            for (int j = 0; j < exception.expectedTokenSequences[i].length; j++) {
                sortedOptions.add(exception.tokenImage[exception.expectedTokenSequences[i][j]]);
            }
        }

        for (String option : sortedOptions) {
            expected.append(" ").append(option);
        }

        sb.append("");

        Token token = exception.currentToken.next;
        for (int i = 0; i < maxExpectedTokenSequenceLength; i++) {
            String tokenText = token.image;
            String escapedTokenText = ParseException.add_escapes(tokenText);
            if (i != 0) {
                sb.append(" ");
            }
            if (token.kind == 0) {
                sb.append(exception.tokenImage[0]);
                break;
            }
            escapedTokenText = "\"" + escapedTokenText + "\"";
            String image = exception.tokenImage[token.kind];
            if (image.equals(escapedTokenText)) {
                sb.append(image);
            } else {
                sb.append(" ")
                        .append(escapedTokenText)
                        .append(" ")
                        .append(image);
            }
            token = token.next;
        }

        if (exception.expectedTokenSequences.length != 0) {
            int numExpectedTokens = exception.expectedTokenSequences.length;
            sb.append(", expected")
                    .append(numExpectedTokens == 1 ? "" : " one of ")
                    .append(expected.toString());
        }
        return sb.toString();
    }
}
