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
