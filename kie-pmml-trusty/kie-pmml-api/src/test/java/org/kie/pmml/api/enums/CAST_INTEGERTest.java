package org.kie.pmml.api.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CAST_INTEGERTest {

    @Test
    void getRound() {
        int retrieved = CAST_INTEGER.getRound(2.718);
        assertThat(retrieved).isEqualTo(3);
        retrieved = CAST_INTEGER.getRound(-2.718);
        assertThat(retrieved).isEqualTo(-3);
        retrieved = CAST_INTEGER.getRound(2.418);
        assertThat(retrieved).isEqualTo(2);
        retrieved = CAST_INTEGER.getRound(-2.418);
        assertThat(retrieved).isEqualTo(-2);
    }

    @Test
    void getCeiling() {
        int retrieved = CAST_INTEGER.getCeiling(2.718);
        assertThat(retrieved).isEqualTo(3);
        retrieved = CAST_INTEGER.getCeiling(-2.718);
        assertThat(retrieved).isEqualTo(-2);
        retrieved = CAST_INTEGER.getCeiling(2.418);
        assertThat(retrieved).isEqualTo(3);
        retrieved = CAST_INTEGER.getCeiling(-2.418);
        assertThat(retrieved).isEqualTo(-2);
    }

    @Test
    void getFloor() {
        int retrieved = CAST_INTEGER.getFloor(2.718);
        assertThat(retrieved).isEqualTo(2);
        retrieved = CAST_INTEGER.getFloor(-2.718);
        assertThat(retrieved).isEqualTo(-3);
        retrieved = CAST_INTEGER.getFloor(2.418);
        assertThat(retrieved).isEqualTo(2);
        retrieved = CAST_INTEGER.getFloor(-2.418);
        assertThat(retrieved).isEqualTo(-3);
    }
}