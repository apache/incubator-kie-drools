package org.drools.mvel.parser;

import static com.github.javaparser.GeneratedJavaParserConstants.GT;
import com.github.javaparser.JavaToken;


/**
 * Base class for the generated {@link Token}
 */
abstract class TokenBase extends com.github.javaparser.Token {
    /**
     * For tracking the >> >>> ambiguity.
     */
    int realKind = GT;

    /**
     * This is the link to the token that JavaParser presents to the user
     */
    JavaToken javaToken = null;
}