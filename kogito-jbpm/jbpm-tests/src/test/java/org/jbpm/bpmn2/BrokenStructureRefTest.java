/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.bpmn2;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.jbpm.process.core.impl.XmlProcessDumper;
import org.jbpm.process.core.impl.XmlProcessDumperFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BrokenStructureRefTest {

    @Test
    public void testProcessWithBrokenItemDefinitionUri() throws Exception {
        URL resource = getClass().getResource("/org/jbpm/bpmn2/flow/BPMN2-BrokenStructureRef.bpmn2");
        XmlProcessDumper dumper = XmlProcessDumperFactory.getXmlProcessDumperFactoryService().newXmlProcessDumper();
        assertThat(dumper).isNotNull();
        String processXml = new String(Files.readAllBytes(Paths.get(resource.toURI())));
        assertThat(processXml).isNotNull();
        org.kie.api.definition.process.Process proc = dumper.readProcess(processXml);
        assertThat(proc).isNotNull();
        assertThat(proc.getId()).isEqualTo("BrokenStructureRef");
    }
}
