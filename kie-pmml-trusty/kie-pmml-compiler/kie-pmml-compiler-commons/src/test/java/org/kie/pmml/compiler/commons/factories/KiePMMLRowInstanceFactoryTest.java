/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.Row;
import org.junit.Test;
import org.kie.pmml.commons.model.expressions.KiePMMLRow;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomRow;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomRowWithCells;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLRow;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLRowWithCells;

public class KiePMMLRowInstanceFactoryTest {

    @Test
    public void getKiePMMLRow() {
        Row toConvert = getRandomRow();
        KiePMMLRow retrieved = KiePMMLRowInstanceFactory.getKiePMMLRow(toConvert);
        commonVerifyKiePMMLRow(retrieved, toConvert);
        toConvert = getRandomRowWithCells();
        retrieved = KiePMMLRowInstanceFactory.getKiePMMLRow(toConvert);
        commonVerifyKiePMMLRowWithCells(retrieved, toConvert);
    }
}