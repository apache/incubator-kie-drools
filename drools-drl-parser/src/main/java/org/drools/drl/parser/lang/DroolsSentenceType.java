package org.drools.drl.parser.lang;

/**
 * Enum to identify a sentence type. This is used by DRLParser and stored into
 * DroolsSentence.
 * 
 * @see DroolsSentence
 */
public enum DroolsSentenceType {
    PACKAGE, 
    UNIT,
	FUNCTION_IMPORT_STATEMENT,
    ACCUMULATE_IMPORT_STATEMENT, 
    IMPORT_STATEMENT, 
	GLOBAL, 
	FUNCTION, 
	TEMPLATE, 
	TYPE_DECLARATION, 
	RULE, 
	QUERY, 
	EVAL, 
	ENTRYPOINT_DECLARATION, 
	WINDOW_DECLARATION, 
	ENUM_DECLARATION;
}
