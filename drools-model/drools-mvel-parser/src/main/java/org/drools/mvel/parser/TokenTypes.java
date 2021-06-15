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

import com.github.javaparser.JavaToken;

import static com.github.javaparser.utils.Utils.EOL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.ABSTRACT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.ANDASSIGN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.ARROW;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.ASSERT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.ASSIGN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.AT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.BANG;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.BIG_DECIMAL_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.BIG_INTEGER_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.BINARY_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.BIT_AND;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.BIT_OR;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.BOOLEAN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.BREAK;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.BYTE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.CASE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.CATCH;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.CHAR;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.CHARACTER_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.CLASS;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.COLON;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.COMMA;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.COMMENT_CONTENT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.CONST;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.CONTINUE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.CTRL_Z;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.DECIMAL_EXPONENT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.DECIMAL_FLOATING_POINT_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.DECIMAL_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.DECR;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.DO;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.DOT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.DOT_DOT_SLASH;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.DOUBLE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.DOUBLECOLON;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.ELLIPSIS;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.ELSE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.ENTER_JAVADOC_COMMENT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.ENTER_MULTILINE_COMMENT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.EOF;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.EQ;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.EXCL_DOT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.EXPORTS;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.EXTENDS;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.FALSE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.FINAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.FINALLY;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.FLOAT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.FLOATING_POINT_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.FOR;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.GE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.GOTO;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.GT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.HASHMARK;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.HEXADECIMAL_EXPONENT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.HEXADECIMAL_FLOATING_POINT_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.HEX_DIGITS;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.HEX_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.HOOK;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.HOUR_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.IDENTIFIER;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.IF;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.IMPLEMENTS;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.IMPORT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.INCR;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.INSTANCEOF;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.INT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.INTEGER_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.INTERFACE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.JAVADOC_COMMENT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.LBRACE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.LBRACKET;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.LE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.LETTER;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.LONG;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.LONG_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.LPAREN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.LSHIFT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.LSHIFTASSIGN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.LT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.MILLISECOND_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.MINUS;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.MINUSASSIGN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.MINUTE_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.MODIFY;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.MODULE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.MULTI_LINE_COMMENT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.MVEL_ENDS_WITH;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.MVEL_LENGTH;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.MVEL_STARTS_WITH;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.NATIVE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.NE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.NEW;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.NOT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.NULL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.OCTAL_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.OLD_MAC_EOL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.OPEN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.OPENS;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.ORASSIGN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.PACKAGE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.PART_LETTER;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.PASSIVE_OOPATH;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.PLUS;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.PLUSASSIGN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.PRIVATE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.PROTECTED;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.PROVIDES;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.PUBLIC;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.RBRACE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.RBRACKET;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.REM;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.REMASSIGN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.REQUIRES;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.RETURN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.RPAREN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.RSIGNEDSHIFT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.RSIGNEDSHIFTASSIGN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.RULE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.RUNSIGNEDSHIFT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.RUNSIGNEDSHIFTASSIGN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.SC_AND;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.SC_OR;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.SECOND_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.SEMICOLON;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.SHORT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.SINGLE_LINE_COMMENT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.SLASH;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.SLASHASSIGN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.SPACE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.STAR;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.STARASSIGN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.STATIC;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.STRICTFP;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.STRING_LITERAL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.SUPER;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.SWITCH;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.SYNCHRONIZED;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.THIS;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.THROW;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.THROWS;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.TILDE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.TO;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.TRANSIENT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.TRANSITIVE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.TRUE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.TRY;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.UNICODE_ESCAPE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.UNIT;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.UNIX_EOL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.USES;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.VOID;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.VOLATILE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.WHEN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.WHILE;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.WINDOWS_EOL;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.WITH;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.XOR;
import static org.drools.mvel.parser.GeneratedMvelParserConstants.XORASSIGN;
import static org.drools.mvel.parser.GeneratedMvelParserConstants._DEFAULT;

/**
 * Complements GeneratedJavaParserConstants
 */
public class TokenTypes {
    public static boolean isWhitespace(int kind) {
        return getCategory(kind).isWhitespace();
    }

    /**
     * @deprecated use isEndOfLineToken
     */
    @Deprecated
    public static boolean isEndOfLineCharacter(int kind) {
        return isEndOfLineToken(kind);
    }

    public static boolean isEndOfLineToken(int kind) {
        return getCategory(kind).isEndOfLine();
    }

    public static boolean isWhitespaceOrComment(int kind) {
        return getCategory(kind).isWhitespaceOrComment();
    }

    public static boolean isSpaceOrTab(int kind) {
        return getCategory(kind).isWhitespaceButNotEndOfLine();
    }

    public static boolean isComment(int kind) {
        return getCategory(kind).isComment();
    }

    /**
     * @deprecated use eolTokenKind
     */
    @Deprecated
    public static int eolToken() {
        return eolTokenKind();
    }

    /**
     * @return the kind of EOL token to use on the platform you're running on.
     */
    public static int eolTokenKind() {
        if (EOL.equals("\n")) {
            return UNIX_EOL;
        }
        if (EOL.equals("\r\n")) {
            return WINDOWS_EOL;
        }
        if (EOL.equals("\r")) {
            return OLD_MAC_EOL;
        }
        throw new AssertionError("Unknown EOL character sequence");
    }

    /**
     * @return the token kind for a single space.
     */
    public static int spaceTokenKind() {
        return SPACE;
    }

    /**
     * @deprecated use spaceTokenKind
     */
    @Deprecated
    public static int spaceToken() {
        return spaceTokenKind();
    }

    /**
     * Category of a token, a little more detailed than
     * <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.5">The JLS</a>.
     */
    public static JavaToken.Category getCategory(int kind) {
        switch (kind) {
            case UNIT:
            case RULE:
            case WHEN:
                return JavaToken.Category.KEYWORD;
            case WINDOWS_EOL:
            case UNIX_EOL:
            case OLD_MAC_EOL:
                return JavaToken.Category.EOL;
            case EOF:
            case SPACE:
            case CTRL_Z:
                return JavaToken.Category.WHITESPACE_NO_EOL;
            case SINGLE_LINE_COMMENT:
            case JAVADOC_COMMENT:
            case MULTI_LINE_COMMENT:
                return JavaToken.Category.COMMENT;
            case ABSTRACT:
            case ASSERT:
            case BOOLEAN:
            case BREAK:
            case BYTE:
            case CASE:
            case CATCH:
            case CHAR:
            case CLASS:
            case CONST:
            case CONTINUE:
            case _DEFAULT:
            case DO:
            case DOUBLE:
            case ELSE:
            case EXTENDS:
            case FALSE:
            case FINAL:
            case FINALLY:
            case FLOAT:
            case FOR:
            case GOTO:
            case IF:
            case IMPLEMENTS:
            case IMPORT:
            case INSTANCEOF:
            case INT:
            case INTERFACE:
            case LONG:
            case NATIVE:
            case NEW:
            case NULL:
            case PACKAGE:
            case PRIVATE:
            case PROTECTED:
            case PUBLIC:
            case RETURN:
            case SHORT:
            case STATIC:
            case STRICTFP:
            case SUPER:
            case SWITCH:
            case SYNCHRONIZED:
            case THIS:
            case THROW:
            case THROWS:
            case TRANSIENT:
            case TRUE:
            case TRY:
            case VOID:
            case VOLATILE:
            case WHILE:
            case REQUIRES:
            case TO:
            case WITH:
            case OPEN:
            case OPENS:
            case USES:
            case MODULE:
            case EXPORTS:
            case PROVIDES:
            case TRANSITIVE:
                return JavaToken.Category.KEYWORD;
            case LONG_LITERAL:
            case INTEGER_LITERAL:
            case DECIMAL_LITERAL:
            case HEX_LITERAL:
            case OCTAL_LITERAL:
            case BINARY_LITERAL:
            case FLOATING_POINT_LITERAL:
            case DECIMAL_FLOATING_POINT_LITERAL:
            case DECIMAL_EXPONENT:
            case HEXADECIMAL_FLOATING_POINT_LITERAL:
            case HEXADECIMAL_EXPONENT:
            case CHARACTER_LITERAL:
            case STRING_LITERAL:
            case MILLISECOND_LITERAL:
            case SECOND_LITERAL:
            case MINUTE_LITERAL:
            case HOUR_LITERAL:
            case BIG_INTEGER_LITERAL:
            case BIG_DECIMAL_LITERAL:
                return JavaToken.Category.LITERAL;
            case IDENTIFIER:
                return JavaToken.Category.IDENTIFIER;
            case LPAREN:
            case RPAREN:
            case LBRACE:
            case RBRACE:
            case LBRACKET:
            case RBRACKET:
            case SEMICOLON:
            case COMMA:
            case DOT:
            case AT:
                return JavaToken.Category.SEPARATOR;
            case MVEL_STARTS_WITH:
            case MVEL_ENDS_WITH:
            case MVEL_LENGTH:
            case NOT:
            case DOT_DOT_SLASH:
            case HASHMARK:
            case EXCL_DOT:
            case PASSIVE_OOPATH:
            case ASSIGN:
            case LT:
            case BANG:
            case TILDE:
            case HOOK:
            case COLON:
            case EQ:
            case LE:
            case GE:
            case NE:
            case SC_OR:
            case SC_AND:
            case INCR:
            case DECR:
            case PLUS:
            case MINUS:
            case STAR:
            case SLASH:
            case BIT_AND:
            case BIT_OR:
            case XOR:
            case REM:
            case LSHIFT:
            case PLUSASSIGN:
            case MINUSASSIGN:
            case STARASSIGN:
            case SLASHASSIGN:
            case ANDASSIGN:
            case ORASSIGN:
            case XORASSIGN:
            case REMASSIGN:
            case LSHIFTASSIGN:
            case RSIGNEDSHIFTASSIGN:
            case RUNSIGNEDSHIFTASSIGN:
            case ELLIPSIS:
            case ARROW:
            case DOUBLECOLON:
            case RUNSIGNEDSHIFT:
            case RSIGNEDSHIFT:
            case GT:
            case MODIFY:
                return JavaToken.Category.OPERATOR;
            case ENTER_JAVADOC_COMMENT:
            case ENTER_MULTILINE_COMMENT:
            case COMMENT_CONTENT:
            case HEX_DIGITS:
            case LETTER:
            case UNICODE_ESCAPE:
            case PART_LETTER:
            default:
                throw new AssertionError("Invalid token kind " + kind);
        }
    }
}
