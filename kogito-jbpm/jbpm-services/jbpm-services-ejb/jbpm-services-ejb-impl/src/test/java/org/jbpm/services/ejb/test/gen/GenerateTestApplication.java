/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.services.ejb.test.gen;

import java.io.File;
import java.util.Date;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jbpm.kie.services.helper.CleanUpCommand;


public class GenerateTestApplication {


	public static void main(String[] args) {
		System.out.println(new Date() + ": Exporting sample ejb application with jbpm services....");
		File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
				.importRuntimeDependencies().resolve().withTransitivity()
				.asFile();

		WebArchive war = ShrinkWrap.create(WebArchive.class, "ejb-app.war");
		for (File file : libs) {
			war.addAsLibrary(file);
		}
		war.addPackage("org.jbpm.services.ejb")
				.addPackage("org.jbpm.services.ejb.api")
				.addPackage("org.jbpm.services.ejb.impl")
				.addPackage("org.jbpm.services.ejb.impl.tx")
				.addPackage("org.jbpm.services.ejb.impl.identity")
				.addClass(CleanUpCommand.class)
				.addAsResource("META-INF/persistence.xml", ArchivePaths.create("META-INF/persistence.xml"))
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");	
		File exported = new File("target/ejb-app.war");
		war.as(ZipExporter.class).exportTo(exported, true);
		
		System.out.println(new Date() + ": Sample application successfully exported to :\n" + exported.getAbsolutePath());
	}
	

}
