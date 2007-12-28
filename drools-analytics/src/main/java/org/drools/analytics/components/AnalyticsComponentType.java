package org.drools.analytics.components;

/**
 * 
 * @author Toni Rikkola
 */
public class AnalyticsComponentType{
	public static final AnalyticsComponentType NOTHING = new AnalyticsComponentType(0);
	public static final AnalyticsComponentType CLASS = new AnalyticsComponentType(1);
	public static final AnalyticsComponentType FIELD = new AnalyticsComponentType(2);
	public static final AnalyticsComponentType RULE = new AnalyticsComponentType(3);
	public static final AnalyticsComponentType CONSTRAINT = new AnalyticsComponentType(4);
	public static final AnalyticsComponentType VARIABLE = new AnalyticsComponentType(5);
	public static final AnalyticsComponentType PATTERN = new AnalyticsComponentType(6);
	public static final AnalyticsComponentType PATTERN_POSSIBILITY = new AnalyticsComponentType(7);
	public static final AnalyticsComponentType RULE_POSSIBILITY = new AnalyticsComponentType(8);
	public static final AnalyticsComponentType RESTRICTION =new AnalyticsComponentType(9);
	public static final AnalyticsComponentType OPERATOR = new AnalyticsComponentType(10);
	public static final AnalyticsComponentType FIELD_CLASS_LINK = new AnalyticsComponentType(11);
	public static final AnalyticsComponentType COLLECT = new AnalyticsComponentType(12);
	public static final AnalyticsComponentType ACCUMULATE = new AnalyticsComponentType(13);
	public static final AnalyticsComponentType FROM = new AnalyticsComponentType(14);
	public static final AnalyticsComponentType EVAL = new AnalyticsComponentType(15);
	public static final AnalyticsComponentType PREDICATE = new AnalyticsComponentType(16);
	public static final AnalyticsComponentType METHOD_ACCESSOR = new AnalyticsComponentType(17);
	public static final AnalyticsComponentType FIELD_ACCESSOR = new AnalyticsComponentType(18);
	public static final AnalyticsComponentType FUNCTION_CALL = new AnalyticsComponentType(19);
	public static final AnalyticsComponentType ACCESSOR = new AnalyticsComponentType(20);
	public static final AnalyticsComponentType RULE_PACKAGE = new AnalyticsComponentType(21);
	public static final AnalyticsComponentType CONSEQUENCE = new AnalyticsComponentType(22);

	private final int index;

	private AnalyticsComponentType(int i) {
		index = i;
	}
}
