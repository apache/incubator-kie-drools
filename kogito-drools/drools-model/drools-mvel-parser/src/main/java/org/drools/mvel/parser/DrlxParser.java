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

import java.util.Collection;
import java.util.function.Function;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.Node;
import org.drools.mvel.parser.ast.expr.DrlxExpression;
import org.drools.mvel.parser.ast.expr.TemporalLiteralExpr;

import static org.drools.mvel.parser.Providers.provider;

public class DrlxParser {

    private DrlxParser() {
        // Creating instances of util classes is forbidden.
    }

    private static final Function<Collection<String>, ParseStart<DrlxExpression>> DRLX_EXPRESSION = (operators) -> parser -> {
        parser.setPointFreeOperators(operators);
        return parser.DrlxExpression();
    };

    private static final ParseStart<TemporalLiteralExpr> DRLX_TEMPORAL_LITERAL = GeneratedMvelParser::TemporalLiteral;

    public static final ParseStart<DrlxExpression> buildDrlxParserWithArguments(Collection<String> operators) {
        return DRLX_EXPRESSION.apply(operators);
    }

    public static <T extends DrlxExpression> T parseExpression(ParseStart<DrlxExpression> parser, final String expression) {
        return (T) simplifiedParse(parser, provider(expression));
    }

    private static <T extends Node> T simplifiedParse(ParseStart<T> context, Provider provider) {
        ParseResult<T> result = new MvelParser().parse(context, provider);
        if (result.isSuccessful()) {
            return result.getResult().orElseThrow(() -> new IllegalStateException("ParseResult doesn't contain any result although marked as successful!"));
        }
        throw new ParseProblemException(result.getProblems());
    }

    public static <T extends TemporalLiteralExpr> T parseTemporalLiteral(final String expression) {
        return (T) simplifiedParse(DRLX_TEMPORAL_LITERAL, provider(expression));
    }
}
