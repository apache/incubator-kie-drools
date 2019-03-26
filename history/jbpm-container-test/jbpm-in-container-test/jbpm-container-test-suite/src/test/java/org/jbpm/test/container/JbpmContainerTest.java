/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.container;

import static java.lang.String.format;

import org.assertj.core.api.Assertions;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Arquillian.class)
public abstract class JbpmContainerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JbpmContainerTest.class);

    protected static final String REMOTE_CONTAINER = "remote-container";

    // Allows the tests to retrieve their names
    @Rule
    public TestName name = new TestName();

    /**
     * Gets the name of current test method.
     */
    public final String getTestName() {
        return name.getMethodName();
    }

    // prints the test class name before executing it
    @ClassRule
    public static TestWatcher classWatcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            LOGGER.info(format("%n%n%25s Starting [%s]%n", "", description.getClassName()));
        }
    };

    // prints test method name before executing it and results after execution
    @Rule
    public TestWatcher methodWatcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            LOGGER.info(format("==== %s ====", description.getMethodName()));
        }

        @Override
        protected void succeeded(Description description) {
            LOGGER.info("succeded {} - {}", description.getClassName(), description.getMethodName());
        }

        @Override
        protected void failed(Throwable e, Description description) {
            LOGGER.warn("failed {} - {}", description.getClassName(), description.getMethodName());
        }
    };

    public KieSession getSession(Resource... resources) {

        KieBase kbase = getKBase(resources);
        KieSession ksession = kbase.newKieSession();

        return ksession;
    }

    public KieBase getKBase(Resource... resources) {

        KieServices kservies = KieServices.Factory.get();
        KieFileSystem kfilesystem = kservies.newKieFileSystem();
        for (int i = 0; resources != null && i < resources.length; ++i) {
            kfilesystem.write(resources[i]);
        }

        KieBuilder kbuilder = kservies.newKieBuilder(kfilesystem);
        kbuilder.buildAll();

        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
            Assertions.fail(kbuilder.getResults().toString());
        }

        KieBase kbase = kservies.newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();
        
        return kbase;
    }
}
