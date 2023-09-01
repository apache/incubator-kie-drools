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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.CLOSURE;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.CommonTestingUtility.getProcessingDTO;

public class KiePMMLDiscretizeTest {

    private static final String NAME = "name";
    private static final String MAP_MISSING_TO = "mapMissingTo";
    private static final String DEFAULTVALUE = "defaultValue";
    private static final DATA_TYPE DATATYPE = DATA_TYPE.INTEGER;

    private static KiePMMLDiscretizeBin kiePMMLDiscretizeBin1;
    private static KiePMMLDiscretizeBin kiePMMLDiscretizeBin2;
    private static KiePMMLDiscretizeBin kiePMMLDiscretizeBin3;
    private static KiePMMLDiscretizeBin kiePMMLDiscretizeBin4;
    private static KiePMMLDiscretizeBin kiePMMLDiscretizeBin5;
    private static List<KiePMMLDiscretizeBin> discretizeBins;

    @BeforeAll
    public static void setup() {
        kiePMMLDiscretizeBin1 = getKiePMMLDiscretizeBin("kiePMMLDiscretizeBin1", new KiePMMLInterval(null, 20,
                                                                                                     CLOSURE.OPEN_OPEN), "kiePMMLDiscretizeBin1");
        kiePMMLDiscretizeBin2 = getKiePMMLDiscretizeBin("kiePMMLDiscretizeBin2", new KiePMMLInterval(21, 30,
                                                                                                     CLOSURE.OPEN_CLOSED), "kiePMMLDiscretizeBin2");
        kiePMMLDiscretizeBin3 = getKiePMMLDiscretizeBin("kiePMMLDiscretizeBin3", new KiePMMLInterval(31, 40,
                                                                                                     CLOSURE.CLOSED_OPEN), "kiePMMLDiscretizeBin3");
        kiePMMLDiscretizeBin4 = getKiePMMLDiscretizeBin("kiePMMLDiscretizeBin4", new KiePMMLInterval(41, 50,
                                                                                                     CLOSURE.CLOSED_CLOSED), "kiePMMLDiscretizeBin4");
        kiePMMLDiscretizeBin5 = getKiePMMLDiscretizeBin("kiePMMLDiscretizeBin5", new KiePMMLInterval(51, null,
                                                                                                     CLOSURE.CLOSED_CLOSED), "kiePMMLDiscretizeBin5");
        discretizeBins = Arrays.asList(kiePMMLDiscretizeBin1, kiePMMLDiscretizeBin2,
                                       kiePMMLDiscretizeBin3, kiePMMLDiscretizeBin4,
                                       kiePMMLDiscretizeBin5);
    }

    @Test
    void evaluateNoInput() {
        KiePMMLDiscretize kiePMMLDiscretize = getKiePMMLDiscretize(null, null);
        ProcessingDTO processingDTO = getProcessingDTO(Collections.emptyList());
        Object retrieved = kiePMMLDiscretize.evaluate(processingDTO);
        assertThat(retrieved).isNull();
        kiePMMLDiscretize = getKiePMMLDiscretize(MAP_MISSING_TO, null);
        retrieved = kiePMMLDiscretize.evaluate(processingDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(MAP_MISSING_TO);
    }

    @Test
    void evaluateDefaultValue() {
        KiePMMLDiscretize kiePMMLDiscretize = getKiePMMLDiscretize(null, null);

        ProcessingDTO processingDTO = getProcessingDTO(List.of(new KiePMMLNameValue(NAME, 20)));
        Object retrieved = kiePMMLDiscretize.evaluate(processingDTO);
        assertThat(retrieved).isNull();
        kiePMMLDiscretize = getKiePMMLDiscretize(MAP_MISSING_TO, DEFAULTVALUE);
        processingDTO = getProcessingDTO(List.of(new KiePMMLNameValue(NAME, 20)));
        retrieved = kiePMMLDiscretize.evaluate(processingDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(DEFAULTVALUE);
        processingDTO = getProcessingDTO(List.of(new KiePMMLNameValue(NAME, 21)));
        retrieved = kiePMMLDiscretize.evaluate(processingDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(DEFAULTVALUE);
        processingDTO = getProcessingDTO(List.of(new KiePMMLNameValue(NAME, 40)));
        retrieved = kiePMMLDiscretize.evaluate(processingDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEqualTo(DEFAULTVALUE);
    }

    @Test
    void getFromDiscretizeBins() {
        KiePMMLDiscretize kiePMMLDiscretize = getKiePMMLDiscretize(null, null);
        Optional<String> retrieved = kiePMMLDiscretize.getFromDiscretizeBins(10);
        assertThat(retrieved).isPresent();
        retrieved = kiePMMLDiscretize.getFromDiscretizeBins(20);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretize.getFromDiscretizeBins(21);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretize.getFromDiscretizeBins(29);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(kiePMMLDiscretizeBin2.getBinValue());
        retrieved = kiePMMLDiscretize.getFromDiscretizeBins(30);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(kiePMMLDiscretizeBin2.getBinValue());
        retrieved = kiePMMLDiscretize.getFromDiscretizeBins(31);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(kiePMMLDiscretizeBin3.getBinValue());
        retrieved = kiePMMLDiscretize.getFromDiscretizeBins(32);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(kiePMMLDiscretizeBin3.getBinValue());
        retrieved = kiePMMLDiscretize.getFromDiscretizeBins(40);
        assertThat(retrieved).isNotPresent();
        retrieved = kiePMMLDiscretize.getFromDiscretizeBins(41);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(kiePMMLDiscretizeBin4.getBinValue());
        retrieved = kiePMMLDiscretize.getFromDiscretizeBins(42);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(kiePMMLDiscretizeBin4.getBinValue());
        retrieved = kiePMMLDiscretize.getFromDiscretizeBins(49);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(kiePMMLDiscretizeBin4.getBinValue());
        retrieved = kiePMMLDiscretize.getFromDiscretizeBins(50);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(kiePMMLDiscretizeBin4.getBinValue());
        retrieved = kiePMMLDiscretize.getFromDiscretizeBins(51);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(kiePMMLDiscretizeBin5.getBinValue());
        retrieved = kiePMMLDiscretize.getFromDiscretizeBins(52);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(kiePMMLDiscretizeBin5.getBinValue());
    }

    private KiePMMLDiscretize getKiePMMLDiscretize(String mapMissingTo, String defaultValue) {
        Collections.shuffle(discretizeBins);
        return new KiePMMLDiscretize(NAME, Collections.emptyList(), discretizeBins, mapMissingTo, defaultValue, DATATYPE);
    }

    private static KiePMMLDiscretizeBin getKiePMMLDiscretizeBin(String name, KiePMMLInterval interval, String binValue) {
        return new KiePMMLDiscretizeBin(name, Collections.emptyList(), binValue, interval);
    }

}