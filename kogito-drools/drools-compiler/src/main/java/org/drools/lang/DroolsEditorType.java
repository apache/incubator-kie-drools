package org.drools.lang;

/**
 * Enum to identify an editor type (most for syntax highlighting). This
 * is used on DroolsTree and DroolsToken.
 */
public enum DroolsEditorType {
    KEYWORD, CODE_CHUNK, SYMBOL, NUMERIC_CONST, BOOLEAN_CONST, STRING_CONST, NULL_CONST, IDENTIFIER, IDENTIFIER_VARIABLE, IDENTIFIER_TYPE, IDENTIFIER_PATTERN;
}
