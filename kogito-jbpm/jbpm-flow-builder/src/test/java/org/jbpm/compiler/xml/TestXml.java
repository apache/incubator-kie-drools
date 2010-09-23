package org.jbpm.compiler.xml;

import java.io.InputStreamReader;
import java.io.StringReader;

import org.drools.xml.SemanticModules;
import org.jbpm.JbpmTestCase;
import org.jbpm.ruleflow.core.RuleFlowProcess;

public class TestXml extends JbpmTestCase {

    public void testSimpleXml() throws Exception {
        SemanticModules modules = new SemanticModules();
        modules.addSemanticModule(new ProcessSemanticModule());
        XmlProcessReader reader = new XmlProcessReader(modules);
        reader.read(new InputStreamReader(TestXml.class.getResourceAsStream("XmlTest.xml")));
        RuleFlowProcess process = (RuleFlowProcess) reader.getProcess();
        assertNotNull(process);

        String output = XmlRuleFlowProcessDumper.INSTANCE.dump(process);
        System.out.println(output);
        reader = new XmlProcessReader(new SemanticModules());
        reader.read(new StringReader(output));
    }
}
