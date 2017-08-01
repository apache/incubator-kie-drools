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

package org.jbpm.compiler.xml;

import java.io.StringReader;
import java.util.List;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.xml.SemanticModules;
import org.jbpm.process.core.impl.XmlProcessDumper;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;

public class XmlRuleFlowProcessDumper extends XmlWorkflowProcessDumper implements XmlProcessDumper {
    
    public static final XmlRuleFlowProcessDumper INSTANCE = new XmlRuleFlowProcessDumper();
    
    public XmlRuleFlowProcessDumper() {
        super(
            "RuleFlow", 
            "http://drools.org/drools-5.0/process",
            "drools-processes-5.0.xsd", 
            new ProcessSemanticModule()
        );
    }

	public String dumpProcess(Process process) {
		return dump((WorkflowProcess) process, false);
	}

	@Override
	public Process readProcess(String processXml) {
		KnowledgeBuilderConfigurationImpl configuration = new KnowledgeBuilderConfigurationImpl();
        SemanticModules modules = configuration.getSemanticModules();
        modules.addSemanticModule(new ProcessSemanticModule());
        XmlProcessReader xmlReader = new XmlProcessReader( modules, Thread.currentThread().getContextClassLoader() );
        try {
        	List<Process> processes = xmlReader.read(new StringReader(processXml));
        	return processes.get(0);
        } catch (Throwable t) {
        	t.printStackTrace();
        	return null;
        }
	}
    
    
    
}
