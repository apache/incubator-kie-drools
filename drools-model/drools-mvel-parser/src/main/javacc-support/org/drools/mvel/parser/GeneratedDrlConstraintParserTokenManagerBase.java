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

import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.TokenRange;
import com.github.javaparser.Problem;
import org.drools.mvel.parser.Token;
import com.github.javaparser.JavaToken;
import com.github.javaparser.Range;



import static com.github.javaparser.GeneratedJavaParserConstants.*;
import static com.github.javaparser.Position.pos;

/**
 * Base class for {@link com.github.javaparser.GeneratedJavaParserTokenManager}
 */
abstract class GeneratedMvelParserTokenManagerBase {
    /**
     * Create a TokenRange that spans exactly one token
     */
    private static TokenRange tokenRange(Token token) {
        JavaToken javaToken = token.javaToken;
        return new TokenRange(javaToken, javaToken);
    }

    /**
     * Since comments are completely captured in a single token, including their delimiters, deconstruct them here so we
     * can turn them into nodes later on.
     */
    static Comment createCommentFromToken(Token token) {
        String commentText = token.image;
        if (token.kind == JAVADOC_COMMENT) {
            return new JavadocComment(tokenRange(token), commentText.substring(3, commentText.length() - 2));
        } else if (token.kind == MULTI_LINE_COMMENT) {
            return new BlockComment(tokenRange(token), commentText.substring(2, commentText.length() - 2));
        } else if (token.kind == SINGLE_LINE_COMMENT || token.kind == 4 || token.kind == 3 || token.kind == 7) {
            // line comments have their end of line character(s) included, and we don't want that.
            Range range = new Range(pos(token.beginLine, token.beginColumn), pos(token.endLine, token.endColumn));
            while (commentText.endsWith("\r") || commentText.endsWith("\n")) {
                commentText = commentText.substring(0, commentText.length() - 1);
            }
            range = range.withEnd(pos(range.begin.line, range.begin.column + commentText.length()));
            LineComment comment = new LineComment(tokenRange(token), commentText.substring(2));
            comment.setRange(range);
            return comment;
        }
        throw new AssertionError("Unexpectedly got passed a non-comment token.");
    }
}
