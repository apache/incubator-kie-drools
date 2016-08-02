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
package org.optaplanner.core.impl.score.director.drools.testgen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.util.SortedSet;
import java.util.TreeSet;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.impl.score.director.drools.testgen.fact.TestGenFact;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestGenTestWriter {

    private static final Logger log = LoggerFactory.getLogger(TestGenTestWriter.class);
    private final TestGenKieSessionJournal journal;
    private final StringBuilder sb;

    public TestGenTestWriter(TestGenKieSessionJournal journal) {
        this.journal = journal;
        this.sb = new StringBuilder(1 << 15); // 2^15 initial capacity
    }

    static void print(TestGenKieSessionJournal journal, File testFile) {
        new TestGenTestWriter(journal).print(testFile);
    }

    private void print(File testFile) {
        printInit();
        printSetup();
        printTest();
        writeTestFile(testFile);
    }

    private void printInit() {
        String domainPackage = null;
        for (TestGenFact fact : journal.getFacts()) {
            Class<? extends Object> factClass = fact.getInstance().getClass();
            for (Annotation ann : factClass.getAnnotations()) {
                if (PlanningEntity.class.equals(ann.annotationType())) {
                    domainPackage = factClass.getPackage().getName();
                }
                break;
            }
            if (domainPackage != null) {
                break;
            }
        }

        if (domainPackage == null) {
            throw new IllegalStateException("Cannot determine planning domain package.");
        }

        sb.append(String.format("package %s;%n%n", domainPackage));
        SortedSet<String> imports = new TreeSet<String>();
        imports.add("org.junit.Before");
        imports.add("org.junit.Test");
        imports.add("org.kie.api.KieServices");
        imports.add("org.kie.api.builder.KieFileSystem");
        imports.add("org.kie.api.builder.model.KieModuleModel");
        imports.add("org.kie.api.io.ResourceType");
        imports.add("org.kie.api.runtime.KieContainer");
        imports.add("org.kie.api.runtime.KieSession");
        for (TestGenFact fact : journal.getFacts()) {
            for (Class<?> cls : fact.getImports()) {
                if (!cls.getPackage().getName().equals(domainPackage)) {
                    imports.add(cls.getCanonicalName());
                }
            }
        }

        for (String cls : imports) {
            sb.append(String.format("import %s;%n", cls));
        }
        sb.append(System.lineSeparator())
                .append("public class DroolsReproducerTest {").append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("    KieSession kieSession;").append(System.lineSeparator());
        for (TestGenFact fact : journal.getFacts()) {
            fact.printInitialization(sb);
        }
        sb.append(System.lineSeparator());
    }

    private void printSetup() {
        sb
                .append("    @Before").append(System.lineSeparator())
                .append("    public void setUp() {").append(System.lineSeparator())
                .append("        KieServices kieServices = KieServices.Factory.get();").append(System.lineSeparator())
                .append("        KieModuleModel kieModuleModel = kieServices.newKieModuleModel();").append(System.lineSeparator())
                .append("        KieFileSystem kfs = kieServices.newKieFileSystem();").append(System.lineSeparator())
                .append("        kfs.writeKModuleXML(kieModuleModel.toXML());").append(System.lineSeparator())
                // TODO don't hard-code score DRL
                .append("        kfs.write(kieServices.getResources()").append(System.lineSeparator())
                .append("                .newClassPathResource(\"org/optaplanner/examples/nurserostering/solver/nurseRosteringScoreRules.drl\")").append(System.lineSeparator())
                .append("                .setResourceType(ResourceType.DRL));").append(System.lineSeparator())
                .append("        kieServices.newKieBuilder(kfs).buildAll();").append(System.lineSeparator())
                .append("        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());").append(System.lineSeparator())
                .append("        kieSession = kieContainer.newKieSession();").append(System.lineSeparator())
                .append(System.lineSeparator());
        for (TestGenFact fact : journal.getFacts()) {
            fact.printSetup(sb);
        }
        sb.append(System.lineSeparator());
        for (TestGenKieSessionOperation insert : journal.getInitialInserts()) {
            insert.print(sb);
        }
        sb.append("    }")
                .append(System.lineSeparator())
                .append(System.lineSeparator());
    }

    private void printTest() {
        sb
                .append("    @Test").append(System.lineSeparator())
                .append("    public void test() {").append(System.lineSeparator());
        for (TestGenKieSessionOperation op : journal.getMoveOperations()) {
            op.print(sb);
        }
        sb
                .append("    }").append(System.lineSeparator())
                .append("}").append(System.lineSeparator());
    }

    private void writeTestFile(File file) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException ex) {
            log.error("Cannot open test file: " + file.toString(), ex);
            return;
        }
        OutputStreamWriter out;
        try {
            out = new OutputStreamWriter(fos, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log.error("Can't open", ex);
            return;
        }
        try {
            out.append(sb);
        } catch (IOException ex) {
            log.error("Can't write", ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                log.error("Can't close", ex);
            }
        }
    }
}
