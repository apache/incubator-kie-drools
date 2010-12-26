package org.drools.compiler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.commons.jci.problems.CompilationProblem;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.RuleDescr;

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
