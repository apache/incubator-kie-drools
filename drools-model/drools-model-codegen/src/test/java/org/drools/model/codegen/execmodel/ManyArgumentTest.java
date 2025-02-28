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
package org.drools.model.codegen.execmodel;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class ManyArgumentTest extends BaseModelTest {

    @ParameterizedTest
    @MethodSource("parameters")
    void largeNumberOfBindings(RUN_TYPE runType) throws URISyntaxException, IOException {
        String drl = Files.readString(Path.of(getClass().getResource("large-number-of-bindings.drl").toURI()));

        KieSession ksession = getKieSession(runType, drl);
        List<Integer> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        for (int i = 1; i <= 26; i++) {
            ksession.insert(i);
        }

        ksession.fireAllRules();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(351);
    }
}
