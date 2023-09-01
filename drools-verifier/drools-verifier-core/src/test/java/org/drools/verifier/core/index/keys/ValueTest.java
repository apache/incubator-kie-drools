package org.drools.verifier.core.index.keys;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValueTest {


    @Test
    void testIntegerVSInteger() throws Exception {
        final Value nroZero = new Value(0);
        final Value nroOne = new Value(1);

        assertThat(nroZero).isLessThan(nroOne);
        assertThat(nroOne).isGreaterThan(nroZero);
    }

    @Test
    void testStringVSInteger() throws Exception {
        final Value hello = new Value("hello");
        final Value nroOne = new Value(1);

        assertThat(hello).isGreaterThan(nroOne);
        assertThat(nroOne).isLessThan(hello);
    }

    @Test
    void testStringVSIntegerString() throws Exception {
        final Value hello = new Value("hello");
        final Value nroOne = new Value("1");

        assertThat(hello).isGreaterThan(nroOne);
        assertThat(nroOne).isLessThan(hello);
    }

    @Test
    void testStringVSString() throws Exception {
        final Value a = new Value("a");
        final Value b = new Value("b");

        assertThat(a).isLessThan(b);
        assertThat(b).isGreaterThan(a);
    }
}