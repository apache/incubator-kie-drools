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
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsCompiledTestCanonicalKieModule;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.util.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class DMNIncrementalCompilationTest extends BaseInterpretedVsCompiledTestCanonicalKieModule {

    public static final Logger LOG = LoggerFactory.getLogger(DMNIncrementalCompilationTest.class);

    public DMNIncrementalCompilationTest(final boolean useExecModelCompiler, boolean canonicalKieModule) {
        super(useExecModelCompiler, canonicalKieModule);
    }

    @Test
    public void testUpgrade() throws Exception {
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId_v10 = ks.newReleaseId("org.kie", "dmn-test-PR1997", "1.0");
        KieHelper.createAndDeployJar(ks,
                                     releaseId_v10,
                                     wrapWithDroolsModelResource(ks, ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v1/20180731-pr1997.dmn", this.getClass())
                                       .setTargetPath("20180731-pr1997.dmn"),
                                     ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v1/Person.java", this.getClass())
                                       .setTargetPath("acme/Person.java")));
        final KieContainer kieContainer = ks.newKieContainer(releaseId_v10);
        final DMNRuntime runtime = DMNRuntimeUtil.typeSafeGetKieRuntime(kieContainer);

        checkTestUpgrade(kieContainer, runtime, "setFirstName", "setLastName", "Hello John Doe, your age is: 47");

        final ReleaseId releaseId_v11 = ks.newReleaseId("org.kie", "dmn-test-PR1997", "1.1");
        KieHelper.createAndDeployJar(ks,
                                     releaseId_v11,
                                     wrapWithDroolsModelResource(ks, ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v2/20180731-pr1997.dmn", this.getClass())
                                       .setTargetPath("20180731-pr1997.dmn"),
                                     ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v2/Person.java", this.getClass())
                                       .setTargetPath("acme/Person.java")));
        kieContainer.updateToVersion(releaseId_v11);

        checkTestUpgrade(kieContainer, runtime, "setFN", "setLN", "UPGRADED Hello John Doe, your age is: 47");
    }

    @Test
    //See https://issues.jboss.org/browse/DROOLS-3841
    //If both files are present and a FULL build is performed there are no errors
    public void testFullBuildWithImport() {
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId_v10 = ks.newReleaseId("org.kie", "dmn-test-DROOLS-3841", "1.0");
        KieHelper.createAndDeployJar(ks,
                                     releaseId_v10,
                                     ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v1/DROOLS-3841a.dmn", this.getClass()),
                                     ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v2/DROOLS-3841b.dmn", this.getClass()));

        ks.newKieContainer(releaseId_v10);
    }

    @Test
    public void testIncrementalBuildWithImport() {
        //See https://issues.jboss.org/browse/DROOLS-3841
        //If one file is first built and then another incrementally added that contains an Import there are errors
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId releaseId_v10 = ks.newReleaseId("org.kie", "dmn-test-DROOLS-3841", "1.0");
        KieHelper.createAndDeployJar(ks,
                                     releaseId_v10,
                                     ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v1/DROOLS-3841a.dmn", this.getClass()));
        final KieContainer kieContainer = ks.newKieContainer(releaseId_v10);

        final ReleaseId releaseId_v11 = ks.newReleaseId("org.kie", "dmn-test-DROOLS-3841", "1.1");
        KieHelper.createAndDeployJar(ks,
                                     releaseId_v11,
                                     ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v2/DROOLS-3841b.dmn", this.getClass()));
        final Results results = kieContainer.updateToVersion(releaseId_v11);

        assertThat(results.getMessages().isEmpty(), is(true));
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
}

