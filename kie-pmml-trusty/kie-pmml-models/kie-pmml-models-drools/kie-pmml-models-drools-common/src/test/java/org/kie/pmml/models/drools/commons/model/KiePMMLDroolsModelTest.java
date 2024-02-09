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
package org.kie.pmml.models.drools.commons.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.KiePMMLExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;

public class KiePMMLDroolsModelTest {

    private final static String MODEL_NAME = "MODELNAME";
    private final static String KMODULE_PACKAGE_NAME = getSanitizedPackageName(MODEL_NAME);
    private final static List<KiePMMLExtension> EXTENSIONS = new ArrayList<>();
    private KiePMMLDroolsModel kiePMMLDroolsModel;

    @BeforeEach
    public void setup() {
        kiePMMLDroolsModel = new KiePMMLDroolsModelFake(MODEL_NAME, KMODULE_PACKAGE_NAME, EXTENSIONS);
    }

    @Test
    void constructor() {
        assertThat(kiePMMLDroolsModel.getName()).isEqualTo(MODEL_NAME);
        assertThat(kiePMMLDroolsModel.getExtensions()).isEqualTo(EXTENSIONS);
        assertThat(kiePMMLDroolsModel.getKModulePackageName()).isEqualTo(getSanitizedPackageName(MODEL_NAME));
    }


    private final class KiePMMLDroolsModelFake extends KiePMMLDroolsModel {

        protected KiePMMLDroolsModelFake(String modelName,
                                         String kModulePackageName,
                                         List<KiePMMLExtension> extensions) {
            super("FILENAME", modelName, extensions);
            this.kModulePackageName = kModulePackageName;
        }

        @Override
        public String getKModulePackageName() {
            return super.getKModulePackageName();
        }
    }
}