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

package org.jbpm.runtime.manager.impl.deploy;

import static org.junit.Assert.assertNotNull;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.ObjectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaxbMarshalingTest {

    private static final Logger logger = LoggerFactory.getLogger(JaxbMarshalingTest.class);    
    
	private Class<?>[] jaxbClasses = { DeploymentDescriptorImpl.class};
	
	@Test
	public void testJaxbDeploymentDescriptorSerialization() throws Exception {
		DeploymentDescriptor descriptor = new DeploymentDescriptorImpl();
		descriptor.getBuilder()
		.addTaskEventListener(new ObjectModel("org.jbpm.task.Listener", new Object[]{"test", "another"}));
		
		String output = convertJaxbObjectToString(descriptor);
		logger.debug(output);
		assertNotNull(output);
	}
	
    public String convertJaxbObjectToString(Object object) throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(jaxbClasses).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter stringWriter = new StringWriter();

        marshaller.marshal(object, stringWriter);
        String output = stringWriter.toString();

        return output;
    }
}
