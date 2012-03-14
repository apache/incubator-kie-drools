package org.jbpm.formbuilder.server;

import java.net.URL;

import junit.framework.TestCase;

import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringConfigurationTest extends TestCase {

    public void testConfiguration() throws Exception {
        URL pathToClasses = getClass().getResource("/FormBuilder.properties");
        String filePath = pathToClasses.toExternalForm();
        //assumes compilation is in target/classes
        filePath = filePath.replace("target/classes/FormBuilder.properties", "src/main/webapp");
        filePath = filePath + "/WEB-INF/springComponents.xml";
        FileSystemXmlApplicationContext fileCtx = new FileSystemXmlApplicationContext(filePath);
        assertNotNull("fileCtx shouldn't be null", fileCtx);
        assertTrue("fileCtx should contain some beans", fileCtx.getBeanDefinitionCount() > 0);
    }
}
