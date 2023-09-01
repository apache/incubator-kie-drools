package org.drools.compiler.integrationtests.noxml;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.codegen.ExecutableModelProject;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandTest {

    @Test
    public void kieCommands_notNull() {
        final String drl = "rule R\n" +
                           "when\n" +
                           "  String()\n" +
                           "then\n" +
                           "end\n";

        KieModuleModel kModuleModel = KieServices.get().newKieModuleModel();

        KieHelper kHelper = new KieHelper();

        KieBase kieBase = kHelper.setKieModuleModel(kModuleModel)
                                 .addContent(drl, ResourceType.DRL)
                                 .build(ExecutableModelProject.class);

        KieSession kieSession = kieBase.newKieSession();

        KieCommands kieCommands = KieServices.Factory.get().getCommands();
        assertThat(kieCommands).isNotNull();

        List<Command> commands = new ArrayList<>();
        commands.add(kieCommands.newInsert("test"));
        commands.add(kieCommands.newFireAllRules("fired"));
        BatchExecutionCommand batchExecutionCommand = CommandFactory.newBatchExecution(commands);

        final ExecutionResults batchResult = kieSession.execute(batchExecutionCommand);

        assertThat(batchResult.getValue("fired")).isEqualTo(1);
    }
}
