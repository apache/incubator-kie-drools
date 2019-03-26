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

package org.jbpm.compiler.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import org.drools.core.xml.SemanticModules;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;
import org.kie.api.definition.process.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestXml extends AbstractBaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(TestXml.class);

    @Test
    public void testSimpleXml() throws Exception {
        SemanticModules modules = new SemanticModules();
        modules.addSemanticModule(new ProcessSemanticModule());
        XmlProcessReader reader = new XmlProcessReader(modules, getClass().getClassLoader());
        reader.read(new InputStreamReader(TestXml.class.getResourceAsStream("XmlTest.xml")));
        List<Process> processes = reader.getProcess();
        assertNotNull(processes);
        assertEquals(1, processes.size());
        RuleFlowProcess process = (RuleFlowProcess) processes.get(0);
        assertNotNull(process);

        String output = XmlRuleFlowProcessDumper.INSTANCE.dump(process);
        logger.info(output);
        reader = new XmlProcessReader(new SemanticModules(), getClass().getClassLoader());
        reader.read(new StringReader(output));
    }
}
