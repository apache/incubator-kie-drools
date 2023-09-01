package org.kie.efesto.runtimemanager.core.utils;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.kie.efesto.runtimemanager.core.mocks.MockKieRuntimeServiceA;
import org.kie.efesto.runtimemanager.core.mocks.MockKieRuntimeServiceC;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;

class TestSPIUtils {

    private static final List<Class<? extends KieRuntimeService>> KIE_RUNTIME_SERVICES =
            Arrays.asList(MockKieRuntimeServiceA.class, MockKieRuntimeServiceC.class);

    private static EfestoRuntimeContext context;

    @BeforeAll
    static void setUp() {
        context = EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
    }
}