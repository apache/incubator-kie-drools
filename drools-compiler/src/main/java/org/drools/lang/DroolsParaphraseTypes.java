package org.drools.lang;

/**
 * Simple enum to identify a paraphrase type. This enum is used to better format
 * error messages during parsing.
 * 
 * @author porcelli
 */
public enum DroolsParaphraseTypes {
    PACKAGE, IMPORT, FUNCTION_IMPORT, GLOBAL, FUNCTION, QUERY, TEMPLATE, RULE, RULE_ATTRIBUTE, PATTERN, TYPE_DECLARE, EVAL;
}
