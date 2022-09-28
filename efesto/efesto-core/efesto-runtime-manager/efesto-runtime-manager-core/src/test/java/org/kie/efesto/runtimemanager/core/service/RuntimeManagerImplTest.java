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
package org.kie.efesto.runtimemanager.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoInputA;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoInputB;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoInputC;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoInputD;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoOutput;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RuntimeManagerImplTest {

    private static RuntimeManagerImpl runtimeManager;
    private static EfestoRuntimeContext context;

    private static final List<Class<? extends EfestoInput>> MANAGED_Efesto_INPUTS =
            Arrays.asList(MockEfestoInputA.class,
                          MockEfestoInputB.class,
                          MockEfestoInputC.class);

    private static KieRuntimeService kieRuntimeServiceA;
    private static KieRuntimeService kieRuntimeServiceB;
    private static KieRuntimeService kieRuntimeServiceC;

    private static EfestoClassKey efestoClassKeyA;
    private static EfestoClassKey efestoClassKeyB;
    private static EfestoClassKey efestoClassKeyC;

    private static ModelLocalUriId modelLocalUri;

    private static KieRuntimeService baseInputService;

    private static KieRuntimeService baseInputExtenderService;

    @BeforeAll
    static void setUp() {
        runtimeManager = new RuntimeManagerImpl();
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

        // setup
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        modelLocalUri = new ModelLocalUriId(parsed);

        baseInputService = new BaseInputService();
        baseInputExtenderService = new BaseInputExtenderService();
    }

    @Test
    void populateFirstLevelCache() {
        List<KieRuntimeService> discoveredKieRuntimeServices = Arrays.asList(kieRuntimeServiceA, kieRuntimeServiceB, kieRuntimeServiceC);
        final Map<EfestoClassKey, List<KieRuntimeService>> toPopulate = new HashMap<>();
        RuntimeManagerImpl.populateFirstLevelCache(discoveredKieRuntimeServices, toPopulate);
        assertThat(toPopulate.size()).isEqualTo(2);
        assertThat(toPopulate.containsKey(efestoClassKeyA)).isTrue(); // Those two are the same
        assertThat(toPopulate.containsKey(efestoClassKeyB)).isTrue(); // Those two are the same
        assertThat(toPopulate.containsKey(efestoClassKeyC)).isTrue();
        List<KieRuntimeService> servicesA = toPopulate.get(efestoClassKeyA);
        List<KieRuntimeService> servicesB = toPopulate.get(efestoClassKeyB);
        assertThat(servicesA).isEqualTo(servicesB);
        assertThat(servicesA.size()).isEqualTo(2);
        assertThat(servicesA).contains(kieRuntimeServiceA);
        assertThat(servicesA).contains(kieRuntimeServiceB);
        List<KieRuntimeService> servicesC = toPopulate.get(efestoClassKeyC);
        assertThat(servicesC.size()).isEqualTo(1);
        assertThat(servicesC).contains(kieRuntimeServiceC);
    }

    @Test
    void checkBaseClassEvaluatedBeforeChild() {
        RuntimeManagerImpl.secondLevelCache.clear();
        RuntimeManagerImpl.firstLevelCache.clear();
        RuntimeManagerImpl.firstLevelCache.put(baseInputService.getEfestoClassKeyIdentifier(),
                                                                  Collections.singletonList(baseInputService));
        RuntimeManagerImpl.firstLevelCache.put(baseInputExtenderService.getEfestoClassKeyIdentifier(), Collections.singletonList(baseInputExtenderService));

        EfestoInput baseEfestoInput = new BaseEfestoInput(modelLocalUri, "One");
        EfestoInput baseEfestoInputExtender = new BaseEfestoInputExtender(modelLocalUri, "One");
        assertThat(RuntimeManagerImpl.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInput)).isNull();
        assertThat(RuntimeManagerImpl.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInputExtender)).isNull();

        assertThat(RuntimeManagerImpl.getKieRuntimeServiceLocal(context, baseEfestoInput)).isPresent();

        assertThat(RuntimeManagerImpl.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInput)).isEqualTo(baseInputService);
        assertThat(RuntimeManagerImpl.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInputExtender)).isNull();

        RuntimeManagerImpl.getKieRuntimeServiceLocal(context, baseEfestoInputExtender);
        assertThat(RuntimeManagerImpl.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInput)).isEqualTo(baseInputService);
        assertThat(RuntimeManagerImpl.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInputExtender)).isEqualTo(baseInputExtenderService);
    }

    @Test
    void checkChildClassEvaluatedBeforeParent() {
        RuntimeManagerImpl.secondLevelCache.clear();
        RuntimeManagerImpl.firstLevelCache.clear();
        RuntimeManagerImpl.firstLevelCache.put(baseInputService.getEfestoClassKeyIdentifier(),
                                                                  Collections.singletonList(baseInputService));
        RuntimeManagerImpl.firstLevelCache.put(baseInputExtenderService.getEfestoClassKeyIdentifier(), Collections.singletonList(baseInputExtenderService));

        EfestoInput baseEfestoInput = new BaseEfestoInput(modelLocalUri, "One");
        EfestoInput baseEfestoInputExtender = new BaseEfestoInputExtender(modelLocalUri, "One");
        assertThat(RuntimeManagerImpl.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInput)).isNull();
        RuntimeManagerImpl.getKieRuntimeServiceLocal(context, baseEfestoInputExtender);
        assertThat(RuntimeManagerImpl.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInput)).isNull();
        assertThat(RuntimeManagerImpl.getKieRuntimeServiceFromSecondLevelCache(baseEfestoInputExtender)).isEqualTo(baseInputExtenderService);
    }

    @Test
    void evaluateInput() {
        MANAGED_Efesto_INPUTS.forEach(managedInput -> {
            try {
                EfestoInput toProcess = managedInput.getDeclaredConstructor().newInstance();
                Collection<EfestoOutput> retrieved = runtimeManager.evaluateInput(context, toProcess);
                assertThat(retrieved).isNotNull().hasSize(1);
            } catch (Exception e) {
                fail("Failed assertion on evaluateInput", e);
            }
        });
        Collection<EfestoOutput> retrieved = runtimeManager.evaluateInput(context,
                                                                          new MockEfestoInputD());
        assertThat(retrieved).isNotNull().isEmpty();
    }

    @Test
    void evaluateInputs() {
        List<EfestoInput> toProcess = new ArrayList<>();
        MANAGED_Efesto_INPUTS.forEach(managedInput -> {
            try {
                EfestoInput toAdd = managedInput.getDeclaredConstructor().newInstance();
                toProcess.add(toAdd);
            } catch (Exception e) {
                fail("Failed assertion on evaluateInput", e);
            }
        });
        toProcess.add(new MockEfestoInputD());
        Collection<EfestoOutput> retrieved = runtimeManager.evaluateInput(context,
                                                                          toProcess.toArray(new EfestoInput[0]));
        assertThat(retrieved).isNotNull().hasSize(MANAGED_Efesto_INPUTS.size());
    }

    @Test
    void getKieRuntimeServiceLocalPresent() {
        MANAGED_Efesto_INPUTS.forEach(managedInput -> {
            try {
                EfestoInput efestoInput = managedInput.getDeclaredConstructor().newInstance();
                Optional<KieRuntimeService> retrieved = RuntimeManagerImpl.getKieRuntimeServiceLocal(context,
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
        Optional<KieRuntimeService> retrieved = RuntimeManagerImpl.getKieRuntimeServiceLocal(context, efestoInput);
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