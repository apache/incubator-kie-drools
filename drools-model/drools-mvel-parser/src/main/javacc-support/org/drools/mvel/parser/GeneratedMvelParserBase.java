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
package org.drools.mvel.parser;

// MVEL: should use JP JavaToken here
import com.github.javaparser.JavaToken;

import com.github.javaparser.Problem;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.CommentsCollection;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.utils.Pair;

import java.util.*;

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

    abstract Token getToken(final int index);

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

    /**
     * Get the token that starts the NodeList l
     */
    JavaToken nodeListBegin(NodeList<?> l) {
        if (!storeTokens || l.isEmpty()) {
            return JavaToken.INVALID;
        }
        return l.get(0).getTokenRange().get().getBegin();
    }

    /* Sets the kind of the last matched token to newKind */
    void setTokenKind(int newKind) {
        // MVEL: setKind is private
        org.drools.mvel.parser.JavaToken token = (org.drools.mvel.parser.JavaToken) token();
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
    /* Called from within a catch block to skip forward to a known token,
        and report the occurred exception as a problem. */
    TokenRange recoverStatement(int recoveryTokenType, int lBraceType, int rBraceType, ParseException p) {
        JavaToken begin = null;
        if (p.currentToken != null) {
            begin = token();
        }
        int level = 0;
        Token t;
        do {
            Token nextToken = getToken(1);
            if (nextToken != null && nextToken.kind == rBraceType && level == 0) {
                TokenRange tokenRange = range(begin, token());
                problems.add(new Problem(makeMessageForParseException(p), tokenRange, p));
                return tokenRange;
            }
            t = getNextToken();
            if (t.kind == lBraceType) {
                level++;
            } else if (t.kind == rBraceType) {
                level--;
            }
        } while (!(t.kind == recoveryTokenType && level == 0) && t.kind != EOF);

        JavaToken end = token();

        TokenRange tokenRange = null;
        if (begin != null && end != null) {
            tokenRange = range(begin, end);
        }

        problems.add(new Problem(makeMessageForParseException(p), tokenRange, p));
        return tokenRange;
    }

    /**
     * Quickly create a new, empty, NodeList
     */
    <T extends Node> NodeList<T> emptyNodeList() {
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
     * Add obj to list only when list is not null
     */
    <T extends Node> NodeList<T> addWhenNotNull(NodeList<T> list, T obj) {
        if (obj == null) {
            return list;
        }
        return add(list, obj);
    }

    /**
     * Add obj to list at position pos
     */
    <T extends Node> NodeList<T> prepend(NodeList<T> list, T obj) {
        if (list == null) {
            list = new NodeList<>();
        }
        list.addFirst(obj);
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
        return wrapInArrayTypes(elementType, additionalBrackets, leftMostBrackets).clone();
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

    /**
     * Converts a NameExpr or a FieldAccessExpr scope to a Name.
     */
    Name scopeToName(Expression scope) {
        if (scope.isNameExpr()) {
            SimpleName simpleName = scope.asNameExpr().getName();
            return new Name(simpleName.getTokenRange().orElse(null), null, simpleName.getIdentifier());
        }
        if (scope.isFieldAccessExpr()) {
            FieldAccessExpr fieldAccessExpr = scope.asFieldAccessExpr();
            return new Name(fieldAccessExpr.getTokenRange().orElse(null), scopeToName(fieldAccessExpr.getScope()), fieldAccessExpr.getName().getIdentifier());

        }
        throw new IllegalStateException("Unexpected expression type: " + scope.getClass().getSimpleName());
    }

    String unquote(String s) {
        return s.substring(1, s.length() - 1);
    }

    String unTripleQuote(String s) {
        int start = 3;
        // Skip over the first end of line too:
        if (s.charAt(start) == '\r') {
            start++;
        }
        if (s.charAt(start) == '\n') {
            start++;
        }
        return s.substring(start, s.length() - 3);
    }

    void setYieldSupported() {
        getTokenSource().setYieldSupported();
    }
}