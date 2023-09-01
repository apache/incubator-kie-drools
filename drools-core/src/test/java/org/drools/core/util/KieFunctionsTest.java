package org.drools.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class KieFunctionsTest {

    @Test
    public void testKieFunctions() {

        assertThat(KieFunctions.isNull(null)).isTrue();
        assertThat(KieFunctions.isNull("nothing")).isFalse();

        assertThat(KieFunctions.isEmpty(null)).isTrue();
        assertThat(KieFunctions.isEmpty("")).isTrue();
        assertThat(KieFunctions.isEmpty(" ")).isFalse();


        assertThat(KieFunctions.equalsTo(55, "55")).isTrue();
        assertThat(KieFunctions.equalsTo(55, "550")).isFalse();

        assertThat(KieFunctions.equalsTo(new BigDecimal("322.123"), "322.123")).isTrue();
        assertThat(KieFunctions.equalsTo(new BigDecimal("322.123"), "3322.123")).isFalse();

        assertThat(KieFunctions.equalsTo(new BigInteger("123456"), "123456")).isTrue();
        assertThat(KieFunctions.equalsTo(new BigInteger("123456"), "1234567")).isFalse();

        assertThat(KieFunctions.equalsTo((String) null, null)).isTrue();
        assertThat(KieFunctions.equalsTo((String) null, "a")).isFalse();
        assertThat(KieFunctions.equalsTo("f", null)).isFalse();

        assertThat(KieFunctions.equalsTo((Integer) null, null)).isTrue();
        assertThat(KieFunctions.equalsTo((Integer) null, "1")).isFalse();

        assertThat(KieFunctions.equalsToIgnoreCase(null, null)).isTrue();
        assertThat(KieFunctions.equalsToIgnoreCase("TEST", "test")).isTrue();
        assertThat(KieFunctions.equalsToIgnoreCase(null, "a")).isFalse();
        assertThat(KieFunctions.equalsToIgnoreCase("f", null)).isFalse();

        boolean comparitionFailed = false;
        try {
            assertThat(KieFunctions.equalsTo(44, null)).isFalse();
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        assertThat(comparitionFailed).isTrue();


        assertThat(KieFunctions.contains("welcome to jamaica", "jama")).isTrue();
        assertThat(KieFunctions.contains("welcome to jamaica", "Jama")).isFalse();
        assertThat(KieFunctions.contains(null, null)).isTrue();
        assertThat(KieFunctions.contains("hello", null)).isFalse();
        assertThat(KieFunctions.contains(null, "hello")).isFalse();

        assertThat(KieFunctions.startsWith("welcome to jamaica", "wel")).isTrue();
        assertThat(KieFunctions.startsWith("welcome to jamaica", "Well")).isFalse();
        assertThat(KieFunctions.startsWith(null, null)).isTrue();
        assertThat(KieFunctions.startsWith("hello", null)).isFalse();
        assertThat(KieFunctions.startsWith(null, "hello")).isFalse();

        assertThat(KieFunctions.endsWith("welcome to jamaica", "jamaica")).isTrue();
        assertThat(KieFunctions.endsWith("welcome to jamaica", "Jamaica")).isFalse();
        assertThat(KieFunctions.endsWith(null, null)).isTrue();
        assertThat(KieFunctions.endsWith("hello", null)).isFalse();
        assertThat(KieFunctions.endsWith(null, "hello")).isFalse();


        assertThat(KieFunctions.greaterThan(5, "2")).isTrue();
        assertThat(KieFunctions.greaterThan(0, "2")).isFalse();
        assertThat(KieFunctions.greaterThan(0, "0")).isFalse();
        assertThat(KieFunctions.greaterThan(null, "0")).isFalse();
        assertThat(KieFunctions.greaterThan(null, null)).isFalse();

        comparitionFailed = false;
        try {
            assertThat(KieFunctions.greaterThan(2, null)).isFalse();
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        assertThat(comparitionFailed).isTrue();


        assertThat(KieFunctions.greaterOrEqualThan(5, "2")).isTrue();
        assertThat(KieFunctions.greaterOrEqualThan(2, "2")).isTrue();
        assertThat(KieFunctions.greaterOrEqualThan(0, "2")).isFalse();
        assertThat(KieFunctions.greaterOrEqualThan(0, "0")).isTrue();
        assertThat(KieFunctions.greaterOrEqualThan(null, "0")).isFalse();
        assertThat(KieFunctions.greaterOrEqualThan(null, null)).isFalse();

        comparitionFailed = false;
        try {
            assertThat(KieFunctions.greaterOrEqualThan(2, null)).isFalse();
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        assertThat(comparitionFailed).isTrue();


        assertThat(KieFunctions.lessThan(5, "2")).isFalse();
        assertThat(KieFunctions.lessThan(0, "2")).isTrue();
        assertThat(KieFunctions.lessThan(0, "0")).isFalse();
        assertThat(KieFunctions.lessThan(null, "0")).isFalse();
        assertThat(KieFunctions.lessThan(null, null)).isFalse();

        comparitionFailed = false;
        try {
            assertThat(KieFunctions.lessThan(2, null)).isFalse();
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        assertThat(comparitionFailed).isTrue();


        assertThat(KieFunctions.lessOrEqualThan(5, "2")).isFalse();
        assertThat(KieFunctions.lessOrEqualThan(2, "2")).isTrue();
        assertThat(KieFunctions.lessOrEqualThan(0, "2")).isTrue();
        assertThat(KieFunctions.lessOrEqualThan(0, "0")).isTrue();
        assertThat(KieFunctions.lessOrEqualThan(null, "0")).isFalse();
        assertThat(KieFunctions.lessOrEqualThan(null, null)).isFalse();

        comparitionFailed = false;
        try {
            assertThat(KieFunctions.lessOrEqualThan(2, null)).isFalse();
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        assertThat(comparitionFailed).isTrue();


        assertThat(KieFunctions.between(0, "1", "10")).isFalse();
        assertThat(KieFunctions.between(11, "1", "10")).isFalse();
        assertThat(KieFunctions.between(1, "1", "10")).isTrue();
        assertThat(KieFunctions.between(10, "1", "10")).isTrue();
        assertThat(KieFunctions.between(2, "1", "10")).isTrue();
        assertThat(KieFunctions.between(null, "5", "6")).isFalse();

        comparitionFailed = false;
        try {
            assertThat(KieFunctions.between(2, null, "9")).isFalse();
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        assertThat(comparitionFailed).isTrue();

        comparitionFailed = false;
        try {
            assertThat(KieFunctions.between(2, "1", null)).isFalse();
        } catch (RuntimeException e) {
            comparitionFailed = true;
        }
        assertThat(comparitionFailed).isTrue();


        assertThat(KieFunctions.isTrue(true)).isTrue();
        assertThat(KieFunctions.isTrue(null)).isFalse();
        assertThat(KieFunctions.isTrue(false)).isFalse();

        assertThat(KieFunctions.isFalse(false)).isTrue();
        assertThat(KieFunctions.isFalse(null)).isFalse();
        assertThat(KieFunctions.isFalse(true)).isFalse();

    }
}
