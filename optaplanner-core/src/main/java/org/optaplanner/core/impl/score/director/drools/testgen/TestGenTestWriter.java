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
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.score.director.drools.testgen.fact.TestGenFact;
import org.optaplanner.core.impl.score.director.drools.testgen.operation.TestGenKieSessionOperation;
import org.optaplanner.core.impl.score.director.drools.testgen.reproducer.TestGenCorruptedScoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestGenTestWriter {

    private static final Logger logger = LoggerFactory.getLogger(TestGenTestWriter.class);
    private StringBuilder sb;
    private TestGenKieSessionJournal journal;
    private List<String> scoreDrlList;
    private List<File> scoreDrlFileList;
    private ScoreDefinition<?> scoreDefinition;
    private boolean constraintMatchEnabled;
    private TestGenCorruptedScoreException scoreEx;

    public void print(TestGenKieSessionJournal journal, File testFile) {
        this.journal = journal;
        this.sb = new StringBuilder(1 << 15); // 2^15 initial capacity
        printInit();
        printSetup();
        printTest();
        writeTestFile(testFile);
    }

    private void printInit() {
        sb.append("package org.optaplanner.testgen;").append(System.lineSeparator())
                .append(System.lineSeparator());
        SortedSet<String> imports = new TreeSet<>();
        imports.add("org.junit.Before");
        imports.add("org.junit.Test");
        imports.add("org.kie.api.KieServices");
        imports.add("org.kie.api.builder.KieFileSystem");
        imports.add("org.kie.api.runtime.KieContainer");
        imports.add("org.kie.api.runtime.KieSession");
        if (!scoreDrlFileList.isEmpty()) {
            imports.add("java.io.File");
        }
        if (scoreDefinition != null) {
            imports.add("org.junit.Assert");
            imports.add(ScoreHolder.class.getCanonicalName());
            imports.add(scoreDefinition.getClass().getCanonicalName());
        }
        for (TestGenFact fact : journal.getFacts()) {
            for (Class<?> cls : fact.getImports()) {
                String pkgName = cls.getPackage().getName();
                if (!pkgName.equals("java.lang")) {
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
                .append("    KieContainer kieContainer;").append(System.lineSeparator())
                .append("    KieSession kieSession;").append(System.lineSeparator());
        if (scoreDefinition != null) {
            sb
                    .append("    ScoreHolder scoreHolder = new ")
                    .append(scoreDefinition.getClass().getSimpleName())
                    .append("().buildScoreHolder(")
                    .append(constraintMatchEnabled)
                    .append(");").append(System.lineSeparator());
        }

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
                .append("        KieFileSystem kfs = kieServices.newKieFileSystem();").append(System.lineSeparator());
        scoreDrlFileList.forEach(file -> {
            sb
                    .append("        kfs.write(kieServices.getResources()").append(System.lineSeparator())
                    .append("                .newFileSystemResource(new File(\"").append(file.getAbsoluteFile())
                    .append("\"), \"UTF-8\"));").append(System.lineSeparator());
        });
        scoreDrlList.forEach(drl -> {
            sb
                    .append("        kfs.write(kieServices.getResources()").append(System.lineSeparator())
                    .append("                .newClassPathResource(\"").append(drl).append("\"));")
                    .append(System.lineSeparator());
        });
        sb
                .append("        kieServices.newKieBuilder(kfs).buildAll();").append(System.lineSeparator())
                .append("        kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());").append(System.lineSeparator())
                .append("        kieSession = kieContainer.newKieSession();").append(System.lineSeparator())
                .append(System.lineSeparator());
        if (scoreDefinition != null) {
            sb.append("        kieSession.setGlobal(\"").append(DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY)
                    .append("\", scoreHolder);")
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
        if (scoreEx != null) {
            sb
                    .append("        // This is the corrupted score, just to make sure the bug is reproducible")
                    .append(System.lineSeparator())
                    .append("        Assert.assertEquals(\"").append(scoreEx.getWorkingScore())
                    .append("\", scoreHolder.extractScore(0).toString());").append(System.lineSeparator());
            // demonstrate the uncorrupted score
            sb
                    .append("        kieSession = kieContainer.newKieSession();").append(System.lineSeparator())
                    .append("        scoreHolder = new ").append(scoreDefinition.getClass().getSimpleName())
                    .append("().buildScoreHolder(").append(constraintMatchEnabled).append(");").append(System.lineSeparator())
                    .append("        kieSession.setGlobal(\"").append(DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY)
                    .append("\", scoreHolder);").append(System.lineSeparator());

            sb.append(System.lineSeparator()).append(System.lineSeparator())
                    .append("        // Insert everything into a fresh session to see the uncorrupted score")
                    .append(System.lineSeparator());
            for (TestGenKieSessionOperation insert : journal.getInitialInserts()) {
                insert.print(sb);
            }
            sb
                    .append("        kieSession.fireAllRules();").append(System.lineSeparator())
                    .append("        Assert.assertEquals(\"").append(scoreEx.getUncorruptedScore())
                    .append("\", scoreHolder.extractScore(0).toString());").append(System.lineSeparator());
        }
        sb
                .append("    }").append(System.lineSeparator())
                .append("}").append(System.lineSeparator());
    }

    private void writeTestFile(File file) {
        File parent = file.getAbsoluteFile().getParentFile();
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                logger.warn("Couldn't create directory: {}", parent);
            }
        }
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

    public void setScoreDrlList(List<String> scoreDrlList) {
        this.scoreDrlList = scoreDrlList == null ? Collections.emptyList() : scoreDrlList;
    }

    public void setScoreDrlFileList(List<File> scoreDrlFileList) {
        this.scoreDrlFileList = scoreDrlFileList == null ? Collections.emptyList() : scoreDrlFileList;
    }

    public void setScoreDefinition(ScoreDefinition<?> scoreDefinition) {
        this.scoreDefinition = scoreDefinition;
    }

    public void setConstraintMatchEnabled(boolean constraintMatchEnabled) {
        this.constraintMatchEnabled = constraintMatchEnabled;
    }

    public void setCorruptedScoreException(TestGenCorruptedScoreException ex) {
        this.scoreEx = ex;
    }

}
