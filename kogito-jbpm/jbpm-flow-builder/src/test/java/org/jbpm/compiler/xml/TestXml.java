package org.jbpm.compiler.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import org.drools.core.xml.SemanticModules;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;
import org.kie.api.definition.process.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestXml extends AbstractBaseTest {
    
    private static Logger logger = LoggerFactory.getLogger(TestXml.class);

    @Test
    public void testSimpleXml() throws Exception {
        SemanticModules modules = new SemanticModules();
        modules.addSemanticModule(new ProcessSemanticModule());
        XmlProcessReader reader = new XmlProcessReader(modules, getClass().getClassLoader());
        reader.read(new InputStreamReader(TestXml.class.getResourceAsStream("XmlTest.xml")));
        List<Process> processes = reader.getProcess();
        assertNotNull(processes);
        assertEquals(1, processes.size());
        RuleFlowProcess process = (RuleFlowProcess) processes.get(0);
        assertNotNull(process);

        String output = XmlRuleFlowProcessDumper.INSTANCE.dump(process);
        logger.info(output);
        reader = new XmlProcessReader(new SemanticModules(), getClass().getClassLoader());
        reader.read(new StringReader(output));
    }
}
