package org.drools.mvel.parser;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;

// MVEL use JP JavaToken
import com.github.javaparser.JavaToken;

import static com.github.javaparser.GeneratedJavaParserConstants.*;

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
        } else if (token.kind == SINGLE_LINE_COMMENT) {
            return new LineComment(tokenRange(token), commentText.substring(2));
        }
        throw new AssertionError("Unexpectedly got passed a non-comment token.");
    }
}