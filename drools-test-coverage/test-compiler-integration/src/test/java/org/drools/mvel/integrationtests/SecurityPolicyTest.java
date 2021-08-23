/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.mvel.integrationtests;

import java.security.Policy;
import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.ConsequenceException;
import org.kie.internal.io.ResourceFactory;
import org.mvel2.PropertyAccessException;

/**
 * This is a sample class to launch a rule.
 */
@Ignore( "This test causes problems to surefire, so it will be disabled for now. It works when executed by itself.")
@RunWith(Parameterized.class)
public class SecurityPolicyTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public SecurityPolicyTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Before
    public void init() {
        String enginePolicy = SecurityPolicyTest.class.getResource("engine.policy").getFile();
        String kiePolicy = SecurityPolicyTest.class.getResource("rules.policy").getFile();
        System.setProperty("java.security.policy", enginePolicy);
        System.setProperty("kie.security.policy", kiePolicy);
        Policy.getPolicy().refresh();
        System.setSecurityManager(new TestSecurityManager());
    }

    @After
    public void close() {
        System.setSecurityManager(null);
        System.setProperty("java.security.policy", "");
        System.setProperty("kie.security.policy", "");
    }
    
    @Test
    public void testUntrustedJavaConsequence() {
        String drl = "package org.foo.bar\n" +
                "rule R1 when\n" +
                "then\n" +
                "    System.exit(0);" +
                "end\n";

        try {
            KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
            KieSession ksession = kbase.newKieSession();
            ksession.fireAllRules();
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (ShouldHavePrevented e) {
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (ConsequenceException e) {
            // test succeeded. the policy in place prevented the rule from executing the System.exit().
        }
    }
    
    @Test
    public void testUntrustedMvelConsequence() {
        String drl = "package org.foo.bar\n" +
                "rule R1 dialect \"mvel\" when\n" +
                "then\n" +
                "    System.exit(0);" +
                "end\n";

        try {
            KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
            KieSession ksession = kbase.newKieSession();
            ksession.fireAllRules();
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (ShouldHavePrevented e) {
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (ConsequenceException e) {
            // test succeeded. the policy in place prevented the rule from executing the System.exit().
        }
    }
    
    @Test
    public void testSerializationUntrustedMvelConsequence() {
     // kbase serialization is not supported. But leave it for standard-drl.
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
    public void testUntrustedJavaSalience() {
        String drl = "package org.foo.bar\n" +
                "import "+MaliciousExitHelper.class.getName().replace('$', '.')+" \n" +
                "rule R1 dialect \"java\" salience( MaliciousExitHelper.exit() ) \n" +
                "when\n" +
                "then\n" +
                "end\n";

        try {
            KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
            KieSession ksession = kbase.newKieSession();
            ksession.fireAllRules();
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (ShouldHavePrevented e) {
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (Exception e) {
            // test succeeded. the policy in place prevented the rule from executing the System.exit().
        }
    }

    @Test
    public void testUntrustedMVELSalience() {
        String drl = "package org.foo.bar\n" +
                "import "+MaliciousExitHelper.class.getName().replace('$', '.')+" \n" +
                "rule R1 dialect \"mvel\" salience( MaliciousExitHelper.exit() ) \n" +
                "when\n" +
                "then\n" +
                "end\n";

        try {
            KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
            KieSession ksession = kbase.newKieSession();
            ksession.fireAllRules();
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (PropertyAccessException e) {
            // weak way of testing but couldn't find a better way
            if( e.toString().contains( "The security policy should have prevented" ) ) {
                Assert.fail("The security policy for the rule should have prevented this from executing...");
            } else {
                // test succeeded
            }
        }
    }

    @Test
    public void testCustomAccumulate() {
        String drl = "package org.foo.bar\n" +
                "rule testRule\n" + 
                "    when\n" + 
                "        Number() from accumulate(Object(), " +
                "               init(System.exit(-1);), " +
                "               action(System.exit(-1);), " +
                "               reverse(System.exit(-1);), " +
                "               result(0))\n" + 
                "    then\n" + 
                "end";

        try {
            KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
            KieSession ksession = kbase.newKieSession();
            ksession.fireAllRules();
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (ShouldHavePrevented e) {
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (Exception e) {
            // test succeeded. the policy in place prevented the rule from executing the System.exit().
        }
    }

    @Test
    public void testCustomAccumulateMVEL() {
        String drl = "package org.foo.bar\n" +
                "rule testRule dialect \"mvel\" \n" + 
                "    when\n" + 
                "        Number() from accumulate(Object(), " +
                "               init(System.exit(-1);), " +
                "               action(System.exit(-1);), " +
                "               reverse(System.exit(-1);), " +
                "               result(0))\n" + 
                "    then\n" + 
                "end";

        try {
            KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
            KieSession ksession = kbase.newKieSession();
            ksession.fireAllRules();
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (PropertyAccessException e) {
            // weak way of testing but couldn't find a better way
            if( e.toString().contains( "The security policy should have prevented" ) ) {
                Assert.fail("The security policy for the rule should have prevented this from executing...");
            } else {
                // test succeeded
            }
        } catch( Exception e ) {
            if( e.toString().contains("access denied (\"java.lang.RuntimePermission\" \"exitVM.-1\")")) {
                // test succeeded
            } else {
                throw e;
            }
        }
    }

    @Test
    public void testAccumulateFunctionMVEL() {
        String drl = "package org.foo.bar\n" +
                "import "+MaliciousExitHelper.class.getName().replace('$', '.')+" \n" +
                "rule testRule dialect \"mvel\" \n" + 
                "    when\n" + 
                "        Number() from accumulate(Object(), " +
                "               sum(MaliciousExitHelper.exit()))\n" + 
                "    then\n" + 
                "end";

        try {
            KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
            KieSession ksession = kbase.newKieSession();
            ksession.insert("foo");
            ksession.fireAllRules();
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (PropertyAccessException e) {
            // weak way of testing but couldn't find a better way
            if( e.toString().contains( "The security policy should have prevented" ) ) {
                Assert.fail("The security policy for the rule should have prevented this from executing...");
            } else {
                // test succeeded
            }
        } catch( Exception e ) {
            if( e.toString().contains("access denied (\"java.lang.RuntimePermission\" \"exitVM.0\")")) {
                // test succeeded
            } else {
                throw e;
            }
        }
    }

    @Test
    public void testAccumulateFunctionJava() {
        String drl = "package org.foo.bar\n" +
                "import "+MaliciousExitHelper.class.getName().replace('$', '.')+" \n" +
                "rule testRule dialect \"java\" \n" + 
                "    when\n" + 
                "        Number() from accumulate(Object(), " +
                "               sum(MaliciousExitHelper.exit()))\n" + 
                "    then\n" + 
                "end";

        try {
            KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
            KieSession ksession = kbase.newKieSession();
            ksession.insert("foo");
            ksession.fireAllRules();
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (PropertyAccessException e) {
            // weak way of testing but couldn't find a better way
            if( e.toString().contains( "The security policy should have prevented" ) ) {
                Assert.fail("The security policy for the rule should have prevented this from executing...");
            } else {
                // test succeeded
            }
        } catch( Exception e ) {
            if( e.toString().contains("access denied (\"java.lang.RuntimePermission\" \"exitVM.0\")")) {
                // test succeeded
            } else {
                throw e;
            }
        }
    }

    @Test
    public void testUntrustedEnabled() {
        String drl = "package org.foo.bar\n" +
                "import "+MaliciousExitHelper.class.getName().replace('$', '.')+" \n" +
                "rule R1 enabled( MaliciousExitHelper.isEnabled() ) \n" +
                "when\n" +
                "then\n" +
                "end\n";

        try {
            KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
            KieSession ksession = kbase.newKieSession();
            ksession.fireAllRules();
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (ShouldHavePrevented e) {
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (Exception e) {
            // test succeeded. the policy in place prevented the rule from executing the System.exit().
        }
    }
    
    @Test
    public void testUntrustedMVELEnabled() {
        String drl = "package org.foo.bar\n" +
                "import "+MaliciousExitHelper.class.getName().replace('$', '.')+" \n" +
                "rule R1 dialect \"mvel\" enabled( MaliciousExitHelper.isEnabled() ) \n" +
                "when\n" +
                "then\n" +
                "end\n";

        try {
            KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
            KieSession ksession = kbase.newKieSession();
            ksession.fireAllRules();
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (PropertyAccessException e) {
            // weak way of testing but couldn't find a better way
            if( e.toString().contains( "The security policy should have prevented" ) ) {
                Assert.fail("The security policy for the rule should have prevented this from executing...");
            } else {
                // test succeeded
            }
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
