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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploymentDescriptorManager {

	private static final Logger logger = LoggerFactory.getLogger(DeploymentDescriptorManager.class);	
	
	
	private String defaultPU = "org.jbpm.persistence.jpa";
	
	public DeploymentDescriptorManager() {
		
	}
	
	public DeploymentDescriptorManager(String defaultPU) {
		this.defaultPU = defaultPU;
	}
	
	public DeploymentDescriptor getDefaultDescriptor() {		
		DeploymentDescriptor defaultDesc = null;
		URL defaultDescriptorLocation = getDefaultdescriptorlocation();
		
		if (defaultDescriptorLocation != null) {
			try {
				logger.debug("Reading default descriptor from " + defaultDescriptorLocation);
				defaultDesc = DeploymentDescriptorIO.fromXml(defaultDescriptorLocation.openStream());
			} catch (IOException e) {
				throw new RuntimeException("Unable to read default deployment descriptor from " + defaultDescriptorLocation, e);
			}
		} else {
			logger.debug("No descriptor found returning default instance");
			defaultDesc = new DeploymentDescriptorImpl(defaultPU);
		}
		
		return defaultDesc;
	}
	
	public List<DeploymentDescriptor> getDeploymentDescriptorHierarchy(KieContainer kieContainer) {
		List<DeploymentDescriptor> descriptorHierarchy = new ArrayList<DeploymentDescriptor>();

		InternalKieModule module = ((KieModuleKieProject) ((KieContainerImpl)kieContainer).getKieProject()).getInternalKieModule();
		collectDeploymentDescriptors(module, descriptorHierarchy);

		// last is the default descriptor
		descriptorHierarchy.add(getDefaultDescriptor());	
		
		return descriptorHierarchy;
	}
	
	protected URL getDefaultdescriptorlocation() {
		String defaultDescriptorLocation = System.getProperty("org.kie.deployment.desc.location");
		URL locationUrl = null;
		if (defaultDescriptorLocation != null) {
	        if (defaultDescriptorLocation.startsWith("classpath:")) {
				String stripedLocation = defaultDescriptorLocation.replaceFirst("classpath:", "");
				locationUrl = this.getClass().getResource(stripedLocation);
		        if (locationUrl == null) {
		        	locationUrl = Thread.currentThread().getContextClassLoader().getResource(stripedLocation);
		        }
			} else {
				try {
					locationUrl = new URL(defaultDescriptorLocation);
				} catch (MalformedURLException e) {
					locationUrl = this.getClass().getResource(defaultDescriptorLocation);
			        if (locationUrl == null) {
			        	locationUrl = Thread.currentThread().getContextClassLoader().getResource(defaultDescriptorLocation);
			        }
				}
			}
		}
        
        return locationUrl;
	}
	
	protected void collectDeploymentDescriptors(InternalKieModule kmodule, List<DeploymentDescriptor> descriptorHierarchy) {
		DeploymentDescriptor descriptor = getDescriptorFromKModule(kmodule);
		if (descriptor != null) {
			descriptorHierarchy.add(descriptor);
		}
		
		if (kmodule.getKieDependencies() != null) {
			Collection<InternalKieModule> depModules = kmodule.getKieDependencies().values();
			for (InternalKieModule depModule : depModules) {
				collectDeploymentDescriptors(depModule, descriptorHierarchy);
			}
		}
	}
	
	protected DeploymentDescriptor getDescriptorFromKModule(InternalKieModule kmodule) {
		DeploymentDescriptor desc = null;
		if (kmodule.isAvailable(DeploymentDescriptor.META_INF_LOCATION)) {
			byte[] content = kmodule.getBytes(DeploymentDescriptor.META_INF_LOCATION);
			ByteArrayInputStream input = new ByteArrayInputStream(content);
			try {
				desc = DeploymentDescriptorIO.fromXml(input);
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					logger.debug("Error when closing stream of kie-deployment-descriptor.xml");
				}
			}		
		}
		
		return desc;
	}
}
