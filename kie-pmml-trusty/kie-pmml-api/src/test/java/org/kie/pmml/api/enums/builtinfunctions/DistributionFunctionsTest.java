/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.api.enums.builtinfunctions;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DistributionFunctionsTest {

    public final static List<DistributionFunctions> supportedDistributionFunctions;
    public final static List<DistributionFunctions> unsupportedDistributionFunctions;

    static {
        supportedDistributionFunctions = new ArrayList<>();
        supportedDistributionFunctions.add(DistributionFunctions.NORMAL_CDF);
        supportedDistributionFunctions.add(DistributionFunctions.NORMAL_PDF);
        supportedDistributionFunctions.add(DistributionFunctions.STD_NORMAL_CDF);
        supportedDistributionFunctions.add(DistributionFunctions.STD_NORMAL_PDF);
        supportedDistributionFunctions.add(DistributionFunctions.ERF);
        supportedDistributionFunctions.add(DistributionFunctions.NORMAL_IDF);
        supportedDistributionFunctions.add(DistributionFunctions.STD_NORMAL_IDF);

        unsupportedDistributionFunctions = new ArrayList<>();
    }

    @Test
    public void getNormalCDFValueCorrectInput() {
        Object[] input1 = {24.11, 24.54, 1.23};
        Object retrieved = DistributionFunctions.NORMAL_CDF.getValue(input1);
        assertThat((Double) retrieved).isCloseTo(0.363, Offset.offset(0.001));
        Object[] input2 = {9.12, 11.35, 3.78};
        retrieved = DistributionFunctions.NORMAL_CDF.getValue(input2);
        assertThat((Double) retrieved).isCloseTo(0.278, Offset.offset(0.001));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNormalCDFValueWrongSizeInput() {
        final Object[] input = {34};
        DistributionFunctions.NORMAL_CDF.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNormalCDFValueWrongTypeInput() {
        final Object[] input = {34, 25.3, "22.1"};
        DistributionFunctions.NORMAL_CDF.getValue(input);
    }

    @Test
    public void getNormalPDFValueCorrectInput() {
        Object[] input1 = {2, 7, 2};
        Object retrieved = DistributionFunctions.NORMAL_PDF.getValue(input1);
        assertThat((Double) retrieved).isCloseTo(0.00876, Offset.offset(0.001));
        Object[] input2 = {9.12, 11.35, 3.78};
        retrieved = DistributionFunctions.NORMAL_PDF.getValue(input2);
        assertThat((Double) retrieved).isCloseTo(0.08868, Offset.offset(0.001));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNormalPDFValueWrongSizeInput() {
        final Object[] input = {34};
        DistributionFunctions.NORMAL_PDF.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNormalPDFValueWrongTypeInput() {
        final Object[] input = {34, 25.3, "22.1"};
        DistributionFunctions.NORMAL_PDF.getValue(input);
    }

    @Test
    public void getStdNormalCDFValueCorrectInput() {
        Object[] input1 = {2};
        Object retrieved = DistributionFunctions.STD_NORMAL_CDF.getValue(input1);
        assertThat((Double) retrieved).isCloseTo(0.97725, Offset.offset(0.001));
        Object[] input2 = {1.243};
        retrieved = DistributionFunctions.STD_NORMAL_CDF.getValue(input2);
        assertThat((Double) retrieved).isCloseTo(0.89307, Offset.offset(0.001));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getStdNormalCDFValueWrongSizeInput() {
        final Object[] input = {34, 312, 11};
        DistributionFunctions.STD_NORMAL_CDF.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getStdNormalCDFValueWrongTypeInput() {
        final Object[] input = {"34"};
        DistributionFunctions.STD_NORMAL_CDF.getValue(input);
    }

    @Test
    public void getStdNormalPDFValueCorrectInput() {
        Object[] input1 = {2};
        Object retrieved = DistributionFunctions.STD_NORMAL_PDF.getValue(input1);
        assertThat((Double) retrieved).isCloseTo(0.05399, Offset.offset(0.001));
        Object[] input2 = {1.243};
        retrieved = DistributionFunctions.STD_NORMAL_PDF.getValue(input2);
        assertThat((Double) retrieved).isCloseTo(0.18425, Offset.offset(0.001));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getStdNormalPDFValueWrongSizeInput() {
        final Object[] input = {34, 312, 11};
        DistributionFunctions.STD_NORMAL_PDF.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getStdNormalPDFValueWrongTypeInput() {
        final Object[] input = {"34"};
        DistributionFunctions.STD_NORMAL_PDF.getValue(input);
    }

    @Test
    public void getErfValueCorrectInput() {
        Object[] input1 = {2};
        Object retrieved = DistributionFunctions.ERF.getValue(input1);
        assertThat((Double) retrieved).isCloseTo(0.9953223, Offset.offset(0.001));
        Object[] input2 = {1.243};
        retrieved = DistributionFunctions.ERF.getValue(input2);
        assertThat((Double) retrieved).isCloseTo(0.92123, Offset.offset(0.001));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getErfValueWrongSizeInput() {
        final Object[] input = {34, 312, 11};
        DistributionFunctions.ERF.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getErfValueWrongTypeInput() {
        final Object[] input = {"34"};
        DistributionFunctions.ERF.getValue(input);
    }

    @Test
    public void getNormalIDFValueCorrectInput() {
        Object[] input1 = {0.75, 1.23, 0.2};
        Object retrieved = DistributionFunctions.NORMAL_IDF.getValue(input1);
        assertThat((Double) retrieved).isCloseTo(1.36, Offset.offset(0.01));
        Object[] input2 = {0.912, 11.35, 3.78};
        retrieved = DistributionFunctions.NORMAL_IDF.getValue(input2);
        assertThat((Double) retrieved).isCloseTo(16.46, Offset.offset(0.01));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNormalIDFValueWrongSizeInput() {
        final Object[] input = {34};
        DistributionFunctions.NORMAL_IDF.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNormalIDFValueWrongTypeInput() {
        final Object[] input = {34, 25.3, "22.1"};
        DistributionFunctions.NORMAL_IDF.getValue(input);
    }

    @Test
    public void getStdNormalIDFValueCorrectInput() {
        Object[] input1 = {0.75};
        Object retrieved = DistributionFunctions.STD_NORMAL_IDF.getValue(input1);
        assertThat((Double) retrieved).isCloseTo(0.67, Offset.offset(0.01));
        Object[] input2 = {0.912};
        retrieved = DistributionFunctions.STD_NORMAL_IDF.getValue(input2);
        assertThat((Double) retrieved).isCloseTo(1.35, Offset.offset(0.01));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getStdNormalIDFValueWrongSizeInput() {
        final Object[] input = {34, 312, 11};
        DistributionFunctions.STD_NORMAL_IDF.getValue(input);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getStdNormalIDFValueWrongTypeInput() {
        final Object[] input = {"34"};
        DistributionFunctions.STD_NORMAL_IDF.getValue(input);
    }


}