/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.bpmn2;

import java.io.InputStream;

import org.drools.core.util.IoUtils;
import org.jbpm.process.core.impl.XmlProcessDumper;
import org.jbpm.process.core.impl.XmlProcessDumperFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BrokenStructureRefTest {

	@Test
	public void testProcessWithBrokenItemDefinitionUri() throws Exception {
		InputStream inputBpmn = getClass().getResourceAsStream("/BPMN2-BrokenStructureRef.bpmn2");
		XmlProcessDumper dumper = XmlProcessDumperFactory.getXmlProcessDumperFactoryService().newXmlProcessDumper();
		assertNotNull(dumper);
		String processXml = new String(IoUtils.readBytesFromInputStream(inputBpmn), "UTF-8");
		assertNotNull(processXml);
		org.kie.api.definition.process.Process proc = dumper.readProcess(processXml);
		assertNotNull(proc);
		assertEquals("BrokenStructureRef", proc.getId());
	}
}
