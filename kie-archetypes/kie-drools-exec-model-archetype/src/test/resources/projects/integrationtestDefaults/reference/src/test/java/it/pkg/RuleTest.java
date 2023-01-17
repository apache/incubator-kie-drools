
package it.pkg;

import java.util.HashSet;
import java.util.Set;

import org.drools.model.codegen.ExecutableModelProject;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RuleTest {
    static final Logger LOG = LoggerFactory.getLogger(RuleTest.class);

    @Test
    public void test() {
        KieContainer kContainer = createKieContainer();

        LOG.info("Creating kieBase");
        KieBase kieBase = kContainer.getKieBase();

        LOG.info("There should be rules: ");
        for ( KiePackage kp : kieBase.getKiePackages() ) {
            for (Rule rule : kp.getRules()) {
                LOG.info("kp " + kp + " rule " + rule.getName());
            }
        }

        LOG.info("Creating kieSession");
        KieSession session = kieBase.newKieSession();

        try {
            LOG.info("Populating globals");
            Set<String> check = new HashSet<String>();
            session.setGlobal("controlSet", check);

            LOG.info("Now running data");

            Measurement mRed = new Measurement("color", "red");
            session.insert(mRed);
            session.fireAllRules();

            Measurement mGreen = new Measurement("color", "green");
            session.insert(mGreen);
            session.fireAllRules();

            Measurement mBlue = new Measurement("color", "blue");
            session.insert(mBlue);
            session.fireAllRules();

            LOG.info("Final checks");

            assertEquals("Size of object in Working Memory is 3", 3, session.getObjects().size());
            assertTrue("contains red", check.contains("red"));
            assertTrue("contains green", check.contains("green"));
            assertTrue("contains blue", check.contains("blue"));
        } finally {
            session.dispose();
        }
    }

    private KieContainer createKieContainer() {
        // Programmatically collect resources and build a KieContainer
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        String packagePath = "it.pkg".replace(".", "/");
        kfs.write("src/main/resources/" + packagePath + "/rules.drl",
                  ks.getResources().newInputStreamResource(this.getClass().getClassLoader().getResourceAsStream(packagePath + "/rules.drl")));
        ReleaseId releaseId = ks.newReleaseId("archetype.it", "basic", "0.1-SNAPSHOT");
        kfs.generateAndWritePomXML(releaseId);
        ks.newKieBuilder(kfs).buildAll(ExecutableModelProject.class);
        return ks.newKieContainer(releaseId);
    }
}