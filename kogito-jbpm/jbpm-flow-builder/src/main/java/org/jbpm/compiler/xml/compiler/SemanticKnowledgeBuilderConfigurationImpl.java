/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.compiler.xml.compiler;

import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.util.ConfFileUtils;
import org.drools.util.ClassUtils;
import org.jbpm.compiler.xml.Handler;
import org.jbpm.compiler.xml.SemanticModule;
import org.jbpm.compiler.xml.core.DefaultSemanticModule;
import org.jbpm.compiler.xml.core.SemanticModules;
import org.jbpm.compiler.xml.core.WrapperSemanticModule;
import org.kie.internal.builder.conf.KnowledgeBuilderOption;
import org.kie.internal.builder.conf.MultiValueKieBuilderOption;
import org.kie.internal.builder.conf.SingleValueKieBuilderOption;
import org.kie.internal.conf.CompositeConfiguration;

public class SemanticKnowledgeBuilderConfigurationImpl extends KnowledgeBuilderConfigurationImpl {
    private SemanticModules semanticModules;

    public SemanticKnowledgeBuilderConfigurationImpl(CompositeConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> compConfig) {
        super(compConfig);
    }

    public void addSemanticModule(SemanticModule module) {
        if (this.semanticModules == null) {
            initSemanticModules();
        }
        this.semanticModules.addSemanticModule(module);
    }

    public SemanticModules getSemanticModules() {
        if (this.semanticModules == null) {
            initSemanticModules();
        }
        return this.semanticModules;
    }

    public void initSemanticModules() {
        this.semanticModules = new SemanticModules();

        RulesSemanticModule ruleModule = new RulesSemanticModule("http://ddefault");

        this.semanticModules.addSemanticModule(new WrapperSemanticModule("http://drools.org/drools-5.0", ruleModule));
        this.semanticModules.addSemanticModule(new WrapperSemanticModule("http://drools.org/drools-5.2", ruleModule));

        // split on each space
        String locations[] = getChainedProperties().getProperty("semanticModules", "").split("\\s");

        // load each SemanticModule
        for (String moduleLocation : locations) {
            // trim leading/trailing spaces and quotes
            moduleLocation = moduleLocation.trim();
            if (moduleLocation.startsWith("\"")) {
                moduleLocation = moduleLocation.substring(1);
            }
            if (moduleLocation.endsWith("\"")) {
                moduleLocation = moduleLocation.substring(0, moduleLocation.length() - 1);
            }
            if (!moduleLocation.equals("")) {
                loadSemanticModule(moduleLocation);
            }
        }
    }

    public void loadSemanticModule(String moduleLocation) {
        URL url = ConfFileUtils.getURL(moduleLocation, getClassLoader(), getClass());
        if (url == null) {
            throw new IllegalArgumentException(moduleLocation + " is specified but cannot be found.'");
        }

        Properties properties = ConfFileUtils.getProperties(url);
        if (properties == null) {
            throw new IllegalArgumentException(moduleLocation + " is specified but cannot be found.'");
        }

        loadSemanticModule(properties);
    }

    public void loadSemanticModule(Properties properties) {
        String uri = properties.getProperty("uri", null);
        if (uri == null || uri.trim().equals("")) {
            throw new RuntimeException("Semantic Module URI property must not be empty");
        }

        DefaultSemanticModule module = new DefaultSemanticModule(uri);

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String elementName = (String) entry.getKey();

            //uri is processed above, so skip
            if ("uri".equals(elementName)) {
                continue;
            }

            if (elementName == null || elementName.trim().equals("")) {
                throw new RuntimeException("Element name must be specified for Semantic Module handler");
            }
            String handlerName = (String) entry.getValue();
            if (handlerName == null || handlerName.trim().equals("")) {
                throw new RuntimeException("Handler name must be specified for Semantic Module");
            }

            Handler handler = (Handler) ClassUtils.instantiateObject(handlerName,
                    getClassLoader());

            if (handler == null) {
                throw new RuntimeException("Unable to load Semantic Module handler '" + elementName + ":" + handlerName + "'");
            } else {
                module.addHandler(elementName,
                        handler);
            }
        }
        this.semanticModules.addSemanticModule(module);
    }

}
