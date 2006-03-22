package org.drools.semantics.java;

import junit.framework.TestCase;

public class FunctionFixerTest extends TestCase {

    private static FunctionFixer fixer = new FunctionFixer();

    public void testSimple() {
        FunctionFixer fixer = new FunctionFixer();
        assertEquals( "Func.func()",
                      fixer.fix( "func( )" ) );
    }

    public void testSimpleWithPArams() {
        FunctionFixer fixer = new FunctionFixer();
        assertEquals( "Func.func(yyy, iii)",
                      fixer.fix( "func(yyy, iii)" ) );
    }

    public void testMoreComplex() {
        FunctionFixer fixer = new FunctionFixer();
        assertEquals( "xxx Func.func(yyy, iii) yyy",
                      fixer.fix( "xxx func(yyy, iii) yyy" ) );
    }
    
    public void testLeaveAloneNew() {
        FunctionFixer fixer = new FunctionFixer();
        assertEquals( "new Integer (5)",
                      fixer.fix( "new Integer (5)" ) );
    }         
    
    public void testLeaveAloneDrools() {
        FunctionFixer fixer = new FunctionFixer();
        assertEquals( "xxx drools.org(iii) Func.func(yyy, iii) yyy",
                      fixer.fix( "xxx drools.org(iii) func(yyy, iii) yyy" ) );
    }

    public void testWorkWithDotAll() {
        FunctionFixer fixer = new FunctionFixer();
        assertEquals( "\n\t\n\tAddFive.addFive(list) ;",
                      fixer.fix( "\n\t\n\taddFive ( list ) ;" ) );
    }  
        
}
