package org.kie.efesto.runtimemanager.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoInputA;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoInputB;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoInputC;
import org.kie.efesto.runtimemanager.core.mocks.MockEfestoInputD;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class RuntimeManagerImplTest {

    private static RuntimeManagerImpl runtimeManager;
    private static EfestoRuntimeContext context;

    private static final List<Class<? extends EfestoInput>> MANAGED_Efesto_INPUTS =
            Arrays.asList(MockEfestoInputA.class,
                          MockEfestoInputB.class,
                          MockEfestoInputC.class);

    @BeforeAll
    static void setUp() {
        runtimeManager = new RuntimeManagerImpl();
        context = EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
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

}