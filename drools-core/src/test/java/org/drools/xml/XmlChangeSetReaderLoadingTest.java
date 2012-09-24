/*
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
package org.drools.xml;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import org.drools.ChangeSet;
import org.drools.io.Resource;
import org.drools.io.impl.ClassPathResource;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class XmlChangeSetReaderLoadingTest {
    private static final String RULES_PATH = "org/drools/test/rules.drl";

    private URLClassLoader classLoader;

    @Before
    public void setup() throws MalformedURLException {
        classLoader = URLClassLoader.newInstance(
            new URL[] { new URL("file:target/test-jar/dynamic-test.jar") }
        );
    }

    @Test
    public void testRulesNotAlreadyOnClassPath() {
        assertNull(getClass().getClassLoader().getResource(RULES_PATH));
    }

    @Test
    public void testLoadingXmlFromJar() throws Exception {
        SemanticModules semanticModules = new SemanticModules();
        semanticModules.addSemanticModule( new ChangeSetSemanticModule() );

        XmlChangeSetReader changeSetReader = new XmlChangeSetReader(semanticModules);
        changeSetReader.setClassLoader(classLoader, null);

        ChangeSet changeSet = changeSetReader.read(classLoader.getResourceAsStream("change-set.xml"));
        assertNotNull(changeSet);

        Collection<Resource> resourcesAdded = changeSet.getResourcesAdded();
        assertEquals(1, resourcesAdded.size());

        Resource resource = resourcesAdded.iterator().next();
        assertNotNull(resource.getInputStream());

        assertTrue(resource instanceof ClassPathResource);

        ClassPathResource classPathResource = (ClassPathResource) resource;
        assertEquals(RULES_PATH, classPathResource.getPath());
    }
}
