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

package org.jbpm.process.workitem.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateManagerTest {
    
    private static final Logger logger = LoggerFactory.getLogger(TemplateManagerTest.class);
    
    private TrackingTemplateManager templateManager;
    private File templateDir = new File("src/test/resources/templates");
    
    private String originalTemplate = "<html><body>Hello ${Name}</body></html>";
    
    @Before
    public void setup() {
        System.setProperty("org.jbpm.email.templates.watcher.enabled", "true");  
        System.setProperty("org.jbpm.email.templates.dir", templateDir.getAbsolutePath());
    }
    
    @After
    public void cleanup() {
        if (templateManager != null) {
            templateManager.close();
        }
        
        File[] templatesToRemove = templateDir.listFiles((file, name) -> { return !name.equals("basic-email.html");});
        for (File template : templatesToRemove) {
            template.delete();
        }
        
        // restore template file to original content
        try {
            Files.write(new File(templateDir, "basic-email.html").toPath(), originalTemplate.getBytes());
        } catch (IOException e) {
            logger.debug("Error when restoring basic-email.html template", e);
        }
        
        System.clearProperty("org.jbpm.email.templates.watcher.enabled");
        System.clearProperty("org.jbpm.email.templates.dir");
    }
    
    private void configureTemplateManager(CountDownLatch loadTemplate, CountDownLatch removeTemplate) {
        // close the default instance as we create extended version to keep track of background actions
        TemplateManager.get().close();
        
        templateManager = new TrackingTemplateManager(loadTemplate, removeTemplate);
    }
    
    @Test(timeout=30000)
    public void testLoadNewTemplateFile() throws Exception {
        CountDownLatch loadTemplate = new CountDownLatch(1);        
        configureTemplateManager(loadTemplate, null);
        
        String expected = "<html><body>Hello John</body></html>";
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("Name", "John");
        
        String rendered = templateManager.render("basic-email", parameters);
        assertEquals(expected, rendered);
        
        String advancedTemplate = "<html><body>Hello ${Name}, welcome!</body></html>";
        String expectedAdvanced = "<html><body>Hello John, welcome!</body></html>";
        
        Thread.sleep(3000);
        
        Files.write(new File(templateDir, "advanced-email.html").toPath(), advancedTemplate.getBytes());
        
        loadTemplate.await();
        
        rendered = templateManager.render("advanced-email", parameters);
        assertEquals(expectedAdvanced, rendered);
    }
    
    @Test(timeout=30000)
    public void testLoadUpdatedTemplateFile() throws Exception {
        CountDownLatch loadTemplate = new CountDownLatch(1);        
        configureTemplateManager(loadTemplate, null);
        
        String expected = "<html><body>Hello John</body></html>";
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("Name", "John");
        
        String rendered = templateManager.render("basic-email", parameters);
        assertEquals(expected, rendered);
        
        String updatedTemplate = "<html><body>Hello ${Name}, welcome!</body></html>";
                  
        Files.write(new File(templateDir, "basic-email.html").toPath(), updatedTemplate.getBytes());
        
        loadTemplate.await();
        
        assertThat(templateManager.getCreatedOrUpdated()).hasSize(1).contains("basic-email");
    }
    
    @Test(timeout=30000)
    public void testDeletedTemplateFile() throws Exception {
        CountDownLatch removeTemplate = new CountDownLatch(1);        
        configureTemplateManager(null, removeTemplate);
        
        String expected = "<html><body>Hello John</body></html>";
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("Name", "John");
        
        String rendered = templateManager.render("basic-email", parameters);
        assertEquals(expected, rendered);
              
        Files.delete(new File(templateDir, "basic-email.html").toPath());
        
        removeTemplate.await();        
        
        assertThat(templateManager.getRemoved()).hasSize(1).contains("basic-email");
    }
    
    private class TrackingTemplateManager extends TemplateManager {
        private CountDownLatch loadTemplate;
        private CountDownLatch removeTemplate;
        
        private Set<String> createdOrUpdated = new LinkedHashSet<>();
        private Set<String> removed = new LinkedHashSet<>();
        
        public TrackingTemplateManager(CountDownLatch loadTemplate, CountDownLatch removeTemplate) {
            super();
            this.loadTemplate = loadTemplate;
            this.removeTemplate = removeTemplate;
        }

        @Override
        protected void loadTemplate(File templateFile) {
            super.loadTemplate(templateFile);
            
            if (loadTemplate != null) {
                createdOrUpdated.add(resolveTemplateId(templateFile));
                loadTemplate.countDown();
            }
        }

        @Override
        protected void removeTemplate(File templateFile) {
            super.removeTemplate(templateFile);
            
            if (removeTemplate != null) {
                removed.add(resolveTemplateId(templateFile));
                removeTemplate.countDown();
            }
        }

        
        protected Set<String> getCreatedOrUpdated() {
            return createdOrUpdated;
        }

        protected Set<String> getRemoved() {
            return removed;
        }        
    }
}
