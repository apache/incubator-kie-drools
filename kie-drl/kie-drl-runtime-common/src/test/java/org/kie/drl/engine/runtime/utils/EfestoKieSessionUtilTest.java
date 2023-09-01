package org.kie.drl.engine.runtime.utils;

import org.drools.model.Model;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;

import static org.assertj.core.api.Assertions.assertThat;

class EfestoKieSessionUtilTest {

    private static final String fullModelResourcesSourceClassName = "org.kie.drl.engine.compilation.model.test.Rulesefe9b92fdd254fbabc9e9002be0d51d6";

    private static final String basePath = "/TestingRule";

    @Disabled("DROOLS-7090 : In this test, there is no RuntimeService for drl so this cannot find IndexFile.drl_json")
    @Test
    void loadKieSession() {
        EfestoRuntimeContext context = EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        ModelLocalUriId localUri = new ModelLocalUriId(LocalUri.parse("/drl" + basePath));
        KieSession retrieved = EfestoKieSessionUtil.loadKieSession(localUri, context);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getIdentifier()).isZero();
    }

    @Test
    void loadModel() {
        EfestoRuntimeContext context = EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        Model retrieved = EfestoKieSessionUtil.loadModel(fullModelResourcesSourceClassName, context);
        assertThat(retrieved).isNotNull();
    }
}
