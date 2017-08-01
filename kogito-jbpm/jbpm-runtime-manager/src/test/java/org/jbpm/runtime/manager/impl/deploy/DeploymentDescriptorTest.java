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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploymentDescriptorTest {
	
	private static final Logger logger = LoggerFactory.getLogger(DeploymentDescriptorTest.class);

	@Test
	public void testWriteDeploymentDescriptorXml() {
		DeploymentDescriptor descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
		
		descriptor.getBuilder()
		.addMarshalingStrategy(new ObjectModel("org.jbpm.testCustomStrategy", 
				new Object[]{
				new ObjectModel("java.lang.String", new Object[]{"param1"}),
				"param2"}))
		.addRequiredRole("experts");
		
		String deploymentDescriptorXml = descriptor.toXml();
		assertNotNull(deploymentDescriptorXml);
		logger.info(deploymentDescriptorXml);
		
		ByteArrayInputStream stream = new ByteArrayInputStream(deploymentDescriptorXml.getBytes());
		DeploymentDescriptor fromXml = DeploymentDescriptorIO.fromXml(stream);
		
		assertNotNull(fromXml);
		assertEquals("org.jbpm.domain", fromXml.getPersistenceUnit());
		assertEquals("org.jbpm.domain", fromXml.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, fromXml.getAuditMode());
		assertEquals(PersistenceMode.JPA, fromXml.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, fromXml.getRuntimeStrategy());
		assertEquals(1, fromXml.getMarshallingStrategies().size());
		assertEquals(0, fromXml.getConfiguration().size());
		assertEquals(0, fromXml.getEnvironmentEntries().size());
		assertEquals(0, fromXml.getEventListeners().size());
		assertEquals(0, fromXml.getGlobals().size());		
		assertEquals(0, fromXml.getTaskEventListeners().size());
		assertEquals(0, fromXml.getWorkItemHandlers().size());
		assertEquals(1, fromXml.getRequiredRoles().size());
	}
	
	@Test
	public void testReadDeploymentDescriptorFromXml() throws Exception {
		InputStream input = this.getClass().getResourceAsStream("/deployment/deployment-descriptor-defaults.xml");
		
		DeploymentDescriptor descriptor = DeploymentDescriptorIO.fromXml(input);
		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());		
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());
		assertEquals(0, descriptor.getRequiredRoles().size());
	}
	
	@Test
	public void testReadDeploymentDescriptorMSFromXml() throws Exception {
		InputStream input = this.getClass().getResourceAsStream("/deployment/deployment-descriptor-defaults-and-ms.xml");
		
		DeploymentDescriptor descriptor = DeploymentDescriptorIO.fromXml(input);
		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
		assertEquals(1, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());		
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());
		assertEquals(1, descriptor.getRequiredRoles().size());
	}
	
	@Test
	public void testReadPartialDeploymentDescriptorFromXml() throws Exception {
		InputStream input = this.getClass().getResourceAsStream("/deployment/partial-deployment-descriptor.xml");
		
		DeploymentDescriptor descriptor = DeploymentDescriptorIO.fromXml(input);
		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());		
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());
		assertEquals(0, descriptor.getRequiredRoles().size());
	}
	
	@Test
	public void testCreateDeploymentDescriptorWithSetters() {
		DeploymentDescriptorImpl descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
		
		descriptor.setAuditMode(AuditMode.JMS);
		descriptor.setEnvironmentEntries(null);
		
		List<ObjectModel> marshallingStrategies = new ArrayList<ObjectModel>();
		marshallingStrategies.add(new ObjectModel("org.jbpm.testCustomStrategy", 
				new Object[]{
				new ObjectModel("java.lang.String", new Object[]{"param1"}),
				"param2"}));
		descriptor.setMarshallingStrategies(marshallingStrategies);
		
		List<String> roles = new ArrayList<String>();
		roles.add("experts");
		
		descriptor.setRequiredRoles(roles);
		
		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
		assertEquals(1, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());		
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());
		assertEquals(1, descriptor.getRequiredRoles().size());
	}
	
	@Test
	public void testPrintDescriptor() {
		DeploymentDescriptor descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
		
		descriptor.getBuilder()
		.addWorkItemHandler(new NamedObjectModel("mvel", "Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()"))
		.addWorkItemHandler(new NamedObjectModel("mvel", "WebService", "new org.jbpm.process.workitem.webservice.WebServiceWorkItemHandler(ksession)"))
		.addWorkItemHandler(new NamedObjectModel("mvel", "Rest", "new org.jbpm.process.workitem.rest.RESTWorkItemHandler()"))
		.addWorkItemHandler(new NamedObjectModel("mvel", "Service Task", "new org.jbpm.process.workitem.bpmn2.ServiceTaskHandler(ksession)"));
		
		logger.debug(descriptor.toXml());
	}
	
	@Test
	public void testWriteDeploymentDescriptorXmlWithDuplicateNamedObjects() {
		DeploymentDescriptor descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
		
		descriptor.getBuilder()
		.addWorkItemHandler(new NamedObjectModel("mvel", "Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()"))
		.addWorkItemHandler(new NamedObjectModel("mvel", "Log", "new org.jbpm.process.instance.impl.demo.CustomSystemOutWorkItemHandler()"))
		.addRequiredRole("experts");
		
		String deploymentDescriptorXml = descriptor.toXml();
		assertNotNull(deploymentDescriptorXml);
		logger.info(deploymentDescriptorXml);
		
		ByteArrayInputStream stream = new ByteArrayInputStream(deploymentDescriptorXml.getBytes());
		DeploymentDescriptor fromXml = DeploymentDescriptorIO.fromXml(stream);
		
		assertNotNull(fromXml);
		assertEquals("org.jbpm.domain", fromXml.getPersistenceUnit());
		assertEquals("org.jbpm.domain", fromXml.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, fromXml.getAuditMode());
		assertEquals(PersistenceMode.JPA, fromXml.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, fromXml.getRuntimeStrategy());
		assertEquals(0, fromXml.getMarshallingStrategies().size());
		assertEquals(0, fromXml.getConfiguration().size());
		assertEquals(0, fromXml.getEnvironmentEntries().size());
		assertEquals(0, fromXml.getEventListeners().size());
		assertEquals(0, fromXml.getGlobals().size());		
		assertEquals(0, fromXml.getTaskEventListeners().size());
		assertEquals(1, fromXml.getWorkItemHandlers().size());
		assertEquals(1, fromXml.getRequiredRoles().size());
	}
	
	@Test
	public void testCreateDeploymentDescriptorWithPrefixedRoles() {
		DeploymentDescriptorImpl descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
		
		descriptor.setAuditMode(AuditMode.JMS);
		descriptor.setEnvironmentEntries(null);
		
		List<ObjectModel> marshallingStrategies = new ArrayList<ObjectModel>();
		marshallingStrategies.add(new ObjectModel("org.jbpm.testCustomStrategy", 
				new Object[]{
				new ObjectModel("java.lang.String", new Object[]{"param1"}),
				"param2"}));
		descriptor.setMarshallingStrategies(marshallingStrategies);
		
		List<String> roles = new ArrayList<String>();
		roles.add("view:managers");
		roles.add("execute:experts");
		roles.add("all:everyone");
		roles.add("employees");
		
		descriptor.setRequiredRoles(roles);
		
		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
		assertEquals(1, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());		
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());
		assertEquals(4, descriptor.getRequiredRoles().size());
		
		List<String> toVerify = descriptor.getRequiredRoles();
		assertEquals(4, toVerify.size());
		assertTrue(toVerify.contains("view:managers"));
		assertTrue(toVerify.contains("execute:experts"));
		assertTrue(toVerify.contains("all:everyone"));
		assertTrue(toVerify.contains("employees"));
		
		toVerify = descriptor.getRequiredRoles(DeploymentDescriptor.TYPE_ALL);
		assertEquals(4, toVerify.size());
		assertTrue(toVerify.contains("managers"));
		assertTrue(toVerify.contains("experts"));
		assertTrue(toVerify.contains("everyone"));
		assertTrue(toVerify.contains("employees"));
		
		toVerify = descriptor.getRequiredRoles(DeploymentDescriptor.TYPE_EXECUTE);
		assertEquals(2, toVerify.size());
		assertTrue(toVerify.contains("experts"));
		assertTrue(toVerify.contains("employees"));
		
		toVerify = descriptor.getRequiredRoles(DeploymentDescriptor.TYPE_VIEW);
		assertEquals(2, toVerify.size());
		assertTrue(toVerify.contains("managers"));
		assertTrue(toVerify.contains("employees"));
	}
	
	@Test
    public void testWriteDeploymentDescriptorXmlWithTransientElements() {
        DeploymentDescriptor descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        
        descriptor.getBuilder()
        .addMarshalingStrategy(new TransientObjectModel("org.jbpm.testCustomStrategy", 
                new Object[]{
                new ObjectModel("java.lang.String", new Object[]{"param1"}),
                "param2"}))
        .addWorkItemHandler(new TransientNamedObjectModel("mvel", "Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()"))
        .addRequiredRole("experts");
        
        String deploymentDescriptorXml = descriptor.toXml();
        assertNotNull(deploymentDescriptorXml);
        logger.info(deploymentDescriptorXml);
        
        ByteArrayInputStream stream = new ByteArrayInputStream(deploymentDescriptorXml.getBytes());
        DeploymentDescriptor fromXml = DeploymentDescriptorIO.fromXml(stream);
        
        assertNotNull(fromXml);
        assertEquals("org.jbpm.domain", fromXml.getPersistenceUnit());
        assertEquals("org.jbpm.domain", fromXml.getAuditPersistenceUnit());
        assertEquals(AuditMode.JPA, fromXml.getAuditMode());
        assertEquals(PersistenceMode.JPA, fromXml.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, fromXml.getRuntimeStrategy());
        assertEquals(0, fromXml.getMarshallingStrategies().size());
        assertEquals(0, fromXml.getConfiguration().size());
        assertEquals(0, fromXml.getEnvironmentEntries().size());
        assertEquals(0, fromXml.getEventListeners().size());
        assertEquals(0, fromXml.getGlobals().size());       
        assertEquals(0, fromXml.getTaskEventListeners().size());
        assertEquals(0, fromXml.getWorkItemHandlers().size());
        assertEquals(1, fromXml.getRequiredRoles().size());
    }
	
	@Test
    public void testEmptyDeploymentDescriptor() {
	    DeploymentDescriptorImpl descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        
        descriptor.getBuilder()
        .addMarshalingStrategy(new ObjectModel("org.jbpm.testCustomStrategy", 
                new Object[]{
                new ObjectModel("java.lang.String", new Object[]{"param1"}),
                "param2"}))
        .addRequiredRole("experts");
        
        assertFalse(descriptor.isEmpty());
        
        InputStream input = this.getClass().getResourceAsStream("/deployment/empty-descriptor.xml");        
        DeploymentDescriptor fromXml = DeploymentDescriptorIO.fromXml(input);
        
        assertNotNull(fromXml);        
        assertTrue(((DeploymentDescriptorImpl)fromXml).isEmpty());
        
        assertNull(fromXml.getPersistenceUnit());
        assertNull(fromXml.getAuditPersistenceUnit());
        assertEquals(AuditMode.JPA, fromXml.getAuditMode());
        assertEquals(PersistenceMode.JPA, fromXml.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, fromXml.getRuntimeStrategy());
        assertEquals(0, fromXml.getMarshallingStrategies().size());
        assertEquals(0, fromXml.getConfiguration().size());
        assertEquals(0, fromXml.getEnvironmentEntries().size());
        assertEquals(0, fromXml.getEventListeners().size());
        assertEquals(0, fromXml.getGlobals().size());       
        assertEquals(0, fromXml.getTaskEventListeners().size());
        assertEquals(0, fromXml.getWorkItemHandlers().size());
        assertEquals(0, fromXml.getRequiredRoles().size());
    }
}
