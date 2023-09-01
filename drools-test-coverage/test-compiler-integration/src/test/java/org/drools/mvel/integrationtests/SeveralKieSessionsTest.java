/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.command.Command;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests evaluation of a backward chaining family relationships example using
 * several KieSessions.
 */
@RunWith(Parameterized.class)
public class SeveralKieSessionsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public SeveralKieSessionsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    // DROOLS-145

    private static final String PACKAGE = SeveralKieSessionsTest.class.getPackage().getName();
    private static final String PACKAGE_PATH = PACKAGE.replaceAll("\\.", "/");
    private static final String DRL_FILE_NAME = "several_kie_sessions.drl";

    private ReleaseId kieModuleId;

    @Before
    public void init() {
        kieModuleId = prepareKieModule();
    }

    /**
     * Tests evaluation of a backward chaining family relationships example with
     * two KieSessions created from the same KieBase.
     *
     * KieSessions are constructed using different KieContainer instances.
     */
    @Test
    public void testFamilyWithTwoKieSessionsFromKieContainer() throws Exception {
        final KieServices ks = KieServices.Factory.get();

        final KieContainer kieContainer = ks.newKieContainer(kieModuleId);
        final KieSession ksession = kieContainer.newKieSession();
        performTestAndDispose(ksession);

        final KieContainer kieContainerOther = ks.newKieContainer(kieModuleId);
        final KieSession ksessionOther = kieContainerOther.newKieSession();
        performTestAndDispose(ksessionOther);
    }

    /**
     * Inserts a new KieModule containing single KieBase and two KieSession
     * instances into KieRepository.
     *
     * @return created module ReleaseId
     */
    private ReleaseId prepareKieModule() {
        final KieServices ks = KieServices.Factory.get();
        final ReleaseId releaseId = ks.newReleaseId("org.drools.compiler",
                                                    "severalKieSessionsTest", "1.0.0");

        KieModuleModel module = ks.newKieModuleModel();

        KieBaseModel baseModel = module.newKieBaseModel("defaultKBase");
        baseModel.setDefault(true);
        baseModel.addPackage("*");
        baseModel.newKieSessionModel("defaultKSession").setDefault(true);

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(module.toXML());
        kfs.generateAndWritePomXML(releaseId);

        kfs.write("src/main/resources/" + PACKAGE_PATH + "/" + DRL_FILE_NAME,
                  ResourceFactory.newClassPathResource(DRL_FILE_NAME, this.getClass()));

        final KieBuilder builder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);
        assertThat(builder.getResults().getMessages().size()).isEqualTo(0);

        ks.getRepository().addKieModule(builder.getKieModule());

        return releaseId;
    }

    /**
     * Performs test on given KieSession and disposes the session afterwards.
     */
    private void performTestAndDispose(final KieSession ksession) throws Exception {
        try {
            performTest(ksession);
        } finally {
            ksession.dispose();
        }
    }

    /**
     * Performs the actual test on given KieSession.
     */
    private void performTest(final KieSession ksession) throws Exception {
        final KieBase kbase = ksession.getKieBase();

        // fact types
        FactType manType = kbase.getFactType(PACKAGE, "Man");
        FactType womanType = kbase.getFactType(PACKAGE, "Woman");
        FactType parentType = kbase.getFactType(PACKAGE, "Parent");

        // ksession.addEventListener(arg0)

        // create working memory objects
        List<Command<?>> commands = new ArrayList<Command<?>>();

        ListHolder listHolder = new ListHolder();
        commands.add(CommandFactory.newInsert(listHolder));

        // parents

        Object parent1 = parentType.newInstance();
        parentType.set(parent1, "parent", "Eva");
        parentType.set(parent1, "child", "Abel");
        commands.add(CommandFactory.newInsert(parent1));

        Object parent2 = parentType.newInstance();
        parentType.set(parent2, "parent", "Eva");
        parentType.set(parent2, "child", "Kain");
        commands.add(CommandFactory.newInsert(parent2));

        Object parent3 = parentType.newInstance();
        parentType.set(parent3, "parent", "Adam");
        parentType.set(parent3, "child", "Abel");
        commands.add(CommandFactory.newInsert(parent3));

        Object parent4 = parentType.newInstance();
        parentType.set(parent4, "parent", "Adam");
        parentType.set(parent4, "child", "Kain");
        commands.add(CommandFactory.newInsert(parent4));

        Object parent5 = parentType.newInstance();
        parentType.set(parent5, "parent", "Abel");
        parentType.set(parent5, "child", "Josef");
        commands.add(CommandFactory.newInsert(parent5));

        // persons

        Object adam = manType.newInstance();
        manType.set(adam, "name", "Adam");
        commands.add(CommandFactory.newInsert(adam));

        Object eva = womanType.newInstance();
        womanType.set(eva, "name", "Eva");
        womanType.set(eva, "age", 101);
        commands.add(CommandFactory.newInsert(eva));

        Object abel = manType.newInstance();
        manType.set(abel, "name", "Abel");
        commands.add(CommandFactory.newInsert(abel));

        Object kain = manType.newInstance();
        manType.set(kain, "name", "Kain");
        commands.add(CommandFactory.newInsert(kain));

        Object josef = manType.newInstance();
        manType.set(josef, "name", "Josef");
        commands.add(CommandFactory.newInsert(josef));

        // fire all rules
        commands.add(CommandFactory.newFireAllRules());
        ksession.execute(CommandFactory.newBatchExecution(commands));

        // asserts
        List<String> manList = listHolder.getManList();
        assertThat(4).isEqualTo(manList.size());
        assertThat(manList.contains("Adam")).isTrue();
        assertThat(manList.contains("Kain")).isTrue();
        assertThat(manList.contains("Abel")).isTrue();
        assertThat(manList.contains("Josef")).isTrue();

        List<String> personList = listHolder.getPersonList();
        assertThat(5).isEqualTo(personList.size());
        assertThat(personList.contains("Adam")).isTrue();
        assertThat(personList.contains("Kain")).isTrue();
        assertThat(personList.contains("Abel")).isTrue();
        assertThat(personList.contains("Josef")).isTrue();
        assertThat(personList.contains("Eva")).isTrue();

        List<String> parentList = listHolder.getParentList();
        assertThat(5).isEqualTo(parentList.size());
        assertThat(parentList.contains("Adam")).isTrue();
        assertThat(parentList.contains("Eva")).isTrue();
        assertThat(parentList.contains("Abel")).isTrue();

        List<String> motherList = listHolder.getMotherList();
        assertThat(2).isEqualTo(motherList.size());
        assertThat(motherList.contains("Eva")).isTrue();

        List<String> fatherList = listHolder.getFatherList();
        assertThat(3).isEqualTo(fatherList.size());
        assertThat(fatherList.contains("Adam")).isTrue();
        assertThat(fatherList.contains("Abel")).isTrue();
        assertThat(fatherList.contains("Eva")).isFalse();
        assertThat(fatherList.contains("Kain")).isFalse();
        assertThat(fatherList.contains("Josef")).isFalse();

        List<String> grandparentList = listHolder.getGrandparentList();
        assertThat(2).isEqualTo(grandparentList.size());
        assertThat(grandparentList.contains("Eva")).isTrue();
        assertThat(grandparentList.contains("Adam")).isTrue();

        assertThat(listHolder.isGrandmaBlessedAgeTriggered()).isTrue();
    }

    /**
     * Static class to store results from the working memory.
     */
    public static class ListHolder implements Serializable {

        private static final long serialVersionUID = -3058814255413392428L;
        private List<String> things;
        private List<String> food;
        private List<String> exits;
        private List<String> manList;
        private List<String> personList;
        private List<String> parentList;
        private List<String> motherList;
        private List<String> fatherList;
        private List<String> grandparentList;
        private boolean grandmaBlessedAgeTriggered;

        ListHolder() {
            things = new ArrayList<String>();
            food = new ArrayList<String>();
            exits = new ArrayList<String>();
            manList = new ArrayList<String>();
            personList = new ArrayList<String>();
            parentList = new ArrayList<String>();
            motherList = new ArrayList<String>();
            fatherList = new ArrayList<String>();
            grandparentList = new ArrayList<String>();
            grandmaBlessedAgeTriggered = false;
        }

        public void setThings(List<String> things) {
            this.things = things;
        }

        public List<String> getThings() {
            return things;
        }

        public void setFood(List<String> food) {
            this.food = food;
        }

        public List<String> getFood() {
            return food;
        }

        public void setExits(List<String> exits) {
            this.exits = exits;
        }

        public List<String> getExits() {
            return exits;
        }

        public void setManList(List<String> manList) {
            this.manList = manList;
        }

        public List<String> getManList() {
            return manList;
        }

        public void setPersonList(List<String> PersonList) {
            personList = PersonList;
        }

        public List<String> getPersonList() {
            return personList;
        }

        public void setParentList(List<String> parentList) {
            this.parentList = parentList;
        }

        public List<String> getParentList() {
            return parentList;
        }

        public void setMotherList(List<String> motherList) {
            this.motherList = motherList;
        }

        public List<String> getMotherList() {
            return motherList;
        }

        public void setGrandparentList(List<String> grandparentList) {
            this.grandparentList = grandparentList;
        }

        public List<String> getGrandparentList() {
            return grandparentList;
        }

        public void setGrandmaBlessedAgeTriggered(boolean grandmaBlessedAgeTriggered) {
            this.grandmaBlessedAgeTriggered = grandmaBlessedAgeTriggered;
        }

        public boolean isGrandmaBlessedAgeTriggered() {
            return grandmaBlessedAgeTriggered;
        }

        public void setFatherList(List<String> fatherList) {
            this.fatherList = fatherList;
        }

        public List<String> getFatherList() {
            return fatherList;
        }
    }
}
