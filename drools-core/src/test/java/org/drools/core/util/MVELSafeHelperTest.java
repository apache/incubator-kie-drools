/*
 * Copyright 2015 JBoss Inc
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

package org.drools.core.util;

import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mvel2.PropertyAccessException;

@Ignore( "This test causes problems to surefire, so it will be disabled for now. It works when executed by itself.")
public class MVELSafeHelperTest {
    private static TestSecurityManager tsm;

    @BeforeClass
    public static void init() {
        String enginePolicy = MVELSafeHelperTest.class.getResource("engine.policy").getFile();
        String kiePolicy = MVELSafeHelperTest.class.getResource("kie.policy").getFile();
        System.setProperty("java.security.policy", enginePolicy);
        System.setProperty("kie.security.policy", kiePolicy);
        
        tsm = new TestSecurityManager();
        System.setSecurityManager(tsm);
    }

    @AfterClass
    public static void close() {
        System.setSecurityManager(null);
        System.setProperty("java.security.policy", "");
        System.setProperty("kie.security.policy", "");
    }
    
    @Test
    public void testUntrustedJavaConsequence() throws Exception {
        try {
            MVELSafeHelper.getEvaluator().eval("System.exit(0);");
            Assert.fail("Should have raised an exception...");
        } catch (ShouldHavePrevented e) {
            Assert.fail("The security policy for the rule should have prevented this from executing...");
        } catch (PropertyAccessException e) {
            // test succeeded. the policy in place prevented the rule from executing the System.exit().
        }
    }
    
    @Test
    public void testReflectionAttack() throws Exception {
        String setup = "java.lang.reflect.Field field = org.drools.core.util.MVELSafeHelper.getDeclaredField(\"evaluator\");\n"
                + "System.out.println(field);\n"  
                + "field.setAccessible(true);\n"  
                + "field.set(null, \"new org.drools.core.util.MVELSafeHelper.RawMVELEvaluator()\");";
        try {
            Assert.assertEquals( MVELSafeHelper.SafeMVELEvaluator.class.getName(), MVELSafeHelper.getEvaluator().getClass().getName() );
            MVELSafeHelper.getEvaluator().eval(setup, new HashMap<String,Object>());
            Assert.fail("Should have raised an AccessControlException");
        } catch (PropertyAccessException e) {
            // test succeeded. the policy in place prevented the rule from executing field.setAccessible().
            //e.printStackTrace();
        }
    }
    
    public void testReflectionOnFinal() throws Exception {
        
        
    }
    
    public static class MaliciousExitHelper {
        public static int exit() {
            System.exit(0);
            return 0;
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
    
    public static class StaticFinalHolder {
        private static final boolean FLAG = true;
    }
}
