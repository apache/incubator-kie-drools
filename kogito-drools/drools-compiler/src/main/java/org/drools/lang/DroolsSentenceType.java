package org.drools.lang;

/**
 * Enum to identify a sentence type. This is used by DRLParser and stored into
 * DroolsSentence.
 * 
 * @author porcelli
 * @see DroolsSentence
 */
public enum DroolsSentenceType {
	PACKAGE, FUNCTION_IMPORT_STATEMENT, IMPORT_STATEMENT, GLOBAL, FUNCTION, TEMPLATE, TYPE_DECLARATION, RULE, QUERY, EVAL;
}