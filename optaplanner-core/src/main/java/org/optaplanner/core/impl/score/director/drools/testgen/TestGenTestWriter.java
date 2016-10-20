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
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.score.director.drools.testgen.fact.TestGenFact;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestGenTestWriter {

    private static final Logger logger = LoggerFactory.getLogger(TestGenTestWriter.class);
    private final TestGenKieSessionJournal journal;
    private final StringBuilder sb;
    private final Class<?> scoreDefClass;
    private final boolean constraintMatchEnabled;
    private final String score;

    public TestGenTestWriter(
            TestGenKieSessionJournal journal,
            Class<?> scoreDefClass,
            boolean constraintMatchEnabled,
            String score) {
        this.journal = journal;
        this.sb = new StringBuilder(1 << 15); // 2^15 initial capacity
        this.scoreDefClass = scoreDefClass;
        this.constraintMatchEnabled = constraintMatchEnabled;
        this.score = score;
    }

    static void print(TestGenKieSessionJournal journal, File testFile) {
        new TestGenTestWriter(journal, null, false, null).print(testFile);
    }

    static void printWithScoreAssert(
            TestGenKieSessionJournal journal,
            Class<?> scoreDefClass,
            boolean constraintMatchEnabled,
            String score,
            File testFile) {
        new TestGenTestWriter(journal, scoreDefClass, constraintMatchEnabled, score).print(testFile);
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
        SortedSet<String> imports = new TreeSet<>();
        imports.add("org.junit.Before");
        imports.add("org.junit.Test");
        imports.add("org.kie.api.KieServices");
        imports.add("org.kie.api.builder.KieFileSystem");
        imports.add("org.kie.api.builder.model.KieModuleModel");
        imports.add("org.kie.api.io.ResourceType");
        imports.add("org.kie.api.runtime.KieContainer");
        imports.add("org.kie.api.runtime.KieSession");
        if (scoreDefClass != null) {
            imports.add("org.junit.Assert");
            imports.add(Score.class.getCanonicalName());
            imports.add(ScoreHolder.class.getCanonicalName());
            imports.add(scoreDefClass.getCanonicalName());
        }
        for (TestGenFact fact : journal.getFacts()) {
            for (Class<?> cls : fact.getImports()) {
                String pkgName = cls.getPackage().getName();
                if (!pkgName.equals(domainPackage) && !pkgName.equals("java.lang")) {
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
        if (scoreDefClass != null) {
            sb.append("        kieSession.setGlobal(\"").append(DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY)
                    .append("\", new ").append(scoreDefClass.getSimpleName()).append("().buildScoreHolder(")
                    .append(constraintMatchEnabled).append("));")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
        }
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
        if (scoreDefClass != null) {
            sb.append("        Score<?> score = ((ScoreHolder) kieSession.getGlobal(\"")
                    .append(DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY).append("\")).extractScore(0);")
                    .append(System.lineSeparator());
            sb.append("        Assert.assertEquals(\"").append(score).append("\", score.toString());")
                    .append(System.lineSeparator());
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
            logger.error("Cannot open test file: " + file.toString(), ex);
            return;
        }
        OutputStreamWriter out;
        try {
            out = new OutputStreamWriter(fos, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logger.error("Can't open", ex);
            return;
        }
        try {
            out.append(sb);
        } catch (IOException ex) {
            logger.error("Can't write", ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                logger.error("Can't close", ex);
            }
        }
    }
}
