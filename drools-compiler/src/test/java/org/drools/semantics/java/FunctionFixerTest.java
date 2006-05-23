package org.drools.semantics.java;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
    
    public void testWithDollarSigns() {
        FunctionFixer fixer = new FunctionFixer();
        assertEquals( "\nFoo.foo($list);",
                      fixer.fix( "\nfoo($list);" ) );
    }      
    
    public void testReservedWordsInJava() {
        FunctionFixer fixer = new FunctionFixer();
        assertEquals( "\nfor(int i=0; i < 2; i++) { /*do noithing*/ }",
                      fixer.fix( "\nfor(int i=0; i < 2; i++) { /*do noithing*/ }" ) );
    }
    
    public void testNestedInAMethod() {
        FunctionFixer fixer = new FunctionFixer();
            assertEquals( "obj.method(Foo.foo(bar));",
                          fixer.fix( "obj.method(foo(bar));" ) );    

        
    }
        
}