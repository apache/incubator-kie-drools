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

package org.jbpm.test.util;

import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kie.api.KieBase;
import org.kie.api.definition.process.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBaseTest {
    
    private static final Logger log = LoggerFactory.getLogger(AbstractBaseTest.class);

    protected boolean useLocking;
   
    public KieBase createKieBase(Process... process) { 
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        for( Process processToAdd : process ) {
            ((KnowledgeBaseImpl) kbase).addProcess(processToAdd);
        }
        return kbase;
    }
    
    @BeforeClass
    public static void configure() { 
        BpmnDebugPrintStream.interceptSysOutSysErr();
    }
    
    @AfterClass
    public static void reset() { 
        BpmnDebugPrintStream.resetInterceptSysOutSysErr();
    }
}
