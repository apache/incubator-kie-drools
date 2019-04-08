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

package org.drools.constraint.parser;

import com.github.javaparser.JavaToken;

import static com.github.javaparser.utils.Utils.EOL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.ABSTRACT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.ANDASSIGN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.ARROW;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.ASSERT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.ASSIGN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.AT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.BANG;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.BIG_DECIMAL_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.BIG_INTEGER_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.BINARY_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.BIT_AND;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.BIT_OR;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.BOOLEAN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.BREAK;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.BYTE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.CASE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.CATCH;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.CHAR;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.CHARACTER_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.CLASS;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.COLON;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.COMMA;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.COMMENT_CONTENT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.CONST;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.CONTINUE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.CTRL_Z;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.DECIMAL_EXPONENT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.DECIMAL_FLOATING_POINT_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.DECIMAL_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.DECR;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.DO;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.DOT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.DOUBLE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.DOUBLECOLON;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.ELLIPSIS;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.ELSE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.ENTER_JAVADOC_COMMENT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.ENTER_MULTILINE_COMMENT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.EOF;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.EQ;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.EXPORTS;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.EXTENDS;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.FALSE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.FINAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.FINALLY;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.FLOAT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.FLOATING_POINT_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.FOR;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.GE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.GOTO;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.GT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.HEXADECIMAL_EXPONENT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.HEXADECIMAL_FLOATING_POINT_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.HEX_DIGITS;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.HEX_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.HOOK;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.HOUR_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.IDENTIFIER;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.IF;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.IMPLEMENTS;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.IMPORT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.INCR;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.INSTANCEOF;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.INT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.INTEGER_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.INTERFACE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.JAVADOC_COMMENT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.LBRACE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.LBRACKET;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.LE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.LETTER;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.LONG;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.LONG_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.LPAREN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.LSHIFT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.LSHIFTASSIGN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.LT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.MILLISECOND_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.MINUS;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.MINUSASSIGN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.MINUTE_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.MODIFY;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.MODULE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.MULTI_LINE_COMMENT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.MVEL_ENDS_WITH;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.MVEL_LENGTH;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.MVEL_STARTS_WITH;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.NATIVE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.NE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.NEW;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.NOT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.NULL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.OCTAL_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.OLD_MAC_EOL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.OPEN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.OPENS;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.ORASSIGN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.PACKAGE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.PART_LETTER;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.PLUS;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.PLUSASSIGN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.PRIVATE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.PROTECTED;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.PROVIDES;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.PUBLIC;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.RBRACE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.RBRACKET;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.REM;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.REMASSIGN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.REQUIRES;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.RETURN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.RPAREN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.RSIGNEDSHIFT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.RSIGNEDSHIFTASSIGN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.RULE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.RUNSIGNEDSHIFT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.RUNSIGNEDSHIFTASSIGN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.SC_AND;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.SC_OR;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.SECOND_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.SEMICOLON;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.SHORT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.SINGLE_LINE_COMMENT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.SLASH;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.SLASHASSIGN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.SPACE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.STAR;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.STARASSIGN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.STATIC;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.STRICTFP;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.STRING_LITERAL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.SUPER;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.SWITCH;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.SYNCHRONIZED;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.THIS;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.THROW;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.THROWS;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.TILDE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.TO;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.TRANSIENT;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.TRANSITIVE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.TRUE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.TRY;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.UNICODE_ESCAPE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.UNIX_EOL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.USES;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.VOID;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.VOLATILE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.WHILE;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.WINDOWS_EOL;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.WITH;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.XOR;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants.XORASSIGN;
import static org.drools.constraint.parser.GeneratedDrlConstraintParserConstants._DEFAULT;

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
            case RULE:
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
            case 151:
            case 152:
            case 153:
            // The following are tokens that are only used internally by the lexer
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
            // These are DRLX tokens, They don't have the constants generated
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
