package org.drools.verifier.core.cache.inspectors.condition;

import java.util.Date;

import org.drools.verifier.core.AnalyzerConfigurationMock;
import org.drools.verifier.core.index.model.Field;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.fieldCondition;
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.getComparableCondition;

@ExtendWith(MockitoExtension.class)
public class DateConditionInspectorSubsumptionTest {

    @Mock
    private Field field;

    @Test
    void testSubsume001() throws Exception {
        ComparableConditionInspector<Date> a = getComparableCondition(field, new Date(100), "!=");
        ComparableConditionInspector<Date> b = getComparableCondition(field, new Date(100), "!=");

        assertThat(a.subsumes(b)).isTrue();
        assertThat(b.subsumes(a)).isTrue();
    }

    @Test
    void testSubsumeEquals001() throws Exception {
        ComparableConditionInspector<Date> a = getComparableCondition(field, new Date(100), "==");
        ComparableConditionInspector<Date> b = getComparableCondition(field, new Date(10), ">");

        assertThat(a.subsumes(b)).isFalse();
        assertThat(b.subsumes(a)).isTrue();
    }

    @Test
    void testSubsumeEquals002() throws Exception {
        ComparableConditionInspector<Date> a = getComparableCondition(field, new Date(10), "==");
        ComparableConditionInspector<Date> b = getComparableCondition(field, new Date(100), ">");

        assertThat(a.subsumes(b)).isFalse();
        assertThat(b.subsumes(a)).isFalse();
    }
}