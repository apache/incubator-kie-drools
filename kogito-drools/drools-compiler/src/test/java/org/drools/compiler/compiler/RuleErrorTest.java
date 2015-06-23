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

package org.drools.compiler.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.drools.compiler.commons.jci.problems.CompilationProblem;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.junit.Test;

public class RuleErrorTest {

    @Test
    public void testNewLineInMessage() {
        CompilationProblem[] probs = new CompilationProblem[3];
        probs[0] = new MockCompilationProblem();
        probs[1] = new MockCompilationProblem();
        probs[2] = new MockCompilationProblem();

        DescrBuildError err = new DescrBuildError( new RuleDescr( "ruleName" ),
                                                   new AndDescr(),
                                                   probs,
                                                   "IM IN YR EROR" );
        assertNotNull( err.toString() );
        String msg = err.getMessage();

        assertTrue( msg.indexOf( "IM IN YR EROR" ) != -1 );
        System.err.println( msg );
        assertEquals( "IM IN YR EROR problem\nproblem\nproblem",
                      msg );

    }

    class MockCompilationProblem
        implements
        CompilationProblem {

        public int getEndColumn() {
            return 0;
        }

        public int getEndLine() {
            return 0;
        }

        public String getFileName() {
            return "X";
        }

        public String getMessage() {
            return "problem";
        }

        public int getStartColumn() {
            return 0;
        }

        public int getStartLine() {
            return 0;
        }

        public boolean isError() {
            return true;
        }

    }

}
