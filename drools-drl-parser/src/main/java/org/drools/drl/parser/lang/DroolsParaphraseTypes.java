package org.drools.drl.parser.lang;

/**
 * Simple enum to identify a paraphrase type. This enum is used to better format
 * error messages during parsing.
 */
public enum DroolsParaphraseTypes {
    PACKAGE, 
    UNIT,
    IMPORT,
    FUNCTION_IMPORT, 
    ACCUMULATE_IMPORT, 
    GLOBAL, 
    FUNCTION, 
    QUERY, 
    TEMPLATE, 
    RULE, 
    RULE_ATTRIBUTE, 
    PATTERN, 
    TYPE_DECLARE, 
    EVAL, 
    ENTRYPOINT_DECLARE, 
    WINDOW_DECLARE,
	ENUM_DECLARE;

}
