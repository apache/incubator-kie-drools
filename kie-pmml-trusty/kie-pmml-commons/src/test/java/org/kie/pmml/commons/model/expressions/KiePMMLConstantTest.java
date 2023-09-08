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

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.commons.model.ProcessingDTO;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLConstantTest {

    @Test
    void evaluate() {
        Object value = 234.45;
        final KiePMMLConstant kiePMMLConstant1 = new KiePMMLConstant("NAME", Collections.emptyList(), value, null);
        ProcessingDTO processingDTO = new ProcessingDTO(Collections.emptyList(), Collections.emptyList(),
                                                        Collections.emptyList(), Collections.emptyList(),
                                                        Collections.emptyList(), Collections.emptyList(),
                                                        Collections.emptyList());
        Object retrieved = kiePMMLConstant1.evaluate(processingDTO);
        assertThat(retrieved).isEqualTo(value);
        final KiePMMLConstant kiePMMLConstant2 = new KiePMMLConstant("NAME", Collections.emptyList(), value,
                                                                     DATA_TYPE.STRING);
        processingDTO = new ProcessingDTO(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                                          Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                                          Collections.emptyList());
        retrieved = kiePMMLConstant2.evaluate(processingDTO);
        assertThat(retrieved).isEqualTo("234.45");
    }
}