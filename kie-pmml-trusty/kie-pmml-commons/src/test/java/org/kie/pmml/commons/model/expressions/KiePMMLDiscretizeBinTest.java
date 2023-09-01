package org.kie.pmml.commons.model.expressions;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.CLOSURE;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLDiscretizeBinTest {

    private static final String NAME = "name";
    private static final String BINVALUE = "binValue";

    @Test
    void evaluateOpenOpen() {
        KiePMMLDiscretizeBin kiePMMLDiscretizeBin = getKiePMMLDiscretizeBin(new KiePMMLInterval(null, 20,
                                                                                                CLOSURE.OPEN_OPEN));
        Optional<String> retrieved = kiePMMLDiscretizeBin.evaluate(10);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(20);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretizeBin.evaluate(30);
        assertThat(retrieved).isNotPresent();
        kiePMMLDiscretizeBin = getKiePMMLDiscretizeBin(new KiePMMLInterval(20, null, CLOSURE.OPEN_OPEN));
        retrieved = kiePMMLDiscretizeBin.evaluate(30);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(20);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretizeBin.evaluate(10);
        assertThat(retrieved).isNotPresent();
        kiePMMLDiscretizeBin = getKiePMMLDiscretizeBin(new KiePMMLInterval(20, 40, CLOSURE.OPEN_OPEN));
        retrieved = kiePMMLDiscretizeBin.evaluate(30);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(10);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretizeBin.evaluate(20);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretizeBin.evaluate(40);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretizeBin.evaluate(50);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void evaluateOpenClosed() {
        KiePMMLDiscretizeBin kiePMMLDiscretizeBin = getKiePMMLDiscretizeBin(new KiePMMLInterval(null, 20,
                                                                                                CLOSURE.OPEN_CLOSED));
        Optional<String> retrieved = kiePMMLDiscretizeBin.evaluate(10);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(20);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(30);
        assertThat(retrieved).isNotPresent();
        kiePMMLDiscretizeBin = getKiePMMLDiscretizeBin(new KiePMMLInterval(20, null, CLOSURE.OPEN_CLOSED));
        retrieved = kiePMMLDiscretizeBin.evaluate(30);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(20);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretizeBin.evaluate(10);
        assertThat(retrieved).isNotPresent();
        kiePMMLDiscretizeBin = getKiePMMLDiscretizeBin(new KiePMMLInterval(20, 40, CLOSURE.OPEN_CLOSED));
        retrieved = kiePMMLDiscretizeBin.evaluate(30);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(10);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretizeBin.evaluate(20);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretizeBin.evaluate(40);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(50);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void evaluateClosedOpen() {
        KiePMMLDiscretizeBin kiePMMLDiscretizeBin = getKiePMMLDiscretizeBin(new KiePMMLInterval(null, 20,
                                                                                                CLOSURE.CLOSED_OPEN));
        Optional<String> retrieved = kiePMMLDiscretizeBin.evaluate(10);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(20);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretizeBin.evaluate(30);
        assertThat(retrieved).isNotPresent();
        kiePMMLDiscretizeBin = getKiePMMLDiscretizeBin(new KiePMMLInterval(20, null, CLOSURE.CLOSED_OPEN));
        retrieved = kiePMMLDiscretizeBin.evaluate(30);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(20);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(10);
        assertThat(retrieved).isNotPresent();
        kiePMMLDiscretizeBin = getKiePMMLDiscretizeBin(new KiePMMLInterval(20, 40, CLOSURE.CLOSED_OPEN));
        retrieved = kiePMMLDiscretizeBin.evaluate(30);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(10);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretizeBin.evaluate(20);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(40);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretizeBin.evaluate(50);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void evaluateClosedClosed() {
        KiePMMLDiscretizeBin kiePMMLDiscretizeBin = getKiePMMLDiscretizeBin(new KiePMMLInterval(null, 20,
                                                                                                CLOSURE.CLOSED_CLOSED));
        Optional<String> retrieved = kiePMMLDiscretizeBin.evaluate(10);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(20);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(30);
        assertThat(retrieved).isNotPresent();
        kiePMMLDiscretizeBin = getKiePMMLDiscretizeBin(new KiePMMLInterval(20, null, CLOSURE.CLOSED_CLOSED));
        retrieved = kiePMMLDiscretizeBin.evaluate(30);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(20);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(10);
        assertThat(retrieved).isNotPresent();
        kiePMMLDiscretizeBin = getKiePMMLDiscretizeBin(new KiePMMLInterval(20, 40, CLOSURE.CLOSED_CLOSED));
        retrieved = kiePMMLDiscretizeBin.evaluate(30);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(10);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretizeBin.evaluate(20);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(40);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(BINVALUE);
        retrieved = kiePMMLDiscretizeBin.evaluate(50);
        assertThat(retrieved).isNotPresent();
    }

    private KiePMMLDiscretizeBin getKiePMMLDiscretizeBin(KiePMMLInterval interval) {
        return new KiePMMLDiscretizeBin(NAME, Collections.emptyList(), BINVALUE, interval);
    }
}