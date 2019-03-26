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

package org.jbpm.bpmn2;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.BPMN2ProcessProvider;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.bpmn2.xpath.XPATHProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.kie.internal.builder.KnowledgeBuilder;

public class BPMN2ProcessProviderImpl implements BPMN2ProcessProvider {

	static {
        ProcessDialectRegistry.setDialect("XPath", new XPATHProcessDialect());
	}
	
    public void configurePackageBuilder(KnowledgeBuilder knowledgeBuilder) {
        KnowledgeBuilderConfigurationImpl conf = ((KnowledgeBuilderImpl) knowledgeBuilder).getBuilderConfiguration();
        if (conf.getSemanticModules().getSemanticModule(BPMNSemanticModule.BPMN2_URI) == null) {
        	conf.addSemanticModule(new BPMNSemanticModule());
        	conf.addSemanticModule(new BPMNDISemanticModule());
        	conf.addSemanticModule(new BPMNExtensionsSemanticModule());
        }
    }

}
