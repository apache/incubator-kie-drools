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
package org.kie.efesto.runtimemanager.core.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoInputA;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoInputB;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoInputC;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoOutput;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RuntimeManagerUtilsTest {

    private static EfestoRuntimeContext context;

    private static final List<Class<? extends EfestoInput>> MANAGED_Efesto_INPUTS =
            Arrays.asList(MockEfestoInputA.class,
                          MockEfestoInputB.class,
                          MockEfestoInputC.class);

    private static KieRuntimeService kieRuntimeServiceA;
    private static KieRuntimeService kieRuntimeServiceB;
    private static KieRuntimeService kieRuntimeServiceC;

    private static KieRuntimeService kieRuntimeServiceA_cloned;

    private static EfestoClassKey efestoClassKeyA;
    private static EfestoClassKey efestoClassKeyB;
    private static EfestoClassKey efestoClassKeyC;

    private static ModelLocalUriId modelLocalUri;

    private static KieRuntimeService baseInputService;

    private static KieRuntimeService baseInputExtenderService;

    @BeforeAll
    static void setUp() {
        context = EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        kieRuntimeServiceA = mock(KieRuntimeService.class);
        efestoClassKeyA = new EfestoClassKey(String.class);
        when(kieRuntimeServiceA.getEfestoClassKeyIdentifier()).thenReturn(efestoClassKeyA);
        kieRuntimeServiceB = mock(KieRuntimeService.class);
        efestoClassKeyB = new EfestoClassKey(String.class);
        when(kieRuntimeServiceB.getEfestoClassKeyIdentifier()).thenReturn(efestoClassKeyB);
        kieRuntimeServiceC = mock(KieRuntimeService.class);
        efestoClassKeyC = new EfestoClassKey(List.class, String.class);
        when(kieRuntimeServiceC.getEfestoClassKeyIdentifier()).thenReturn(efestoClassKeyC);
        kieRuntimeServiceA_cloned = mock(KieRuntimeService.class);
        when(kieRuntimeServiceA_cloned.getEfestoClassKeyIdentifier()).thenReturn(efestoClassKeyA);

        // setup
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        modelLocalUri = new ModelLocalUriId(parsed);

        baseInputService = new BaseInputService();
        baseInputExtenderService = new BaseInputExtenderService();
    }

    @Test
    void populateFirstLevelCache() {
        List<KieRuntimeService> discoveredKieRuntimeServices = Arrays.asList(kieRuntimeServiceA, kieRuntimeServiceB,
                                                                             kieRuntimeServiceC,
                                                                             kieRuntimeServiceA_cloned);
        final Map<EfestoClassKey, List<KieRuntimeService>> toPopulate = new HashMap<>();
        RuntimeManagerUtils.populateFirstLevelCache(discoveredKieRuntimeServices, toPopulate);
        assertThat(toPopulate).hasSize(2);
        assertThat(toPopulate).containsKeys(efestoClassKeyA, efestoClassKeyB, efestoClassKeyC); // efestoClassKeyA and efestoClassKeyB  are equals
        List<KieRuntimeService> servicesA = toPopulate.get(efestoClassKeyA);
        List<KieRuntimeService> servicesB = toPopulate.get(efestoClassKeyB);
        assertThat(servicesA).isEqualTo(servicesB);
        assertThat(servicesA).hasSize(3);
        assertThat(servicesA).contains(kieRuntimeServiceA, kieRuntimeServiceB, kieRuntimeServiceA_cloned);
        List<KieRuntimeService> servicesC = toPopulate.get(efestoClassKeyC);
        assertThat(servicesC).containsExactly(kieRuntimeServiceC);
    }

    @Test
    void addKieRuntimeServiceToFirstLevelCache() {
        List<KieRuntimeService> discoveredKieRuntimeServices = Collections.singletonList(kieRuntimeServiceA);
        final Map<EfestoClassKey, List<KieRuntimeService>> toPopulate = new HashMap<>();
        RuntimeManagerUtils.populateFirstLevelCache(discoveredKieRuntimeServices, toPopulate);
        assertThat(toPopulate).hasSize(1);
        assertThat(toPopulate).containsKeys(efestoClassKeyA);
        List<KieRuntimeService> servicesA = toPopulate.get(efestoClassKeyA);
        assertThat(servicesA).containsExactly(kieRuntimeServiceA);

        RuntimeManagerUtils.firstLevelCache.putAll(toPopulate);
        RuntimeManagerUtils.addKieRuntimeServiceToFirstLevelCache(kieRuntimeServiceA_cloned, efestoClassKeyA);
        servicesA = RuntimeManagerUtils.firstLevelCache.get(efestoClassKeyA);
        assertThat(servicesA).containsExactly(kieRuntimeServiceA, kieRuntimeServiceA_cloned);

    }

    @Test
    void getKieRuntimeServiceFromSecondLevelCacheBaseClassEvaluatedBeforeChild() {
        RuntimeManagerUtils.secondLevelCache.clear();
        RuntimeManagerUtils.firstLevelCache.clear();
        RuntimeManagerUtils.firstLevelCache.put(baseInputService.getEfestoClassKeyIdentifier(),
                                                Collections.singletonList(baseInputService));
        RuntimeManagerUtils.firstLevelCache.put(baseInputExtenderService.getEfestoClassKeyIdentifier(),
                                                Collections.singletonList(baseInputExtenderService));

        EfestoInput baseEfestoInput = new BaseEfestoInput(modelLocalUri, "One");
        EfestoInput baseEfestoInputExtender = new BaseEfestoInputExtender(modelLocalUri, "One");
        assertThat(RuntimeManagerUtils.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInput)).isNull();
        assertThat(RuntimeManagerUtils.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInputExtender)).isNull();

        Optional<KieRuntimeService> retrieved = RuntimeManagerUtils.getKieRuntimeServiceLocal(context, baseEfestoInput);
        assertThat(retrieved).isPresent();
        assertThat(retrieved).get().isEqualTo(baseInputService);
        // verify second-level cache population
        assertThat(RuntimeManagerUtils.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInput)).isEqualTo(baseInputService);
        assertThat(RuntimeManagerUtils.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInputExtender)).isNull();

        retrieved = RuntimeManagerUtils.getKieRuntimeServiceLocal(context, baseEfestoInputExtender);
        assertThat(retrieved).isPresent();
        assertThat(retrieved).get().isEqualTo(baseInputExtenderService);
        // verify second-level cache population
        assertThat(RuntimeManagerUtils.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInput)).isEqualTo(baseInputService);
        assertThat(RuntimeManagerUtils.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInputExtender)).isEqualTo(baseInputExtenderService);
    }

    @Test
    void getKieRuntimeServiceFromSecondLevelCacheChildClassEvaluatedBeforeParent() {
        RuntimeManagerUtils.secondLevelCache.clear();
        RuntimeManagerUtils.firstLevelCache.clear();
        RuntimeManagerUtils.firstLevelCache.put(baseInputService.getEfestoClassKeyIdentifier(),
                                                Collections.singletonList(baseInputService));
        RuntimeManagerUtils.firstLevelCache.put(baseInputExtenderService.getEfestoClassKeyIdentifier(),
                                                Collections.singletonList(baseInputExtenderService));

        EfestoInput baseEfestoInput = new BaseEfestoInput(modelLocalUri, "One");
        EfestoInput baseEfestoInputExtender = new BaseEfestoInputExtender(modelLocalUri, "One");
        assertThat(RuntimeManagerUtils.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInput)).isNull();
        Optional<KieRuntimeService> retrieved = RuntimeManagerUtils.getKieRuntimeServiceLocal(context, baseEfestoInputExtender);
        assertThat(retrieved).isPresent();
        assertThat(retrieved).get().isEqualTo(baseInputExtenderService);
        // verify second-level cache population
        assertThat(RuntimeManagerUtils.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInput)).isNull();
        assertThat(RuntimeManagerUtils.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInputExtender)).isEqualTo(baseInputExtenderService);

        retrieved = RuntimeManagerUtils.getKieRuntimeServiceLocal(context, baseEfestoInput);
        assertThat(retrieved).isPresent();
        assertThat(retrieved).get().isEqualTo(baseInputService);
        // verify second-level cache population
        assertThat(RuntimeManagerUtils.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInput)).isEqualTo(baseInputService);
        assertThat(RuntimeManagerUtils.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInputExtender)).isEqualTo(baseInputExtenderService);
    }

    @Test
    void getKieRuntimeServiceLocalPresent() {
        MANAGED_Efesto_INPUTS.forEach(managedInput -> {
            try {
                EfestoInput efestoInput = managedInput.getDeclaredConstructor().newInstance();
                Optional<KieRuntimeService> retrieved = RuntimeManagerUtils.getKieRuntimeServiceLocal(context,
                                                                                                      efestoInput);
                assertThat(retrieved).isNotNull().isPresent();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void getKieRuntimeServiceLocalNotPresent() {
        EfestoInput efestoInput = new EfestoInput() {
            @Override
            public ModelLocalUriId getModelLocalUriId() {
                return new ModelLocalUriId(LocalUri.parse("/not-existing/notexisting"));
            }

            @Override
            public Object getInputData() {
                return null;
            }
        };
        Optional<KieRuntimeService> retrieved = RuntimeManagerUtils.getKieRuntimeServiceLocal(context, efestoInput);
        assertThat(retrieved).isNotNull().isNotPresent();
    }

    static class BaseEfestoInputExtender extends BaseEfestoInput<String> {

        public BaseEfestoInputExtender(ModelLocalUriId modelLocalUriId, String inputData) {
            super(modelLocalUriId, inputData);
        }
    }

    static class BaseInputService implements KieRuntimeService<String, String, BaseEfestoInput<String>,
            MockEfestoOutput, EfestoRuntimeContext> {

        @Override
        public EfestoClassKey getEfestoClassKeyIdentifier() {
            // This should always return an unmatchable key
            return new EfestoClassKey(BaseEfestoInput.class, String.class);
        }

        @Override
        public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
            return toEvaluate instanceof BaseEfestoInput;
        }

        @Override
        public Optional<MockEfestoOutput> evaluateInput(BaseEfestoInput toEvaluate, EfestoRuntimeContext context) {
            return Optional.empty();
        }

        @Override
        public String getModelType() {
            return "BaseEfestoInput";
        }
    }

    static class BaseInputExtenderService implements KieRuntimeService<String, String, BaseEfestoInputExtender,
            MockEfestoOutput, EfestoRuntimeContext> {

        @Override
        public EfestoClassKey getEfestoClassKeyIdentifier() {
            // THis should always return an unmatchable key
            return new EfestoClassKey(BaseEfestoInputExtender.class, String.class);
        }

        @Override
        public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
            return toEvaluate instanceof BaseEfestoInputExtender;
        }

        @Override
        public Optional<MockEfestoOutput> evaluateInput(BaseEfestoInputExtender toEvaluate,
                                                        EfestoRuntimeContext context) {
            return Optional.empty();
        }

        @Override
        public String getModelType() {
            return "BaseEfestoInputExtender";
        }
    }
}