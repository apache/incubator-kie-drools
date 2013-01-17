package org.jbpm.compiler.xml;

import java.io.StringReader;
import java.util.List;

import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.xml.SemanticModules;
import org.jbpm.process.core.impl.XmlProcessDumper;
import org.kie.definition.process.Process;
import org.kie.definition.process.WorkflowProcess;

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
		PackageBuilderConfiguration configuration = new PackageBuilderConfiguration();
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
