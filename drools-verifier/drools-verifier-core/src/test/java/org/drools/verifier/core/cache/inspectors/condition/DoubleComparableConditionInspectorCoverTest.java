package org.drools.verifier.core.cache.inspectors.condition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.drools.verifier.core.index.model.Field;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.getComparableCondition;

@ExtendWith(MockitoExtension.class)
public class DoubleComparableConditionInspectorCoverTest {

	@Mock
    private Field field;

    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                // condition value, condition operator, value, covers
                {0.5d, "==", 0.5d, true},
                {0.5d, "<", 0.5d, false},
                {0.5d, "<", 10.5d, false},
                {10.5d, "<", 0.5d, true},
                {0.5d, ">", 0.5d, false},
                {0.5d, ">", 10.5d, true},
                {10.5d, "==", 0.5d, false},
                {0.5d, "==", 10.5d, false},
                {10.5d, ">", 0.5d, false},
                {-1.5d, ">", 0.5d, true},
                {0.5d, ">", -1.5d, false},
                {-1.5d, "==", 0.5d, false},
                {0.5d, "==", -1.5d, false},
                {new Date(0), "==", new Date(0), true},
                {new Date(0), "==", new Date(1), false},
                {new Date(0), "!=", new Date(0), false},
                {new Date(0), "!=", new Date(1), true},
                {new Date(0), "after", new Date(1), true},
                {new Date(1), "after", new Date(0), false},
                {new Date(0), "before", new Date(1), false},
                {new Date(1), "before", new Date(0), true}
        });
    }

    @MethodSource("testData")
    @ParameterizedTest(name = "it is {3} that {0} {1} {2}")
    void parametrizedTest(Comparable conditionValue, String conditionOperator, Comparable value, boolean coverExpected) {
        ComparableConditionInspector a = getComparableCondition(field, conditionValue, conditionOperator);

        assertThat(a.covers(value)).as(getAssertDescription(a,
                value,
                coverExpected)).isEqualTo(coverExpected);
    }

    private String getAssertDescription(ComparableConditionInspector a,
                                        Comparable b,
                                        boolean conflictExpected) {
        return format("Expected condition '%s' %sto cover value '%s':",
                a.toHumanReadableString(),
                conflictExpected ? "" : "not ",
                b.toString());
    }
}