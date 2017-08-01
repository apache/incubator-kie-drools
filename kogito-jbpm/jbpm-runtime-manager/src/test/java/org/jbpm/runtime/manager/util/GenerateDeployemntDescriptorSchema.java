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

package org.jbpm.runtime.manager.util;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorIO;

/**
 * Use this generator to create new xsd when changes have been made to deployment descriptor object structure.
 * It will directly store xsd file in src/main/resources but with default name: schema1.xsd so it should be renamed
 *
 */
public class GenerateDeployemntDescriptorSchema {

	public static void main(String[] args) throws Exception {
		JAXBContext jaxbContext = DeploymentDescriptorIO.getContext();
		SchemaOutputResolver sor = new FileSchemaOutputResolver();
		jaxbContext.generateSchema(sor);
	}
	
	private static class FileSchemaOutputResolver extends SchemaOutputResolver {

	    public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
	        File file = new File("src/main/resources/" + suggestedFileName);
	        StreamResult result = new StreamResult(file);
	        result.setSystemId(file.toURI().toURL().toString());
	        return result;
	    }

	}
}
