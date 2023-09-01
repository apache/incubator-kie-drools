package org.drools.testcoverage.regression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.model.ListHolder;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.kie.api.command.Command;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.testcoverage.common.util.KieUtil.getCommands;

/**
 * Test to verify that BRMS-580 is fixed. NPE when trying to update fact with
 * rules with different saliences.
 */
public class MultipleSalienceUpdateFactTest extends KieSessionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultipleSalienceUpdateFactTest.class);

    private static final String DRL_FILE = "BRMS-580.drl";

    public MultipleSalienceUpdateFactTest(final KieBaseTestConfiguration kieBaseTestConfiguration,
                                          final KieSessionTestConfiguration kieSessionTestConfiguration) {
        super(kieBaseTestConfiguration, kieSessionTestConfiguration);
    }

    @Parameterized.Parameters(name = "{1}" + " (from " + "{0}" + ")")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseAndStatefulKieSessionConfigurations();
    }

    @Test
    public void test() {
        session.setGlobal("LOGGER", LOGGER);
        List<Command<?>> commands = new ArrayList<Command<?>>();

        Person person = new Person("PAUL");

        ListHolder listHolder = new ListHolder();
        List<String> list = Arrays.asList("eins", "zwei", "drei");
        listHolder.setList(list);

        commands.add(getCommands().newInsert(person));
        commands.add(getCommands().newInsert(listHolder));
        commands.add(getCommands().newFireAllRules());

        session.execute(getCommands().newBatchExecution(commands, null));

        assertThat(firedRules.isRuleFired("PERSON_PAUL")).isTrue();
        assertThat(firedRules.isRuleFired("PERSON_PETER")).isTrue();
    }

    @Override
    protected Resource[] createResources() {
        return KieUtil.createResources(DRL_FILE, MultipleSalienceUpdateFactTest.class);
    }
}
