/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateBKMEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateBKMEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.core.api.event.DefaultDMNRuntimeEventListener;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A type-check safe runtime creation helper.
 */
public final class DMNRuntimeUtil {

    public static DMNRuntime createRuntime(final Class testClass) {
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(
        ks.newReleaseId("org.kie", "dmn-test-"+UUID.randomUUID(), "1.0"));
        final DMNRuntime runtime = typeSafeGetKieRuntime(kieContainer);
        Assert.assertNotNull(runtime);
        return runtime;
    }

    public static DMNRuntime createRuntime(final String resourceName, final Class testClass) {
        final KieServices ks = KieServices.Factory.get();
        final KieContainer kieContainer = KieHelper.getKieContainer(
                ks.newReleaseId("org.kie", "dmn-test-"+UUID.randomUUID(), "1.0"),
                ks.getResources().newClassPathResource(resourceName, testClass));

        final DMNRuntime runtime = typeSafeGetKieRuntime(kieContainer);
        Assert.assertNotNull(runtime);
        return runtime;
    }
    
    public static DMNRuntime createRuntimeWithAdditionalResources(final String resourceName, final Class testClass, final String... additionalResources) {
        final KieServices ks = KieServices.Factory.get();
        Resource mainResource = ks.getResources().newClassPathResource(resourceName, testClass);
        List<Resource> totalResources = new ArrayList<>();
        totalResources.add(mainResource);
        for ( String add : additionalResources ) {
            totalResources.add( ks.getResources().newClassPathResource(add, testClass) );
        }
        final KieContainer kieContainer = KieHelper.getKieContainer(
                ks.newReleaseId("org.kie", "dmn-test-"+UUID.randomUUID(), "1.0"),
                totalResources.toArray(new Resource[] {}));

        final DMNRuntime runtime = typeSafeGetKieRuntime(kieContainer);
        Assert.assertNotNull(runtime);
        return runtime;
    }

    private static DMNRuntime typeSafeGetKieRuntime(final KieContainer kieContainer) {
        DMNRuntime dmnRuntime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        ((DMNRuntimeImpl) dmnRuntime).setOption(new RuntimeTypeCheckOption(true));
        return dmnRuntime;
    }
    
    public static DMNRuntimeEventListener createListener() {
        return new DefaultDMNRuntimeEventListener() {
            private final Logger logger = LoggerFactory.getLogger(DMNRuntimeEventListener.class);

            @Override
            public void beforeEvaluateDecision(BeforeEvaluateDecisionEvent event) {
                logger.info(event.toString());
            }

            @Override
            public void afterEvaluateDecision(AfterEvaluateDecisionEvent event) {
                logger.info(event.toString());
            }

            @Override
            public void beforeEvaluateBKM(BeforeEvaluateBKMEvent event) {
                logger.info(event.toString());
            }

            @Override
            public void afterEvaluateBKM(AfterEvaluateBKMEvent event) {
                logger.info(event.toString());
            }

            @Override
            public void beforeEvaluateDecisionTable(BeforeEvaluateDecisionTableEvent event) {
                logger.info(event.toString());
            }

            @Override
            public void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {
                logger.info(event.toString());
            }
        };
    }

    public static String formatMessages(final List<DMNMessage> messages) {
        return messages.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }

    private DMNRuntimeUtil() {
        // No constructor for util class.
    }
}
