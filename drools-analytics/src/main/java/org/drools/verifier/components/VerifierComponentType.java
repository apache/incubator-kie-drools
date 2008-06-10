package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierComponentType{
	public static final VerifierComponentType NOTHING = new VerifierComponentType(0);
	public static final VerifierComponentType CLASS = new VerifierComponentType(1);
	public static final VerifierComponentType FIELD = new VerifierComponentType(2);
	public static final VerifierComponentType RULE = new VerifierComponentType(3);
	public static final VerifierComponentType CONSTRAINT = new VerifierComponentType(4);
	public static final VerifierComponentType VARIABLE = new VerifierComponentType(5);
	public static final VerifierComponentType PATTERN = new VerifierComponentType(6);
	public static final VerifierComponentType PATTERN_POSSIBILITY = new VerifierComponentType(7);
	public static final VerifierComponentType RULE_POSSIBILITY = new VerifierComponentType(8);
	public static final VerifierComponentType RESTRICTION =new VerifierComponentType(9);
	public static final VerifierComponentType OPERATOR = new VerifierComponentType(10);
	public static final VerifierComponentType FIELD_CLASS_LINK = new VerifierComponentType(11);
	public static final VerifierComponentType COLLECT = new VerifierComponentType(12);
	public static final VerifierComponentType ACCUMULATE = new VerifierComponentType(13);
	public static final VerifierComponentType FROM = new VerifierComponentType(14);
	public static final VerifierComponentType EVAL = new VerifierComponentType(15);
	public static final VerifierComponentType PREDICATE = new VerifierComponentType(16);
	public static final VerifierComponentType METHOD_ACCESSOR = new VerifierComponentType(17);
	public static final VerifierComponentType FIELD_ACCESSOR = new VerifierComponentType(18);
	public static final VerifierComponentType FUNCTION_CALL = new VerifierComponentType(19);
	public static final VerifierComponentType ACCESSOR = new VerifierComponentType(20);
	public static final VerifierComponentType RULE_PACKAGE = new VerifierComponentType(21);
	public static final VerifierComponentType CONSEQUENCE = new VerifierComponentType(22);

	private final int index;

	private VerifierComponentType(int i) {
		index = i;
	}
}
