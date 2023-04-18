package org.drools.verifier.core.cache.inspectors.condition;

import static java.lang.String.format;
import static org.mockito.Mockito.mock;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldCondition;

public class ConditionInspectorUtils {
	
	public static <T> FieldCondition fieldCondition(Field field, Comparable<T> value, String operator) {
		return new FieldCondition<>(field, mock(Column.class), operator, new Values<>(value),
                        new AnalyzerConfigurationMock());
	}
	
	public static <T> FieldCondition fieldCondition(Field field, Values value, String operator) {
		return new FieldCondition<>(field, mock(Column.class), operator, value,
                        new AnalyzerConfigurationMock());
	}

    public static String getAssertDescription(ComparableConditionInspector a,
    		ComparableConditionInspector b,
            boolean conflictExpected) {
    			return format("Expected condition '%s' %sto subsume condition '%s':",
    					a.toHumanReadableString(),
    					conflictExpected ? "" : "not ",
    					b.toHumanReadableString());
    }
}
