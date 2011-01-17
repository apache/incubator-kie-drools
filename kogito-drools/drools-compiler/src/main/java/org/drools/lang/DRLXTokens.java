package org.drools.lang;

/**
 * A class to store token definitions for the DRL parser/lexer
 */
public class DRLXTokens {
    public static final String[] tokenNames = new String[]{
                                            "<invalid>", "<EOR>", "<DOWN>", "<UP>", "VT_COMPILATION_UNIT", "VT_FUNCTION_IMPORT", "VT_FACT", "VT_CONSTRAINTS", "VT_LABEL", "VT_QUERY_ID", "VT_TYPE_DECLARE_ID", "VT_TYPE_NAME",
                                            "VT_RULE_ID", "VT_ENTRYPOINT_ID", "VT_RULE_ATTRIBUTES", "VT_PKG_ATTRIBUTES", "VT_RHS_CHUNK", "VT_CURLY_CHUNK", "VT_SQUARE_CHUNK", "VT_PAREN_CHUNK", "VT_BEHAVIOR", "VT_AND_IMPLICIT", "VT_AND_PREFIX",
                                            "VT_OR_PREFIX", "VT_AND_INFIX",
                                            "VT_OR_INFIX", "VT_ACCUMULATE_INIT_CLAUSE", "VT_ACCUMULATE_ID_CLAUSE", "VT_FROM_SOURCE", "VT_EXPRESSION_CHAIN", "VT_FOR_CE", "VT_FOR_FUNCTIONS", "VT_PATTERN", "VT_FACT_BINDING", "VT_FACT_OR",
                                            "VT_BIND_FIELD", "VT_FIELD",
                                            "VT_ACCESSOR_PATH", "VT_ACCESSOR_ELEMENT", "VT_DATA_TYPE", "VT_PATTERN_TYPE", "VT_PACKAGE_ID", "VT_IMPORT_ID", "VT_GLOBAL_ID", "VT_FUNCTION_ID", "VT_PARAM_LIST", "VT_ARGUMENTS", "VT_EXPRESSION",
                                            "VK_DATE_EFFECTIVE", "VK_DATE_EXPIRES",
                                            "VK_LOCK_ON_ACTIVE", "VK_NO_LOOP", "VK_AUTO_FOCUS", "VK_ACTIVATION_GROUP", "VK_AGENDA_GROUP", "VK_RULEFLOW_GROUP", "VK_TIMER", "VK_CALENDARS", "VK_DIALECT", "VK_SALIENCE", "VK_ENABLED", "VK_ATTRIBUTES",
                                            "VK_RULE", "VK_EXTEND",
                                            "VK_IMPLEMENTS", "VK_IMPORT", "VK_PACKAGE", "VK_QUERY", "VK_DECLARE", "VK_FUNCTION", "VK_GLOBAL", "VK_EVAL", "VK_ENTRY_POINT", "VK_NOT", "VK_IN", "VK_OR", "VK_AND", "VK_EXISTS", "VK_FORALL", "VK_FOR",
                                            "VK_ACTION", "VK_REVERSE",
                                            "VK_RESULT", "VK_OPERATOR", "VK_END", "VK_INIT", "VK_INSTANCEOF", "VK_EXTENDS", "VK_SUPER", "VK_PRIMITIVE_TYPE", "VK_THIS", "VK_VOID", "VK_CLASS", "VK_NEW", "VK_FINAL", "VK_IF", "VK_ELSE", "VK_WHILE",
                                            "VK_DO", "VK_CASE", "VK_DEFAULT",
                                            "VK_TRY", "VK_CATCH", "VK_FINALLY", "VK_SWITCH", "VK_SYNCHRONIZED", "VK_RETURN", "VK_THROW", "VK_BREAK", "VK_CONTINUE", "VK_ASSERT", "VK_MODIFY", "VK_STATIC", "VK_PUBLIC", "VK_PROTECTED", "VK_PRIVATE",
                                            "VK_ABSTRACT", "VK_NATIVE",
                                            "VK_TRANSIENT", "VK_VOLATILE", "VK_STRICTFP", "VK_THROWS", "VK_INTERFACE", "VK_ENUM", "SIGNED_DECIMAL", "SIGNED_HEX", "SIGNED_FLOAT", "VT_PROP_KEY", "VT_PROP_VALUE", "SEMICOLON", "ID", "DOT", "DOT_STAR",
                                            "STRING", "AT", "COLON",
                                            "EQUALS_ASSIGN", "WHEN", "COMMA", "BOOL", "LEFT_PAREN", "RIGHT_PAREN", "FROM", "OVER", "TimePeriod", "DECIMAL", "ACCUMULATE", "COLLECT", "DOUBLE_PIPE", "DOUBLE_AMPER", "ARROW", "EQUALS", "GREATER",
                                            "GREATER_EQUALS", "LESS",
                                            "LESS_EQUALS", "NOT_EQUALS", "LEFT_SQUARE", "RIGHT_SQUARE", "NULL", "PLUS", "MINUS", "HEX", "FLOAT", "THEN", "LEFT_CURLY", "RIGHT_CURLY", "QUESTION", "PIPE", "XOR", "AMPER", "SHIFT_LEFT",
                                            "SHIFT_RIGHT_UNSIG", "SHIFT_RIGHT", "STAR",
                                            "DIV", "MOD", "INCR", "DECR", "TILDE", "NEGATION", "PLUS_ASSIGN", "MINUS_ASSIGN", "MULT_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "EOL", "WS", "Exponent",
                                            "FloatTypeSuffix", "HexDigit",
                                            "IntegerTypeSuffix", "EscapeSequence", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "IdentifierStart", "IdentifierPart", "MISC"
                                                        };

}
