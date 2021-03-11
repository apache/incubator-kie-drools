/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.incrementalcompilation;

import java.lang.reflect.Method;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.util.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DMNIncrementalCompilationTest extends BaseInterpretedVsCompiledTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNIncrementalCompilationTest.class);

    public DMNIncrementalCompilationTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testUpgrade() throws Exception {
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId_v10 = ks.newReleaseId("org.kie", "dmn-test-PR1997", "1.0");
        KieHelper.createAndDeployJar(ks,
                                     releaseId_v10,
                                     ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v1/20180731-pr1997.dmn", this.getClass())
                                       .setTargetPath("20180731-pr1997.dmn"),
                                     ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v1/Person.java", this.getClass())
                                       .setTargetPath("acme/Person.java"));
        final KieContainer kieContainer = ks.newKieContainer(releaseId_v10);
        final DMNRuntime runtime = DMNRuntimeUtil.typeSafeGetKieRuntime(kieContainer);

        checkTestUpgrade(kieContainer, runtime, "setFirstName", "setLastName", "Hello John Doe, your age is: 47");

        final ReleaseId releaseId_v11 = ks.newReleaseId("org.kie", "dmn-test-PR1997", "1.1");
        KieHelper.createAndDeployJar(ks,
                                     releaseId_v11,
                                     ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v2/20180731-pr1997.dmn", this.getClass())
                                       .setTargetPath("20180731-pr1997.dmn"),
                                     ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v2/Person.java", this.getClass())
                                       .setTargetPath("acme/Person.java"));
        kieContainer.updateToVersion(releaseId_v11);

        checkTestUpgrade(kieContainer, runtime, "setFN", "setLN", "UPGRADED Hello John Doe, your age is: 47");
    }

    private void checkTestUpgrade(final KieContainer kieContainer,
                                  final DMNRuntime runtime,
                                  final String methodNameForFirstName,
                                  final String methodNameForLastName,
                                  final String sayHelloAndAgeDecisionResultValue) throws Exception {
        // the Model does NOT change in its NAME or ID, but it does change indeed in the LiteralExpression decision logic.
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_7a39d775-bce9-45e3-aa3b-147d6f0028c7", "20180731-pr1997"); //  // NO change v1.0 -> v1.1
        assertThat(runtime, notNullValue());

        final Object personByReflection = kieContainer.getClassLoader().loadClass("acme.Person").newInstance();
        final Method setFirstNameMethod = personByReflection.getClass().getMethod(methodNameForFirstName, String.class);// change v1.0 -> v1.1
        setFirstNameMethod.invoke(personByReflection, "John");
        final Method setLastNameMethod = personByReflection.getClass().getMethod(methodNameForLastName, String.class);// change v1.0 -> v1.1
        setLastNameMethod.invoke(personByReflection, "Doe");
        final Method setAgeMethod = personByReflection.getClass().getMethod("setAge", int.class);
        setAgeMethod.invoke(personByReflection, 47);

        final DMNContext context = DMNFactory.newContext();
        context.set("a Person", personByReflection);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("Say hello and age"), is(sayHelloAndAgeDecisionResultValue));// change v1.0 -> v1.1
    }

    @Test
    public void testUpgradeWithImport() {
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId_v10 = ks.newReleaseId("org.kie", "dmn-test-RHDM-965", "1.0");
        KieHelper.createAndDeployJar(ks,
                                     releaseId_v10,
                                     ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/import-itemdef-100/air-conditioning-control.dmn", this.getClass())
                                       .setTargetPath("air-conditioning-control.dmn"),
                                     ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/import-itemdef-100/air-conditioning-data-types.dmn", this.getClass())
                                       .setTargetPath("air-conditioning-data-types.dmn"));
        final KieContainer kieContainer = ks.newKieContainer(releaseId_v10);
        final DMNRuntime runtime = DMNRuntimeUtil.typeSafeGetKieRuntime(kieContainer);

        final ReleaseId releaseId_v11 = ks.newReleaseId("org.kie", "dmn-test-RHDM-965", "1.1");
        KieHelper.createAndDeployJar(ks,
                                     releaseId_v11,
                                     ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/import-itemdef-101/air-conditioning-control.dmn", this.getClass())
                                       .setTargetPath("air-conditioning-control.dmn"),
                                     ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/import-itemdef-101/air-conditioning-data-types.dmn", this.getClass())
                                       .setTargetPath("air-conditioning-data-types.dmn"));
        kieContainer.updateToVersion(releaseId_v11);

    }
}

