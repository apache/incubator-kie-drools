package org.drools.lang;

/**
 * Simple holder class identifying a paraphrase types. This is stored in
 * DRLParser paraphrase internal structure to be used on error messages.
 * 
 * @author porcelli
 */
public class DroolsParaphareseTypes {
	public static int PACKAGE = 0;
	public static int IMPORT = 1;
	public static int FUNCTION_IMPORT = 2;
	public static int GLOBAL = 3;
	public static int FUNCTION = 4;
	public static int QUERY = 5;
	public static int TEMPLATE = 6;
	public static int RULE = 7;
	public static int RULE_ATTRIBUTE = 8;
	public static int PATTERN = 9;
}