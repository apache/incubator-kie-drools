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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.scanner.KieMavenRepository;

import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public abstract class AbstractDeploymentDescriptorTest extends AbstractBaseTest {

	@After
	public void cleanup() {
		// always reset location of the default deployment descriptors after
		// each test
		System.clearProperty("org.kie.deployment.desc.location");
	}
	
	/* helper methods */

	protected void installKjar(ReleaseId releaseId, InternalKieModule kJar1) {
		File pom = new File("target/kmodule", "pom.xml");
		pom.getParentFile().mkdir();
		try {
			FileOutputStream fs = new FileOutputStream(pom);
			fs.write(getPom(releaseId).getBytes());
			fs.close();
		} catch (Exception e) {

		}
		KieMavenRepository repository = getKieMavenRepository();
		repository.installArtifact(releaseId, kJar1, pom);
	}
	
	protected String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
		String pom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
				+ "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n"
				+ "  <modelVersion>4.0.0</modelVersion>\n" + "\n"
				+ "  <groupId>" + releaseId.getGroupId() + "</groupId>\n"
				+ "  <artifactId>" + releaseId.getArtifactId()
				+ "</artifactId>\n" + "  <version>" + releaseId.getVersion()
				+ "</version>\n" + "\n";
		if (dependencies != null && dependencies.length > 0) {
			pom += "<dependencies>\n";
			for (ReleaseId dep : dependencies) {
				pom += "<dependency>\n";
				pom += "  <groupId>" + dep.getGroupId() + "</groupId>\n";
				pom += "  <artifactId>" + dep.getArtifactId()
						+ "</artifactId>\n";
				pom += "  <version>" + dep.getVersion() + "</version>\n";
				pom += "</dependency>\n";
			}
			pom += "</dependencies>\n";
		}
		pom += "</project>";
		return pom;
	}

	protected InternalKieModule createKieJar(KieServices ks, ReleaseId releaseId, Map<String, String> resources, ReleaseId... dependencies) {

		KieFileSystem kfs = createKieFileSystemWithKProject(ks);
		kfs.writePomXML(getPom(releaseId, dependencies));

		for (Map.Entry<String, String> entry : resources.entrySet()) {
			kfs.write(entry.getKey(), ResourceFactory
					.newByteArrayResource(entry.getValue().getBytes()));
		}

		KieBuilder kieBuilder = ks.newKieBuilder(kfs);
		if (!kieBuilder.buildAll().getResults().getMessages().isEmpty()) {
			for (Message message : kieBuilder.buildAll().getResults()
					.getMessages()) {
				logger.error("Error Message: ({}) {}", message.getPath(), message.getText());
			}
			throw new RuntimeException(
					"There are errors builing the package, please check your knowledge assets!");
		}

		return (InternalKieModule) kieBuilder.getKieModule();
	}

	protected KieFileSystem createKieFileSystemWithKProject(KieServices ks) {
		KieModuleModel kproj = ks.newKieModuleModel();

		KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase-test")
				.setDefault(true).addPackage("*")
				.setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
				.setEventProcessingMode(EventProcessingOption.STREAM);

		kieBaseModel1.newKieSessionModel("ksession-test").setDefault(true)
				.setType(KieSessionModel.KieSessionType.STATEFUL)
				.setClockType(ClockTypeOption.get("realtime"));
		KieFileSystem kfs = ks.newKieFileSystem();
		kfs.writeKModuleXML(kproj.toXML());
		return kfs;
	}
}
