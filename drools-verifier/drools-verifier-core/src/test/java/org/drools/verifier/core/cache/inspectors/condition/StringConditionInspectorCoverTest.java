package org.drools.verifier.core.cache.inspectors.condition;

import java.util.Arrays;
import java.util.Collection;

import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.relations.Operator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.getStringCondition;

@ExtendWith(MockitoExtension.class)
public class StringConditionInspectorCoverTest {

	@Mock
    private Field field;

    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {new Values<>("toni"), Operator.EQUALS.toString(), new Values<>("toni"), true},
                {new Values<>("toni"), Operator.MATCHES.toString(), new Values<>("toni"), true},
                {new Values<>("toni"), Operator.SOUNDSLIKE.toString(), new Values<>("toni"), true},
                {new Values<>("toni", "eder"), Operator.IN.toString(), new Values<>("toni"), true},
                {new Values<>("toni"), Operator.GREATER_OR_EQUAL.toString(), new Values<>("toni"), true},
                {new Values<>("toni"), Operator.LESS_OR_EQUAL.toString(), new Values<>("toni"), true},

                {new Values<>("toni"), Operator.LESS_THAN.toString(), new Values<>("toni"), false},
                {new Values<>("toni"), Operator.GREATER_THAN.toString(), new Values<>("toni"), false},

                {new Values<>("toni"), Operator.EQUALS.toString(), new Values<>("michael"), false},
                {new Values<>("toni"), Operator.MATCHES.toString(), new Values<>("michael"), false},
                {new Values<>("toni"), Operator.SOUNDSLIKE.toString(), new Values<>("michael"), false},
                {new Values<>("toni", "eder"), Operator.IN.toString(), new Values<>("michael"), false},
                {new Values<>("toni"), Operator.GREATER_OR_EQUAL.toString(), new Values<>("michael"), false},
                {new Values<>("toni"), Operator.LESS_OR_EQUAL.toString(), new Values<>("michael"), false},

                {new Values<>("toni", "eder"), Operator.NOT_IN.toString(), new Values<>("michael"), true},
                {new Values<>("toni", "eder"), Operator.NOT_IN.toString(), new Values<>("eder"), false},

                {new Values<>("toni"), Operator.NOT_EQUALS.toString(), new Values<>("toni"), false},
                {new Values<>("toni"), Operator.NOT_EQUALS.toString(), new Values<>("eder"), true},

                {new Values<>("toni"), Operator.NOT_MATCHES.toString(), new Values<>("toni"), false},
                {new Values<>("toni"), Operator.NOT_MATCHES.toString(), new Values<>("eder"), true},

                {new Values<>("toni rikkola"), Operator.STR_ENDS_WITH.toString(), new Values<>("rikkola"), true},
                {new Values<>("toni rikkola"), Operator.STR_ENDS_WITH.toString(), new Values<>("toni"), false},
                {new Values<>("toni rikkola"), Operator.STR_STARTS_WITH.toString(), new Values<>("toni"), true},
                {new Values<>("toni rikkola"), Operator.STR_STARTS_WITH.toString(), new Values<>("rikkola"), false},

                // No matter what we do this returns false
                {new Values<>("array"), Operator.CONTAINS.toString(), new Values<>("toni", "eder"), false},
                {new Values<>("array"), Operator.CONTAINS.toString(), new Values<>("toni"), false},
                {new Values<>("array"), Operator.CONTAINS.toString(), new Values<>("eder"), false},
                {new Values<>("array"), Operator.NOT_CONTAINS.toString(), new Values<>("toni", "eder"), false},
                {new Values<>("array"), Operator.NOT_CONTAINS.toString(), new Values<>("toni"), false},
                {new Values<>("array"), Operator.NOT_CONTAINS.toString(), new Values<>("eder"), false},
        });
    }

    @MethodSource("testData")
    @ParameterizedTest
    void parametrizedTest(Values<String> value1, String operator, Values<String> value2, boolean covers) {
        StringConditionInspector a = getStringCondition(field, value1, operator);

        assertThat(a.covers(value2.iterator().next())).as(getAssertDescription(a,
                covers,
                                                  value2.iterator()
                                                          .next())).isEqualTo(covers);
    }

    private String getAssertDescription(final StringConditionInspector a,
                                        final boolean covers,
                                        final String condition) {
        return format("Expected condition '%s' to %s cover '%s':",
                a.toHumanReadableString(),
                covers ? "" : "not ",
                condition);
    }
}