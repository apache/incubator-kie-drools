package org.drools.verifier.core.cache.inspectors.condition;

import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Field;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorUtils.getStringCondition;

@ExtendWith(MockitoExtension.class)
public class StringConditionInspectorOverlapTest {

    @Mock
    private Field field;

    @Test
    void test001() throws Exception {
        StringConditionInspector a = getStringCondition(field, new Values<>("Toni"), "==");
        StringConditionInspector b = getStringCondition(field, new Values<>("Toni"), "!=");

        assertThat(a.overlaps(b)).isFalse();
        assertThat(b.overlaps(a)).isFalse();
    }

    @Test
    void test002() throws Exception {
        StringConditionInspector a = getStringCondition(field, new Values<>("Toni"), "==");
        StringConditionInspector b = getStringCondition(field, new Values<>("Eder"), "!=");

        assertThat(a.overlaps(b)).isTrue();
        assertThat(b.overlaps(a)).isTrue();
    }

    @Test
    void test003() throws Exception {
        StringConditionInspector a = getStringCondition(field, new Values<>("Toni", "Michael", "Eder"), "in");
        StringConditionInspector b = getStringCondition(field, new Values<>("Toni"), "!=");

        assertThat(a.overlaps(b)).isTrue();
        assertThat(b.overlaps(a)).isTrue();
    }

    @Test
    void test004() throws Exception {
        StringConditionInspector a = getStringCondition(field, new Values<>("Toni", "Michael", "Eder"), "in");
        StringConditionInspector b = getStringCondition(field, new Values<>("Toni"), "==");

        assertThat(a.overlaps(b)).isTrue();
        assertThat(b.overlaps(a)).isTrue();
    }

    @Test
    void test005() throws Exception {
        StringConditionInspector a = getStringCondition(field, new Values<>("Toni", "Michael"), "in");
        StringConditionInspector b = getStringCondition(field, new Values<>("Eder"), "==");

        assertThat(a.overlaps(b)).isFalse();
        assertThat(b.overlaps(a)).isFalse();
    }

    @Test
    void test006() throws Exception {
        StringConditionInspector a = getStringCondition(field, new Values<>("Toni", "Michael"), "in");
        StringConditionInspector b = getStringCondition(field, new Values<>("Eder"), "!=");

        assertThat(a.overlaps(b)).isTrue();
        assertThat(b.overlaps(a)).isTrue();
    }

    @Test
    void test007() throws Exception {
        StringConditionInspector a = getStringCondition(field, new Values<>("Toni", "Michael"), "in");
        StringConditionInspector b = getStringCondition(field, new Values<>("Eder", "John"), "in");

        assertThat(a.overlaps(b)).isFalse();
        assertThat(b.overlaps(a)).isFalse();
    }

    @Test
    void test008() throws Exception {
        StringConditionInspector a = getStringCondition(field, new Values<>("Toni", "Michael"), "in");
        StringConditionInspector b = getStringCondition(field, new Values<>("Toni", "Eder"), "in");

        assertThat(a.overlaps(b)).isTrue();
        assertThat(b.overlaps(a)).isTrue();
    }

    @Test
    void test009() throws Exception {
        StringConditionInspector a = getStringCondition(field, new Values<>("Toni"), "in");
        StringConditionInspector b = getStringCondition(field, new Values<>("Eder", "Toni"), "in");

        assertThat(a.overlaps(b)).isTrue();
        assertThat(b.overlaps(a)).isTrue();
    }

    @Test
    void test010() throws Exception {
        StringConditionInspector a = getStringCondition(field, new Values<>(""), "==");
        StringConditionInspector b = getStringCondition(field, new Values<>(""), "==");

        assertThat(a.overlaps(b)).isFalse();
        assertThat(b.overlaps(a)).isFalse();
    }

    @Test
    void test011() throws Exception {
        StringConditionInspector a = getStringCondition(field, new Values<>("Toni"), "==");
        StringConditionInspector b = getStringCondition(field, new Values<>("Toni"), "==");

        assertThat(a.overlaps(b)).isTrue();
        assertThat(b.overlaps(a)).isTrue();
    }

}