package org.drools.verifier.core.cache.inspectors.condition;

import static java.lang.String.format;
import static org.mockito.Mockito.mock;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldCondition;

public class ConditionInspectorUtils {


    public static String getAssertDescription(ComparableConditionInspector a,
    		ComparableConditionInspector b,
            boolean conflictExpected) {
    			return format("Expected condition '%s' %sto subsume condition '%s':",
    					a.toHumanReadableString(),
    					conflictExpected ? "" : "not ",
    					b.toHumanReadableString());
    }

    public static String getAssertDescriptionForRedundant(final ComparableConditionInspector a,
	                                    final ComparableConditionInspector b,
	                                    final boolean conflictExpected) {
	    return format("Expected conditions '%s' and '%s' %sto be redundant:",
	            a.toHumanReadableString(),
	            b.toHumanReadableString(),
	            conflictExpected ? "" : "not ");
	}

    public static StringConditionInspector getStringCondition(Field field, final Values values, final String operator) {
	    AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
	    return new StringConditionInspector(fieldCondition(field, values, operator), configurationMock);
	}

    public static NumericIntegerConditionInspector getNumericCondition(Field field, int value, String operator) {
	    AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
	    return new NumericIntegerConditionInspector(fieldCondition(field, value, operator), configurationMock);
	}

    public static ComparableConditionInspector getComparableCondition(Field field, Comparable value, String operator) {
	    AnalyzerConfigurationMock configurationMock = new AnalyzerConfigurationMock();
	    return new ComparableConditionInspector(fieldCondition(field, value, operator), configurationMock);
	}

	public static BooleanConditionInspector getBooleanCondition(Field field, boolean value, String operator) {
	    return new BooleanConditionInspector(fieldCondition(field, Boolean.valueOf(value), operator),
	            new AnalyzerConfigurationMock());
	}
	
	
	public static <T> FieldCondition fieldCondition(Field field, Comparable<T> value, String operator) {
		return new FieldCondition<>(field, mock(Column.class), operator, new Values<>(value),
                        new AnalyzerConfigurationMock());
	}
	
	public static <T> FieldCondition fieldCondition(Field field, Values value, String operator) {
		return new FieldCondition<>(field, mock(Column.class), operator, value,
                        new AnalyzerConfigurationMock());
	}
}
