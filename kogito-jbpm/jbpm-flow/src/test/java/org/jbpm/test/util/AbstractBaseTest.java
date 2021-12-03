/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.test.util;

import org.drools.core.impl.RuleBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.kie.api.KieBase;
import org.kie.api.definition.process.Process;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.slf4j.Logger;

public abstract class AbstractBaseTest {

    protected Logger logger;

    @BeforeEach
    public void before(TestInfo testInfo) {
        addLogger();
        logger.debug("> " + testInfo.getDisplayName());
    }

    public abstract void addLogger();

    public KogitoProcessRuntime createKogitoProcessRuntime(Process... process) {
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        for (Process processToAdd : process) {
            ((RuleBase) kbase).addProcess(processToAdd);
        }
        return KogitoProcessRuntime.asKogitoProcessRuntime(kbase.newKieSession());
    }

    @BeforeAll
    public static void configure() {
        LoggingPrintStream.interceptSysOutSysErr();
    }

    @AfterAll
    public static void reset() {
        LoggingPrintStream.resetInterceptSysOutSysErr();
    }
}
