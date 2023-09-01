package org.drools.ruleunits.impl;

import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.drools.ruleunits.impl.listener.TestAgendaEventListener;
import org.drools.ruleunits.impl.listener.TestRuleEventListener;
import org.drools.ruleunits.impl.listener.TestRuleRuntimeEventListener;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InterpretedRuleUnitTest {

    @Test
    public void helloWorldInterpreted() {
        HelloWorldUnit unit = new HelloWorldUnit();
        unit.getStrings().add("Hello World");

        try ( RuleUnitInstance<HelloWorldUnit> unitInstance = InterpretedRuleUnit.instance(unit) ) {
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("it worked!");
        }
    }

    @Test
    public void addEventListeners() {
        TestAgendaEventListener testAgendaEventListener = new TestAgendaEventListener();
        TestRuleRuntimeEventListener testRuleRuntimeEventListener = new TestRuleRuntimeEventListener();
        TestRuleEventListener testRuleEventListener = new TestRuleEventListener();

        RuleConfig ruleConfig = RuleUnitProvider.get().newRuleConfig();
        ruleConfig.getAgendaEventListeners().add(testAgendaEventListener);
        ruleConfig.getRuleRuntimeListeners().add(testRuleRuntimeEventListener);
        ruleConfig.getRuleEventListeners().add(testRuleEventListener);

        HelloWorldUnit unit = new HelloWorldUnit();
        unit.getStrings().add("Hello World");

        try (RuleUnitInstance<HelloWorldUnit> unitInstance = InterpretedRuleUnit.instance(unit, ruleConfig)) {
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("it worked!");
            assertThat(testAgendaEventListener.getResults()).containsExactly("matchCreated : HelloWorld", "beforeMatchFired : HelloWorld", "afterMatchFired : HelloWorld");
            assertThat(testRuleRuntimeEventListener.getResults()).containsExactly("objectInserted : Hello World");
            assertThat(testRuleEventListener.getResults()).containsExactly("onBeforeMatchFire : HelloWorld", "onAfterMatchFire : HelloWorld");
        }
    }

    @Test
    public void fireWithAgendaFilter() {
        RuleNameUnit unit = new RuleNameUnit();
        unit.getStrings().add("Hello World");

        try (RuleUnitInstance<RuleNameUnit> unitInstance = InterpretedRuleUnit.instance(unit)) {
            assertThat(unitInstance.fire(new RuleNameStartsWithAgendaFilter("GoodBye"))).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("GoodByeWorld");
        }
    }
}
