/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.ruleflow.core.validation;

import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RuleFlowProcessValidatorTest {

    private RuleFlowProcessValidator validator;

    private List<ProcessValidationError> errors;

    @Mock
    private RuleFlowProcess process;

    @Mock
    private Node node;

    @Before
    public void setUp() throws Exception {
        errors = new ArrayList<ProcessValidationError>();
        validator = RuleFlowProcessValidator.getInstance();
    }

    @Test
    public void testAddErrorMessage() throws Exception {
        when(node.getName()).thenReturn("nodeName");
        when(node.getId()).thenReturn(Long.MAX_VALUE);
        validator.addErrorMessage(process, node, errors, "any message");
        assertEquals(1, errors.size());
        assertEquals("Node 'nodeName' [" + Long.MAX_VALUE + "] any message", errors.get(0).getMessage());
    }
}
