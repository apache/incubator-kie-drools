package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.ConsequenceException;
import org.kie.internal.io.ResourceFactory;

/**
 * This is a sample class to launch a rule.
 */
@Ignore( "This test causes problems to surefire, so it will be disabled for now. It works when executed by itself.")
public class SecurityPolicyTest extends CommonTestMethodBase {

    @Before
    public void init() {
        String enginePolicy = SecurityPolicyTest.class.getResource("engine.policy").getFile();
        String kiePolicy = SecurityPolicyTest.class.getResource("rules.policy").getFile();
        System.setProperty("java.security.policy", enginePolicy);
        System.setProperty("kie.security.policy", kiePolicy);

        TestSecurityManager tsm = new TestSecurityManager();
        System.setSecurityManager(tsm);
    }

    @After
    public void close() {
        System.setSecurityManager(null);
        System.setProperty("java.security.policy", "");
        System.setProperty("kie.security.policy", "");
    }
    
    @Test
    public void testUntrustedJavaConsequence() throws Exception {
        String drl = "package org.foo.bar\n" +
                "rule R1 when\n" +
                "then\n" +
                "    System.exit(0);" +
                "end\n";

        try {
            KieServices ks = KieServices.Factory.get();
            KieFileSystem kfs = ks.newKieFileSystem().write(ResourceFactory.newByteArrayResource(drl.getBytes())
                    .setSourcePath("org/foo/bar/r1.drl"));
            ks.newKieBuilder(kfs).buildAll();

            ReleaseId releaseId = ks.getRepository().getDefaultReleaseId();
            KieContainer kc = ks.newKieContainer(releaseId);

            KieSession ksession = kc.newKieSession();
            ksession.fireAllRules();
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (ShouldHavePrevented e) {
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (ConsequenceException e) {
            // test succeeded. the policy in place prevented the rule from executing the System.exit().
        }
    }
    
    @Test
    public void testUntrustedMvelConsequence() throws Exception {
        String drl = "package org.foo.bar\n" +
                "rule R1 dialect \"mvel\" when\n" +
                "then\n" +
                "    System.exit(0);" +
                "end\n";

        try {
            KieServices ks = KieServices.Factory.get();
            KieFileSystem kfs = ks.newKieFileSystem().write(ResourceFactory.newByteArrayResource(drl.getBytes())
                    .setSourcePath("org/foo/bar/r1.drl"));
            ks.newKieBuilder(kfs).buildAll();

            ReleaseId releaseId = ks.getRepository().getDefaultReleaseId();
            KieContainer kc = ks.newKieContainer(releaseId);

            KieSession ksession = kc.newKieSession();
            ksession.fireAllRules();
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (ShouldHavePrevented e) {
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (ConsequenceException e) {
            // test succeeded. the policy in place prevented the rule from executing the System.exit().
        }
    }
    
    @Test
    public void testSerializationUntrustedMvelConsequence() throws Exception {
        String drl = "package org.foo.bar\n" +
                "rule R1 dialect \"mvel\" when\n" +
                "then\n" +
                "    System.exit(0);" +
                "end\n";

        try {
            KieServices ks = KieServices.Factory.get();
            KieFileSystem kfs = ks.newKieFileSystem().write(ResourceFactory.newByteArrayResource(drl.getBytes())
                    .setSourcePath("org/foo/bar/r1.drl"));
            ks.newKieBuilder(kfs).buildAll();

            ReleaseId releaseId = ks.getRepository().getDefaultReleaseId();
            KieContainer kc = ks.newKieContainer(releaseId);

            KieBase kbase = kc.getKieBase();
            kbase = SerializationHelper.serializeObject( kbase );
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
            // test succeeded. the policy in place prevented the rule from executing the System.exit().
        }
    }
    
    @Test
    public void testUntrustedSalience() throws Exception {
        String drl = "package org.foo.bar\n" +
                "import "+MaliciousExitHelper.class.getName().replace('$', '.')+" \n" +
                "rule R1 salience( MaliciousExitHelper.exit() ) \n" +
                "when\n" +
                "then\n" +
                "end\n";

        try {
            KieServices ks = KieServices.Factory.get();
            KieFileSystem kfs = ks.newKieFileSystem().write(ResourceFactory.newByteArrayResource(drl.getBytes())
                    .setSourcePath("org/foo/bar/r1.drl"));
            ks.newKieBuilder(kfs).buildAll();

            ReleaseId releaseId = ks.getRepository().getDefaultReleaseId();
            KieContainer kc = ks.newKieContainer(releaseId);

            KieSession ksession = kc.newKieSession();
            ksession.fireAllRules();
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (ShouldHavePrevented e) {
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (Exception e) {
            // test succeeded. the policy in place prevented the rule from executing the System.exit().
        }
    }

    @Test
    public void testUntrustedEnabled() throws Exception {
        String drl = "package org.foo.bar\n" +
                "import "+MaliciousExitHelper.class.getName().replace('$', '.')+" \n" +
                "rule R1 enabled( MaliciousExitHelper.isEnabled() ) \n" +
                "when\n" +
                "then\n" +
                "end\n";

        try {
            KieServices ks = KieServices.Factory.get();
            KieFileSystem kfs = ks.newKieFileSystem().write(ResourceFactory.newByteArrayResource(drl.getBytes())
                    .setSourcePath("org/foo/bar/r1.drl"));
            ks.newKieBuilder(kfs).buildAll();

            ReleaseId releaseId = ks.getRepository().getDefaultReleaseId();
            KieContainer kc = ks.newKieContainer(releaseId);

            KieSession ksession = kc.newKieSession();
            ksession.fireAllRules();
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (ShouldHavePrevented e) {
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (Exception e) {
            // test succeeded. the policy in place prevented the rule from executing the System.exit().
        }
    }
    
    public static class MaliciousExitHelper {
        public static int exit() {
            System.exit(0);
            return 0;
        }
        public static boolean isEnabled() {
            System.exit(0);
            return true;
        }
    }
    
    public static class TestSecurityManager extends SecurityManager {
        @Override
        public void checkExit(int status) {
            super.checkExit(status);
            throw new ShouldHavePrevented("The security policy should have prevented the call to System.exit()");
        }
    }
    
    public static class ShouldHavePrevented extends SecurityException {
        public ShouldHavePrevented(String message) {
            super(message);
        }
    }

}