/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.compiler;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.drools.compiler.lang.DRL5Expressions;
import org.drools.compiler.lang.DRL5Lexer;
import org.drools.compiler.lang.DRL5Parser;
import org.drools.compiler.lang.DRL6Expressions;
import org.drools.compiler.lang.DRL6Lexer;
import org.drools.compiler.lang.DRL6Parser;
import org.drools.compiler.lang.DRL6StrictParser;
import org.drools.compiler.lang.DRLExpressions;
import org.drools.compiler.lang.DRLLexer;
import org.drools.compiler.lang.DRLParser;
import org.drools.compiler.lang.ParserHelper;
import org.kie.internal.builder.conf.LanguageLevelOption;

import java.io.InputStream;
import java.io.Reader;

public class DRLFactory {

    private DRLFactory() { }

    public static DRLLexer buildLexer(String text, LanguageLevelOption languageLevel) {
        return getDRLLexer(new ANTLRStringStream(text), languageLevel);
    }

    public static DRLLexer buildLexer( Reader reader, LanguageLevelOption languageLevel ) {
        try {
            return getDRLLexer(new ANTLRReaderStream(reader), languageLevel);
        } catch ( final Exception e ) {
            throw new RuntimeException( "Unable to parser Reader", e );
        }
    }

    public static DRLLexer buildLexer( final InputStream is, final String encoding, LanguageLevelOption languageLevel ) {
        try {
            return getDRLLexer(encoding != null ? new ANTLRInputStream(is, encoding) : new ANTLRInputStream(is), languageLevel);
        } catch ( final Exception e ) {
            throw new RuntimeException( "Unable to parser Reader", e );
        }
    }

    public static DRLParser buildParser(DRLLexer lexer, LanguageLevelOption languageLevel) {
        CommonTokenStream stream = new CommonTokenStream( lexer );
        return getDRLParser(stream, languageLevel);
    }

    public static DRLParser buildParser(CharStream input, LanguageLevelOption languageLevel) {
        return buildParser(getDRLLexer( input, languageLevel ), languageLevel);
    }

    public static DRLParser buildParser(String text, LanguageLevelOption languageLevel) {
        return buildParser(new ANTLRStringStream(text), languageLevel);
    }

    public static DRLParser buildParser( Reader reader, LanguageLevelOption languageLevel ) {
        try {
            return buildParser(new ANTLRReaderStream(reader), languageLevel);
        } catch ( final Exception e ) {
            throw new RuntimeException( "Unable to parser Reader", e );
        }
    }

    public static DRLParser buildParser( final InputStream is, final String encoding, LanguageLevelOption languageLevel ) {
        try {
            return buildParser(encoding != null ? new ANTLRInputStream(is, encoding) : new ANTLRInputStream(is), languageLevel);
        } catch ( final Exception e ) {
            throw new RuntimeException( "Unable to parser Reader", e );
        }
    }

    public static DRLParser getDRLParser(CommonTokenStream stream, LanguageLevelOption languageLevel) {
        switch (languageLevel) {
            case DRL5:
                return new DRL5Parser(stream);
            case DRL6:
                return new DRL6Parser(stream);
            case DRL6_STRICT:
                return new DRL6StrictParser(stream);
        }
        throw new RuntimeException("Unknown language level");
    }

    public static DRLLexer getDRLLexer(CharStream input, LanguageLevelOption languageLevel) {
        switch (languageLevel) {
            case DRL5:
                return new DRL5Lexer(input);
            case DRL6:
            case DRL6_STRICT:
                return new DRL6Lexer(input);
        }
        throw new RuntimeException("Unknown language level");
    }

    public static DRLExpressions getDRLExpressions(TokenStream input, RecognizerSharedState state, ParserHelper helper, LanguageLevelOption languageLevel ) {
        switch (languageLevel) {
            case DRL5:
                return new DRL5Expressions(input, state, helper);
            case DRL6:
            case DRL6_STRICT:
                return new DRL6Expressions(input, state, helper);
        }
        throw new RuntimeException("Unknown language level");
    }

    public static int lexerId(LanguageLevelOption languageLevel) {
        switch (languageLevel) {
            case DRL5:
                return DRL5Lexer.ID;
            case DRL6:
            case DRL6_STRICT:
                return DRL6Lexer.ID;
        }
        throw new RuntimeException("Unknown language level");
    }

    /**
     * Helper method that creates a user friendly token definition
     *
     * @param tokenType
     *            token type
     * @param defaultValue
     *            default value for identifier token, may be null
     * @return user friendly token definition
     */
    public static String getBetterToken( int tokenType, String defaultValue, LanguageLevelOption languageLevel ) {
        switch (languageLevel) {
            case DRL5:
                return getBetterTokenForDRL5(tokenType, defaultValue);
            case DRL6:
            case DRL6_STRICT:
                return getBetterTokenForDRL6(tokenType, defaultValue);
        }
        throw new RuntimeException("Unknown language level");
    }

    private static String getBetterTokenForDRL5( int tokenType, String defaultValue ) {
        switch ( tokenType ) {
            case DRL5Lexer.DECIMAL :
                return defaultValue == null ? "int" : defaultValue;
            case DRL5Lexer.FLOAT :
                return defaultValue == null ? "float" : defaultValue;
            case DRL5Lexer.STRING :
                return defaultValue == null ? "string" : defaultValue;
            case DRL5Lexer.BOOL :
                return defaultValue == null ? "boolean" : defaultValue;
            case DRL5Lexer.NULL :
                return "null";
            case DRL5Lexer.SEMICOLON :
                return ";";
            case DRL5Lexer.COLON :
                return ":";
            case DRL5Lexer.EQUALS :
                return "==";
            case DRL5Lexer.NOT_EQUALS :
                return "!=";
            case DRL5Lexer.GREATER :
                return ">";
            case DRL5Lexer.GREATER_EQUALS :
                return ">=";
            case DRL5Lexer.LESS :
                return "<";
            case DRL5Lexer.LESS_EQUALS :
                return "<=";
            case DRL5Lexer.ARROW :
                return "->";
            case DRL5Lexer.ID :
                return defaultValue == null ? "identifier" : defaultValue;
            case DRL5Lexer.LEFT_PAREN :
                return "(";
            case DRL5Lexer.RIGHT_PAREN :
                return ")";
            case DRL5Lexer.LEFT_SQUARE :
                return "[";
            case DRL5Lexer.RIGHT_SQUARE :
                return "]";
            case DRL5Lexer.LEFT_CURLY :
                return "{";
            case DRL5Lexer.RIGHT_CURLY :
                return "}";
            case DRL5Lexer.COMMA :
                return ",";
            case DRL5Lexer.DOT :
                return ".";
            case DRL5Lexer.DOUBLE_AMPER :
                return "&&";
            case DRL5Lexer.DOUBLE_PIPE :
                return "||";
            case DRL5Lexer.MISC :
                return defaultValue == null ? "misc" : defaultValue;
            case DRL5Lexer.EOF :
                return "<eof>";
            default :
                return defaultValue;
        }
    }

    private static String getBetterTokenForDRL6( int tokenType, String defaultValue ) {
        switch ( tokenType ) {
            case DRL6Lexer.DECIMAL :
                return defaultValue == null ? "int" : defaultValue;
            case DRL6Lexer.FLOAT :
                return defaultValue == null ? "float" : defaultValue;
            case DRL6Lexer.STRING :
                return defaultValue == null ? "string" : defaultValue;
            case DRL6Lexer.BOOL :
                return defaultValue == null ? "boolean" : defaultValue;
            case DRL6Lexer.NULL :
                return "null";
            case DRL6Lexer.SEMICOLON :
                return ";";
            case DRL6Lexer.COLON :
                return ":";
            case DRL6Lexer.EQUALS :
                return "==";
            case DRL6Lexer.NOT_EQUALS :
                return "!=";
            case DRL6Lexer.GREATER :
                return ">";
            case DRL6Lexer.GREATER_EQUALS :
                return ">=";
            case DRL6Lexer.LESS :
                return "<";
            case DRL6Lexer.LESS_EQUALS :
                return "<=";
            case DRL6Lexer.ARROW :
                return "->";
            case DRL6Lexer.ID :
                return defaultValue == null ? "identifier" : defaultValue;
            case DRL6Lexer.LEFT_PAREN :
                return "(";
            case DRL6Lexer.RIGHT_PAREN :
                return ")";
            case DRL6Lexer.LEFT_SQUARE :
                return "[";
            case DRL6Lexer.RIGHT_SQUARE :
                return "]";
            case DRL6Lexer.LEFT_CURLY :
                return "{";
            case DRL6Lexer.RIGHT_CURLY :
                return "}";
            case DRL6Lexer.COMMA :
                return ",";
            case DRL6Lexer.DOT :
                return ".";
            case DRL6Lexer.DOUBLE_AMPER :
                return "&&";
            case DRL6Lexer.DOUBLE_PIPE :
                return "||";
            case DRL6Lexer.MISC :
                return defaultValue == null ? "misc" : defaultValue;
            case DRL6Lexer.EOF :
                return "<eof>";
            default :
                return defaultValue;
        }
    }
}
