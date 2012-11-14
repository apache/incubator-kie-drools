/**
 * Copyright 2012 JBoss Inc
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
package org.jbpm.integration.console.kbase;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.agent.KnowledgeAgent;
import org.kie.agent.KnowledgeAgentConfiguration;
import org.kie.agent.KnowledgeAgentFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.ProcessBuilderFactory;
import org.kie.definition.KnowledgePackage;
import org.kie.io.ResourceChangeScannerConfiguration;
import org.kie.io.ResourceFactory;
import org.drools.marshalling.impl.ProcessMarshallerFactory;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.integration.console.shared.GuvnorConnectionUtils;
import org.jbpm.integration.console.shared.PropertyLoader;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultKnowledgeBaseManager implements KnowledgeBaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultKnowledgeBaseManager.class);
    
    private KnowledgeBase kbase;
    private KnowledgeAgent kagent;
    private Set<String> knownPackages;
    
    public KnowledgeBase getKnowledgeBase() {
        if (this.kbase != null) {
            return kbase;
        }
        knownPackages = new CopyOnWriteArraySet<String>();
        Properties consoleProperties = PropertyLoader.getJbpmConsoleProperties();
        GuvnorConnectionUtils guvnorUtils = new GuvnorConnectionUtils();
        if(guvnorUtils.guvnorExists()) {
            kagent = buildKnowledgeAgent(guvnorUtils);
            if (kagent != null) {
                kbase = kagent.getKnowledgeBase();
            }
        } else {
            logger.warn("Could not connect to Guvnor.");
        }

        // Create a kbase if we couldn't do that with Guvnor
        if (kbase == null) {
            kbase = KnowledgeBaseFactory.newKnowledgeBase();
        }
        
        // load processes from local file system
        addProcessesFromConsoleDirectory(kbase, consoleProperties);
        
        // add known packages
        for (KnowledgePackage pkg : kbase.getKnowledgePackages()) {
            knownPackages.add(pkg.getName());
        }
        return kbase;
    }
    
    private void addProcessesFromConsoleDirectory(KnowledgeBase kbase, Properties consoleProperties) { 
        String directory = System.getProperty("jbpm.console.directory") == null ? consoleProperties.getProperty("jbpm.console.directory") : System.getProperty("jbpm.console.directory");
        if (directory == null || directory.length() < 1 ) {
            logger.info("jbpm.console.directory property not found - processes from local file system will not be loaded");
        } else {
            File file = new File(directory);
            if (!file.exists()) {
                throw new IllegalArgumentException("Could not find " + directory);
            }
            if (!file.isDirectory()) {
                throw new IllegalArgumentException(directory + " is not a directory");
            }
            ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
            ProcessMarshallerFactory.setProcessMarshallerFactoryService(new ProcessMarshallerFactoryServiceImpl());
            ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
            BPMN2ProcessFactory.setBPMN2ProcessProvider(new BPMN2ProcessProviderImpl());
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            for (File subfile: file.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".bpmn") || name.endsWith("bpmn2");
                }})) {
                logger.info("Loading process from file system: " + subfile.getName());
                kbuilder.add(ResourceFactory.newFileResource(subfile), ResourceType.BPMN2);
            }
            kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        }
    }
    
    public synchronized void syncPackages() {
        try {
            GuvnorConnectionUtils guvnorUtils = new GuvnorConnectionUtils();
            if(guvnorUtils.guvnorExists()) {
                
                if (kagent == null) {
                    kagent = buildKnowledgeAgent(guvnorUtils);
                }
                List<String> guvnorPackages = guvnorUtils.getBuiltPackageNames();
                
                guvnorPackages.removeAll(knownPackages);
                
                if (guvnorPackages.size() > 0 && kagent != null) {
                    kagent.applyChangeSet(ResourceFactory.newReaderResource(guvnorUtils.createChangeSet(guvnorPackages)));
                    knownPackages.addAll(guvnorPackages);
                }
            } 
        } catch (Exception e) {
            logger.error("Error while checking packages from Guvnor", e);
        }
    }

    public void dispose() {
        if (this.kagent != null) {
            this.kagent.dispose();
            this.kagent = null;
        }
        this.knownPackages.clear();
        
    }
    
    protected KnowledgeAgent buildKnowledgeAgent(GuvnorConnectionUtils guvnorUtils) {
        try {
            ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
            sconf.setProperty( "drools.resource.scanner.interval", "10" );
            ResourceFactory.getResourceChangeScannerService().configure( sconf );
            ResourceFactory.getResourceChangeScannerService().start();
            ResourceFactory.getResourceChangeNotifierService().start();
            KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
            aconf.setProperty("drools.agent.newInstance", "false");
            KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("Guvnor default", aconf);
            kagent.applyChangeSet(ResourceFactory.newReaderResource(guvnorUtils.createChangeSet()));
            return kagent;
        } catch (Throwable t) {
            logger.error("Could not load processes from Guvnor: " + t.getMessage(), t);
            return null;
        }
    }
}
