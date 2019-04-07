/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.workitem.core.util;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.core.TestWorkItemManager;
import org.junit.Test;

import static org.junit.Assert.*;

public class RequiredParameterValidatorTest {

    private RequiredParametersTestHandler testHandler = new RequiredParametersTestHandler();
    private TestWorkItemManager testManager = new TestWorkItemManager();

    @Test
    public void testValidaRequiredParameters() throws Exception {
        try {
            WorkItemImpl workItem = new WorkItemImpl();
            workItem.setParameter("firstParam",
                                  "testValue");
            workItem.setParameter("thirdParam",
                                  "testValue");

            testHandler.executeWorkItem(workItem,
                                        testManager);
        } catch (Exception e) {
            fail("Required parameters have been set. No exception should be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidaRequiredParameters() throws Exception {
        try {
            WorkItemImpl workItem = new WorkItemImpl();
            workItem.setParameter("secondParam",
                                  "testValue");

            testHandler.executeWorkItem(workItem,
                                        testManager);
            fail("Exception on required parameters was not thrown by handler");
        } catch (Exception e) {

        }
    }
}
