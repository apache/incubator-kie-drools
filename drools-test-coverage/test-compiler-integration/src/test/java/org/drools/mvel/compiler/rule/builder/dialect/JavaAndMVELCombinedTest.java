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
package org.drools.mvel.compiler.rule.builder.dialect;

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;

@RunWith(Parameterized.class)
public class JavaAndMVELCombinedTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public JavaAndMVELCombinedTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private final static String FN1 = "mveljavarules.drl";
    private final static String FN2 = "mvelonly.drl";
    private final static String FN3 = "javaonly.drl";

    @Test
    public void testMixed() {
        timing( FN1, "mveljava: ");
    }
    
    @Test
    public void testMVEL() {
        timing( FN2, "    mvel: ");
    }
    
    @Test
    public void testJAVA() {
        timing( FN3, "    java: ");
    }
    
//    public void testJavaMVELCombination() throws Exception {
//        long time1 = timing( new Runnable() {
//            public void run() {
//                readDRL( FN1 );
//            }
//        } );
//        long time2 = timing( new Runnable() {
//            public void run() {
//                readDRL( FN2 );
//            }
//        } );
//        long time3 = timing( new Runnable() {
//            public void run() {
//                readDRL( FN3 );
//            }
//        } );
//        
//        System.out.println("mveljava: "+time1/1000.);
//        System.out.println("    mvel: "+time2/1000.);
//        System.out.println("    java: "+time3/1000.);
//        
//    }

    private void timing( String name, String msg ) {
        long start = System.currentTimeMillis();
        readDRL( name );
        long time = System.currentTimeMillis()-start;
        System.out.println(msg+time/1000.);
    }

    private void readDRL(String fn) {
        try {
            KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, fn);
        } catch ( Throwable t ) {
            throw new RuntimeException(t);
        }

    }

}
