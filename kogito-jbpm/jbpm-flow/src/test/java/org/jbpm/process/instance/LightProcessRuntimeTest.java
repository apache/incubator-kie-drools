package org.jbpm.process.instance;

import java.util.Collections;

import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LightProcessRuntimeTest {

    static class MyProcess {
        String result;
        RuleFlowProcess process;
        {
            RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("org.kie.api2.MyProcessUnit");
            factory
                    // Header
                    .name("HelloWorldProcess")
                    .version("1.0")
                    .packageName("org.jbpm")
                    // Nodes
                    .startNode(1).name("Start").done()
                    .actionNode(2).name("Action")
                    .action(ctx -> {
                        result = "Hello!";
                    }).done()
                    .endNode(3).name("End").done()
                    // Connections
                    .connection(1, 2)
                    .connection(2, 3);
            process = factory.validate().getProcess();
        }
    }
    @Test
    public void testInstantiation() {
        LightProcessRuntimeServiceProvider services =
                new LightProcessRuntimeServiceProvider();

        MyProcess myProcess = new MyProcess();
        LightProcessRuntimeContext rtc = new LightProcessRuntimeContext(
                Collections.singletonList(myProcess.process));

        LightProcessRuntime rt = new LightProcessRuntime(rtc, services);

        rt.startProcess(myProcess.process.getId());

        assertEquals("Hello!", myProcess.result);

    }

}
