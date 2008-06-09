package org.drools.xml;

import java.io.InputStreamReader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.drools.ruleflow.core.RuleFlowProcess;

public class TestXml extends TestCase {

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
