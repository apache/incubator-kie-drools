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

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class TemplateManager {
    
    private static final Logger logger = LoggerFactory.getLogger(TemplateManager.class);

    private static TemplateManager INSTANCE = new TemplateManager();
    
    private StringTemplateLoader stringLoader = new StringTemplateLoader();
    private Configuration cfg;
    
    private String templateDirectory = System.getProperty("org.jbpm.email.templates.dir");
    private boolean watcherEnabled = Boolean.parseBoolean(System.getProperty("org.jbpm.email.templates.watcher.enabled", "false"));
    private Integer watcherInterval = Integer.parseInt(System.getProperty("org.jbpm.email.templates.watcher.interval", "5"));
    
    private TemplateDirectoryWatcher watcher;
    
    protected TemplateManager() {
        cfg = new Configuration(Configuration.VERSION_2_3_26);
        cfg.setTemplateLoader(stringLoader);
        cfg.setDefaultEncoding("UTF-8");
        
        loadTemplates();
        
        if (watcherEnabled) {
            this.watcher = new TemplateDirectoryWatcher(templateDirectory);
            Thread watcherThread = new Thread(watcher, "Email Template Watch Thread");
            watcherThread.start();
        }
    }
    
    public static TemplateManager get() {
        return INSTANCE;
    }
    
    public static synchronized TemplateManager reset() { 
        // close in case there is watcher running to stop it
        INSTANCE.close();
        // create new instance
        INSTANCE = new TemplateManager();
        return INSTANCE;
    }
    
    public String render(String templateName, Map<String, Object> parameters) {
        StringWriter out = new StringWriter();
        try {
            Template template = cfg.getTemplate(templateName);
            
            template.process(parameters, out);
        } catch (Exception e) {
            throw new IllegalArgumentException("Template " + templateName + " not found", e);
        }
        return out.toString();
    }
    
    public void registerTemplate(String id, Object template) {
        this.stringLoader.putTemplate(id, template.toString());

    }

    public void unregisterTemplate(String id) {
        // no-op

    }
    
    protected void loadTemplates() {
        if (templateDirectory != null) {
            File directory = new File(templateDirectory);
            
            if (directory.exists() && directory.isDirectory()) {
                File[] foundTemplates = directory.listFiles((dir, name) -> { return name.endsWith(".html");});
                
                for (File templateFile : foundTemplates) {
                    
                    loadTemplate(templateFile);
                }
            }
        }
    }
    
    protected void loadTemplate(File templateFile) {
        String templateId = resolveTemplateId(templateFile);
        
        try (FileInputStream inputStream = new FileInputStream(templateFile)) {
            this.stringLoader.putTemplate(templateId, read(inputStream));
            
            logger.info("Loaded template {} from file {}", templateId, templateFile);
        } catch (Exception e) {
            logger.warn("Exception while loading template from {} due to {}", templateFile, e.getMessage(), e);
        }
    }
    
    protected void removeTemplate(File templateFile) {
        String templateId = resolveTemplateId(templateFile);
        stringLoader.removeTemplate(templateId);
        logger.info("Removed template {} backed by file {}", templateId, templateFile);
    }
    
    protected String resolveTemplateId(File templateFile) {
        String templateId = templateFile.getName().substring(0, templateFile.getName().lastIndexOf("."));
        
        return templateId;
    }
     
    protected String read(InputStream input) {
        String lineSeparator = System.getProperty("line.separator");

        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")))) {
            return buffer.lines().collect(Collectors.joining(lineSeparator));
        } catch (Exception e) {
            return null;
        }
    }
    
    public void close() {
        if (this.watcher != null) {
            this.watcher.stop();            
        }
    }
    
    private class TemplateDirectoryWatcher implements Runnable {
     
        private WatchService watcher;
        private Path toWatch;
        private AtomicBoolean active = new AtomicBoolean(true);     
        
        public TemplateDirectoryWatcher(String configFilePath) {
            
            this.toWatch = Paths.get(configFilePath);
            
            try {
                this.watcher = toWatch.getFileSystem().newWatchService();
                logger.debug("About to start watching " + toWatch.toString());
                toWatch.register(watcher, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
            } catch (Exception e) {
                logger.error("Error when setting up config file watcher :: " + e.getMessage(), e);
                this.active.set(false);
            }
        }

        public void stop() {
            this.active.set(false);
        }
        
        @Override
        public void run() {
            try{
                while(active.get()) {
                    WatchKey key = watcher.poll(watcherInterval, TimeUnit.SECONDS);
                    if (key != null && active.get()) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            Path updatedFile = (Path) event.context();
                            File modifiedFile = updatedFile.toFile();
                            if (modifiedFile.getName().endsWith("html")) {
                                
                                if (event.kind().equals(ENTRY_DELETE)) {
                                    logger.debug("Found deleted template file {}, removing it", modifiedFile);
                                    removeTemplate(new File(templateDirectory, modifiedFile.getName()));
                                } else {
                                    logger.debug("Found updated or new template file {}, loading it", modifiedFile);
                                    loadTemplate(new File(templateDirectory, modifiedFile.getName()));
                                }
                            }
                        }
                        key.reset();
                    }
                }
            } catch (InterruptedException e) {
                logger.debug("Interrupted exception received...");
            }
        }
    }
}
