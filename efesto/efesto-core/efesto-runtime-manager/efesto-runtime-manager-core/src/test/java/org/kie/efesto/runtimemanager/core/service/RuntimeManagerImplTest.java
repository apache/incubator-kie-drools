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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.mocks.MockEfestoInputA;
import org.kie.efesto.runtimemanager.api.mocks.MockEfestoInputB;
import org.kie.efesto.runtimemanager.api.mocks.MockEfestoInputC;
import org.kie.efesto.runtimemanager.api.mocks.MockEfestoInputD;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RuntimeManagerImplTest {

    private static RuntimeManager runtimeManager;
    private static EfestoRuntimeContext context;

    private static final List<Class<? extends EfestoInput>> MANAGED_Efesto_INPUTS =
            Arrays.asList(MockEfestoInputA.class,
                          MockEfestoInputB.class,
                          MockEfestoInputC.class);

    @BeforeAll
    static void setUp() {
        runtimeManager = new RuntimeManagerImpl();
        context = EfestoRuntimeContext.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void populateFirstLevelCache() {
        KieRuntimeService kieRuntimeServiceA = mock(KieRuntimeService.class);
        EfestoClassKey efestoClassKeyA = new EfestoClassKey(String.class);
        when(kieRuntimeServiceA.getEfestoClassKeyIdentifier()).thenReturn(efestoClassKeyA);
        KieRuntimeService kieRuntimeServiceB = mock(KieRuntimeService.class);
        EfestoClassKey efestoClassKeyB = new EfestoClassKey(String.class);
        when(kieRuntimeServiceB.getEfestoClassKeyIdentifier()).thenReturn(efestoClassKeyB);
        KieRuntimeService kieRuntimeServiceC = mock(KieRuntimeService.class);
        EfestoClassKey efestoClassKeyC = new EfestoClassKey(List.class, String.class);
        when(kieRuntimeServiceC.getEfestoClassKeyIdentifier()).thenReturn(efestoClassKeyC);
        List<KieRuntimeService> discoveredKieRuntimeServices = Arrays.asList(kieRuntimeServiceA, kieRuntimeServiceB, kieRuntimeServiceC);
        final Map<EfestoClassKey, List<KieRuntimeService>> toPopulate = new HashMap<>();
        ((RuntimeManagerImpl)runtimeManager).populateFirstLevelCache(discoveredKieRuntimeServices, toPopulate);
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
}