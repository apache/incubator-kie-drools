/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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