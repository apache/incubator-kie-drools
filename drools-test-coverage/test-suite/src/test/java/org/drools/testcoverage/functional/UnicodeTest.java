/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.functional;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.Resource;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.builder.DecisionTableInputType;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tests Drools engine capabilities regarding Unicode characters
 * 
 */
public class UnicodeTest {

    @Test
    public void testJapanese() {
        final KieServices kieServices = KieServices.Factory.get();
        final Resource resource = kieServices.getResources().newClassPathResource("unicode.drl", getClass());
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, resource);
        final KieSession ksession = kbase.newKieSession();

        final List<Command<?>> commands = new ArrayList<Command<?>>();

        List<人> 一覧 = new ArrayList<人>();
        commands.add(kieServices.getCommands().newSetGlobal("一覧", 一覧));

        // let's create person yokozuna
        final 人 横綱 = new 人();
        横綱.set歳(30);
        横綱.setの名前("横綱");
        横綱.set既婚(true);
        commands.add(kieServices.getCommands().newInsert(横綱));

        commands.add(kieServices.getCommands().newFireAllRules("firedRulesCount"));
        final ExecutionResults results = ksession.execute(kieServices.getCommands().newBatchExecution(commands, null));

        Assertions.assertThat(results.getValue("firedRulesCount")).isEqualTo(2);
        Assertions.assertThat(一覧.size()).isEqualTo(1);
        Assertions.assertThat(一覧.iterator().next().getの名前()).isEqualTo("横綱");
    }

    @Test
    public void testCzech() {
        final KieServices kieServices = KieServices.Factory.get();
        final Resource resource = kieServices.getResources().newClassPathResource("unicode.drl", getClass());
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, resource);
        final KieSession ksession = kbase.newKieSession();

        final List<Command<?>> commands = new ArrayList<Command<?>>();
        final List<Člověk> lidé = new ArrayList<Člověk>();
        commands.add(kieServices.getCommands().newSetGlobal("lidé", lidé));
        Člověk Řehoř = new Člověk();
        Řehoř.setVěk(30);
        Řehoř.setJméno("Řehoř");
        commands.add(kieServices.getCommands().newInsert(Řehoř));
        commands.add(kieServices.getCommands().newFireAllRules());
        ksession.execute(kieServices.getCommands().newBatchExecution(commands, null));

        Assertions.assertThat(kbase.getRule(TestConstants.PACKAGE_FUNCTIONAL, "příliš žluťoučký kůň úpěl ďábelské ódy")).isNotNull();

        Map<String, Object> metaData = kbase.getRule(TestConstants.PACKAGE_FUNCTIONAL,
                "příliš žluťoučký kůň úpěl ďábelské ódy").getMetaData();
        Assertions.assertThat(metaData.get("PrávníPožadavek")).isEqualTo("Osoba starší osmnácti let");

        Assertions.assertThat(lidé.size()).isEqualTo(2);
        Assertions.assertThat(lidé.get(0).getJméno()).isEqualTo("Řehoř");
        Assertions.assertThat(lidé.get(1).getJméno()).isEqualTo("Oldřiška");
    }

    @Test
    public void testCzechDomainSpecificLanguage() {
        final KieServices kieServices = KieServices.Factory.get();
        final Resource dsl = kieServices.getResources().newClassPathResource("unicode.dsl", getClass());
        final Resource dslr = kieServices.getResources().newClassPathResource("unicode.dslr", getClass());
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, dsl, dslr);
        final KieSession ksession = kbase.newKieSession();

        final List<Command<?>> commands = new ArrayList<Command<?>>();
        final List<Člověk> dospělí = new ArrayList<Člověk>();
        commands.add(kieServices.getCommands().newSetGlobal("dospělí", dospělí));
        final Člověk Řehoř = new Člověk();
        Řehoř.setVěk(30);
        Řehoř.setJméno("Řehoř");
        commands.add(kieServices.getCommands().newInsert(Řehoř));
        commands.add(kieServices.getCommands().newFireAllRules());
        ksession.execute(kieServices.getCommands().newBatchExecution(commands, null));

        Assertions.assertThat(kbase.getRule(TestConstants.PACKAGE_FUNCTIONAL, "pokusné doménově specifické pravidlo")).isNotNull();

        Assertions.assertThat(dospělí.size()).isEqualTo(1);
        Assertions.assertThat(dospělí.iterator().next().getJméno()).isEqualTo("Řehoř");
    }

    @Test
    public void testCzechXLSDecisionTable() throws FileNotFoundException {
        final KieServices kieServices = KieServices.Factory.get();
        final Resource resource = kieServices.getResources().newClassPathResource("unicode.xls", getClass());
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, resource);
        final KieSession ksession = kbase.newKieSession();

        final List<Command<?>> commands = new ArrayList<Command<?>>();
        final List<Člověk> dospělí = new ArrayList<Člověk>();
        commands.add(kieServices.getCommands().newSetGlobal("dospělí", dospělí));
        final Člověk Řehoř = new Člověk();
        Řehoř.setVěk(30);
        Řehoř.setJméno("Řehoř");
        commands.add(kieServices.getCommands().newInsert(Řehoř));
        commands.add(kieServices.getCommands().newFireAllRules());

        ksession.execute(kieServices.getCommands().newBatchExecution(commands, null));
        Assertions.assertThat(kbase.getRule(TestConstants.PACKAGE_FUNCTIONAL, "přidej k dospělým")).isNotNull();

        Assertions.assertThat(dospělí.size()).isEqualTo(1);
        Assertions.assertThat(dospělí.iterator().next().getJméno()).isEqualTo("Řehoř");
    }

    @Test
    public void testCzechCSVDecisionTable() throws FileNotFoundException {
        final KieServices kieServices = KieServices.Factory.get();

        final Resource decisionTable =
                KieBaseUtil.getDecisionTableResourceFromClasspath("unicode.csv", getClass(), DecisionTableInputType.CSV);

        KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, decisionTable);
        KieSession ksession = kbase.newKieSession();

        List<Command<?>> commands = new ArrayList<Command<?>>();
        List<Člověk> dospělí = new ArrayList<Člověk>();
        commands.add(kieServices.getCommands().newSetGlobal("dospělí", dospělí));
        Člověk Řehoř = new Člověk();
        Řehoř.setVěk(30);
        Řehoř.setJméno("Řehoř");
        commands.add(kieServices.getCommands().newInsert(Řehoř));
        commands.add(kieServices.getCommands().newFireAllRules());
        ksession.execute(kieServices.getCommands().newBatchExecution(commands, null));

        Assertions.assertThat(kbase.getRule(TestConstants.PACKAGE_FUNCTIONAL, "pokusné pravidlo rozhodovací tabulky")).isNotNull();

        Assertions.assertThat(dospělí.size()).isEqualTo(1);
        Assertions.assertThat(dospělí.iterator().next().getJméno()).isEqualTo("Řehoř");
    }

    // test queries in Czech language
    @Test
    public void testQueryCallFromJava() throws InstantiationException, IllegalAccessException {
        final KieServices kieServices = KieServices.Factory.get();
        final Resource resource = kieServices.getResources().newClassPathResource("unicode.drl", getClass());
        final KieBase kbase = KieBaseUtil.getKieBaseFromResources(true, resource);
        final KieSession ksession = kbase.newKieSession();

        final FactType locationType = kbase.getFactType(TestConstants.PACKAGE_FUNCTIONAL, "Umístění");

        // a pear is in the kitchen
        final Object hruška = locationType.newInstance();
        locationType.set(hruška, "věc", "hruška");
        locationType.set(hruška, "místo", "kuchyně");

        // a desk is in the office
        final Object stůl = locationType.newInstance();
        locationType.set(stůl, "věc", "stůl");
        locationType.set(stůl, "místo", "kancelář");

        // a flashlight is on the desk
        final Object svítilna = locationType.newInstance();
        locationType.set(svítilna, "věc", "svítilna");
        locationType.set(svítilna, "místo", "stůl");

        // an envelope is on the desk
        final Object obálka = locationType.newInstance();
        locationType.set(obálka, "věc", "obálka");
        locationType.set(obálka, "místo", "stůl");

        // a key is in the envelope
        final Object klíč = locationType.newInstance();
        locationType.set(klíč, "věc", "klíč");
        locationType.set(klíč, "místo", "obálka");

        // create working memory objects
        final List<Command<?>> commands = new ArrayList<Command<?>>();

        // Location instances
        commands.add(kieServices.getCommands().newInsert(hruška));
        commands.add(kieServices.getCommands().newInsert(stůl));
        commands.add(kieServices.getCommands().newInsert(svítilna));
        commands.add(kieServices.getCommands().newInsert(obálka));
        commands.add(kieServices.getCommands().newInsert(klíč));

        // fire all rules
        final String queryAlias = "obsaženo";
        commands.add(kieServices.getCommands().newQuery(queryAlias, "jeObsažen", new Object[] { Variable.v, "kancelář" }));

        final ExecutionResults results = ksession.execute(kieServices.getCommands().newBatchExecution(commands, null));
        final QueryResults qResults = (QueryResults) results.getValue(queryAlias);

        final List<String> l = new ArrayList<String>();
        for (QueryResultsRow r : qResults) {
            l.add((String) r.get("x"));
        }

        // items in the office should be the following
        Assertions.assertThat(l.size()).isEqualTo(4);
        Assertions.assertThat(l.contains("stůl")).isTrue();
        Assertions.assertThat(l.contains("svítilna")).isTrue();
        Assertions.assertThat(l.contains("obálka")).isTrue();
        Assertions.assertThat(l.contains("klíč")).isTrue();
    }

    // japanese person
    public static class 人 {

        // age
        private int 歳;
        // name
        private String の名前;
        // married
        private boolean 既婚;

        public void set歳(final int 歳) {
            this.歳 = 歳;
        }

        public int get歳() {
            return 歳;
        }

        public void setの名前(final String の名前) {
            this.の名前 = の名前;
        }

        public String getの名前() {
            return の名前;
        }

        public boolean is既婚() {
            return 既婚;
        }

        public void set既婚(final boolean 既婚) {
            this.既婚 = 既婚;
        }
    }

    // czech person
    public static class Člověk {

        private int věk;
        private String jméno;

        public void setVěk(final int věk) {
            this.věk = věk;
        }

        public int getVěk() {
            return věk;
        }

        public void setJméno(final String jméno) {
            this.jméno = jméno;
        }

        public String getJméno() {
            return jméno;
        }
    }
}
