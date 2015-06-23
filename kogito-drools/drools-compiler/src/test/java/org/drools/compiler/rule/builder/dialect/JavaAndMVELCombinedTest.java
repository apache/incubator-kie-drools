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

package org.drools.compiler.rule.builder.dialect;

import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import static org.junit.Assert.*;

import org.kie.api.KieBase;

public class JavaAndMVELCombinedTest extends CommonTestMethodBase {

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
            KieBase kieBase = loadKnowledgeBase(fn);
        } catch ( Throwable t ) {
            throw new RuntimeException(t);
        }

    }

}
