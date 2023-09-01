package org.drools.scenariosimulation.backend.fluent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.drools.scenariosimulation.backend.runner.model.ValueWrapper;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

public class RuleStatelessScenarioExecutableBuilder implements RuleScenarioExecutableBuilder {

    private final KieContainer kieContainer;
    private final String sessionName;
    private final List<Object> inputs = new ArrayList<>();
    private final Map<FactIdentifier, List<FactCheckerHandle>> internalConditions = new HashMap<>();
    private final KieCommands commands = KieServices.get().getCommands();

    private String agendaGroup;

    protected RuleStatelessScenarioExecutableBuilder(KieContainer kieContainer, String sessionName) {
        this.kieContainer = kieContainer;
        this.sessionName = sessionName;
    }

    @Override
    public void addInternalCondition(Class<?> clazz,
                                     Function<Object, ValueWrapper> checkFunction,
                                     ScenarioResult scenarioResult) {
        internalConditions.computeIfAbsent(scenarioResult.getFactIdentifier(), key -> new ArrayList<>())
                .add(new FactCheckerHandle(clazz, checkFunction, scenarioResult));
    }

    @Override
    public void setActiveAgendaGroup(String agendaGroup) {
        this.agendaGroup = agendaGroup;
    }

    @Override
    public void setActiveRuleFlowGroup(String ruleFlowGroup) {
        this.agendaGroup = ruleFlowGroup;
    }

    @Override
    public void insert(Object element) {
        inputs.add(element);
    }

    @Override
    public Map<String, Object> run() {
        StatelessKieSession statelessKieSession = kieContainer.newStatelessKieSession(sessionName);

        CoverageAgendaListener coverageAgendaListener = new CoverageAgendaListener();

        statelessKieSession.execute(generateCommands(coverageAgendaListener));

        Map<String, Object> toReturn = new HashMap<>();

        toReturn.put(COVERAGE_LISTENER, coverageAgendaListener);
        toReturn.put(RULES_AVAILABLE, getAvailableRules(statelessKieSession.getKieBase(), agendaGroup));
        return toReturn;
    }

    protected Command<ExecutionResults> generateCommands(CoverageAgendaListener coverageAgendaListener) {

        List<Command<?>> toReturn = new ArrayList<>();

        toReturn.add(new AddCoverageListenerCommand(coverageAgendaListener));

        if (agendaGroup != null) {
            toReturn.add(commands.newAgendaGroupSetFocus(agendaGroup));
        }

        if (!inputs.isEmpty()) {
            toReturn.add(commands.newInsertElements(inputs));
        }
        toReturn.add(commands.newFireAllRules());
        internalConditions.values()
                .forEach(factToCheck -> toReturn.add(new ValidateFactCommand(factToCheck)));

        return commands.newBatchExecution(toReturn);
    }
}
