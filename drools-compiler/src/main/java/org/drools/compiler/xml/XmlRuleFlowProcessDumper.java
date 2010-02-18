package org.drools.compiler.xml;

public class XmlRuleFlowProcessDumper extends XmlWorkflowProcessDumper {
    
    public static final XmlRuleFlowProcessDumper INSTANCE = new XmlRuleFlowProcessDumper();
    
    private XmlRuleFlowProcessDumper() {
        super(
            "RuleFlow", 
            "http://drools.org/drools-5.0/process",
            "drools-processes-5.0.xsd", 
            new ProcessSemanticModule()
        );
    }
    
}
