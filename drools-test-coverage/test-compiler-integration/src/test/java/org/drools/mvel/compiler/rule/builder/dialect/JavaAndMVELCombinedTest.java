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

import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;

public class JavaAndMVELCombinedTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    private final static String FN1 = "mveljavarules.drl";
    private final static String FN2 = "mvelonly.drl";
    private final static String FN3 = "javaonly.drl";

    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testMixed(KieBaseTestConfiguration kieBaseTestConfiguration) {
        timing(kieBaseTestConfiguration, FN1, "mveljava: ");
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testMVEL(KieBaseTestConfiguration kieBaseTestConfiguration) {
        timing(kieBaseTestConfiguration, FN2, "    mvel: ");
    }
    
    @ParameterizedTest(name = "KieBase type={0}")
	@MethodSource("parameters")
    public void testJAVA(KieBaseTestConfiguration kieBaseTestConfiguration) {
        timing(kieBaseTestConfiguration, FN3, "    java: ");
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

    private void timing( KieBaseTestConfiguration kieBaseTestConfiguration, String name, String msg ) {
        long start = System.currentTimeMillis();
        readDRL(kieBaseTestConfiguration, name);
        long time = System.currentTimeMillis()-start;
        System.out.println(msg+time/1000.);
    }

    private void readDRL(KieBaseTestConfiguration kieBaseTestConfiguration, String fn) {
        try {
            KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, fn);
        } catch ( Throwable t ) {
            throw new RuntimeException(t);
        }

    }

}
