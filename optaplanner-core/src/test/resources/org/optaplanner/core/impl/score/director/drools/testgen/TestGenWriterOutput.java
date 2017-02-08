package org.optaplanner.testgen;

import java.io.File;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.optaplanner.core.api.score.holder.ScoreHolder;
import org.optaplanner.core.impl.score.buildin.simple.SimpleScoreDefinition;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

public class TestGenWriterOutput {

    KieContainer kieContainer;
    KieSession kieSession;
    ScoreHolder scoreHolder = new SimpleScoreDefinition().buildScoreHolder(true);
    private final String string_0 = new String();
    private final TestdataEntity testdataEntity_1 = new TestdataEntity();
    private final TestdataValue testdataValue_2 = new TestdataValue();

    @Before
    public void setUp() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.write(kieServices.getResources()
                .newFileSystemResource(new File("SCORE_DRL_ABSOLUTE_PATH"), "UTF-8"));
        kfs.write(kieServices.getResources()
                .newClassPathResource("x"));
        kfs.write(kieServices.getResources()
                .newClassPathResource("y"));
        kieServices.newKieBuilder(kfs).buildAll();
        kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        kieSession = kieContainer.newKieSession();

        kieSession.setGlobal("scoreHolder", scoreHolder);

        //abc
        //E
        testdataEntity_1.setCode("E");
        //V
        testdataValue_2.setCode("V");

        //operation I #0
        kieSession.insert(testdataValue_2);
    }

    @Test
    public void test() {
        //operation U #1
        testdataEntity_1.setValue(testdataValue_2);
        kieSession.update(kieSession.getFactHandle(testdataEntity_1), testdataEntity_1, "value");
        //operation D #2
        kieSession.delete(kieSession.getFactHandle(testdataValue_2));
        //operation F #3
        kieSession.fireAllRules();
        // This is the corrupted score, just to make sure the bug is reproducible
        Assert.assertEquals("1", scoreHolder.extractScore(0).toString());
        kieSession = kieContainer.newKieSession();
        scoreHolder = new SimpleScoreDefinition().buildScoreHolder(true);
        kieSession.setGlobal("scoreHolder", scoreHolder);


        // Insert everything into a fresh session to see the uncorrupted score
        //operation I #0
        kieSession.insert(testdataValue_2);
        kieSession.fireAllRules();
        Assert.assertEquals("0", scoreHolder.extractScore(0).toString());
    }
}
