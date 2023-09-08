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
package org.kie.efesto.compilationmanager.core.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputA;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputB;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputC;
import org.kie.efesto.compilationmanager.core.mocks.MockEfestoRedirectOutputD;
import org.kie.efesto.compilationmanager.core.mocks.MockKieCompilerServiceAB;
import org.kie.efesto.compilationmanager.core.mocks.MockKieCompilerServiceC;
import org.kie.efesto.compilationmanager.core.mocks.MockKieCompilerServiceE;

import static org.assertj.core.api.Assertions.assertThat;

class TestSPIUtils {

    private static final List<Class<? extends KieCompilerService>> KIE_COMPILER_SERVICES = Arrays.asList(MockKieCompilerServiceAB.class, MockKieCompilerServiceC.class, MockKieCompilerServiceE.class);

    @Test
    void getKieCompilerService() {
        Optional<KieCompilerService> retrieved = SPIUtils.getKieCompilerService(new MockEfestoRedirectOutputA(), false);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get() instanceof MockKieCompilerServiceAB).isTrue();
        retrieved = SPIUtils.getKieCompilerService(new MockEfestoRedirectOutputB(), false);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get() instanceof MockKieCompilerServiceAB).isTrue();
        retrieved = SPIUtils.getKieCompilerService(new MockEfestoRedirectOutputC(), false);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get() instanceof MockKieCompilerServiceC).isTrue();
        retrieved = SPIUtils.getKieCompilerService(new MockEfestoRedirectOutputD(), false);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void getKieCompilerServices() {
        List<KieCompilerService> retrieved = SPIUtils.getKieCompilerServices(false);
        assertThat(retrieved).isNotNull().hasSize(KIE_COMPILER_SERVICES.size());
    }
}