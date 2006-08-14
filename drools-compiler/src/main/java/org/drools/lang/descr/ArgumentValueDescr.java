package org.drools.lang.descr;

import java.io.Serializable;

/**
 * This holds the value of an argument that has been parsed. 
 * The argument would then be passed to a method, or function etc. 
 * 
 * @author Michael Neale
 *
 */
public class ArgumentValueDescr implements Serializable {

	private static final long serialVersionUID = -8921442520702424678L;
	
	/** Obviously if it was in quotes, its a string literal (which could be anything) */
	public static final int STRING = 1;
	
	/** Means true integer, not Javas interpretation of it */
	public static final int INTEGRAL = 2;
	
	/** Means a decimal number, which may or may not be floating */
	public static final int DECIMAL = 4;
	
	/** If its none of the above, then its a variable */
	public static final int VARIABLE = 8;
	
	public static final int BOOLEAN = 16;
	
	public static final int NULL = 32;
	
	private final int type;
	private final String value;
	
	/**
	 * @param type One of the constant types.
	 * @param value
	 */
	public ArgumentValueDescr(int type, String value) {
		this.type = type;
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	
	public boolean isVariable() {
		return type == VARIABLE;
	}
	
	
	
	
}
