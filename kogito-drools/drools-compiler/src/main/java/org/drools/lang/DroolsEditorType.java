package org.drools.lang;

/**
 * Simple holder class identifying a DroolsTree editor type. This is stored in
 * DroolsTree to be used on editor for syntax highlighting.
 * 
 * @author porcelli
 */
public class DroolsEditorType {
	public static int KEYWORD = 0;
	public static int CODE_CHUNK = 1;
	public static int SYMBOL = 2;
	public static int NUMERIC_CONST = 3;
	public static int BOOLEAN_CONST = 4;
	public static int STRING_CONST = 5;
	public static int NULL_CONST = 6;
	public static int IDENTIFIER = 7;
	public static int IDENTIFIER_TYPE = 8;
	public static int IDENTIFIER_PATTERN = 9;
}