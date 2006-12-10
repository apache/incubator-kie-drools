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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jfdi.interpreter.TypeResolver;
import org.codehaus.jfdi.interpreter.ClassTypeResolver;
import org.drools.spi.AvailableVariables;
import org.drools.rule.Package;

import junit.framework.TestCase;

public class FunctionFixerTest extends TestCase {

    private FunctionFixer fixer;
    
    public void setUp() {
        List list = new ArrayList();
        list.add( "org.drools.*" );
        TypeResolver typeResolver = new ClassTypeResolver( list );
        Package pkg = new Package("testPackage");
        pkg.addFunction( "addFive" );
        pkg.addFunction( "foo" );
        pkg.addFunction( "bar" );
        
        list = new ArrayList();
        list.add( "Func.*" );
        this.fixer = new FunctionFixer( pkg, new StaticMethodFunctionResolver(list, typeResolver) );
    }

    public void testSimple() {
        assertEquals( "org.drools.Func.func()",
                      fixer.fix( "func( )" ) );
    }

    public void testSimpleWithPArams() {
        Map variables = new HashMap();
        variables.put( "yyy", String.class );
        variables.put( "iii", String.class );
        assertEquals( "org.drools.Func.func(yyy, iii)",
                      fixer.fix( "func(yyy, iii)", new AvailableVariables( new Map[] { variables }  ) ) );
    }

    public void testMoreComplex() {
        Map variables = new HashMap();
        variables.put( "yyy", String.class );
        variables.put( "iii", String.class );        
        assertEquals( "xxx org.drools.Func.func(yyy, iii) yyy",
                      fixer.fix( "xxx func(yyy, iii) yyy", new AvailableVariables( new Map[] { variables }  )  ) );
    }

    public void testLeaveAloneNew() {
        assertEquals( "new Integer (5)",
                      fixer.fix( "new Integer (5)" ) );
    }

    public void testLeaveAloneDrools() {
        assertEquals( "xxx drools.org(iii) org.drools.Func.func(yyy, iii) yyy",
                      fixer.fix( "xxx drools.org(iii) func(yyy, iii) yyy" ) );
    }

    public void testWorkWithDotAll() {        
        assertEquals( "\n\t\n\tAddFive.addFive(list) ;",
                      fixer.fix( "\n\t\n\taddFive ( list ) ;" ) );
    }

    public void testWithDollarSigns() {
        assertEquals( "\nFoo.foo($list);",
                      fixer.fix( "\nfoo($list);" ) );
    }

    public void testReservedWordsInJava() {
        assertEquals( "\nfor(int i=0; i < 2; i++) { /*do noithing*/ }",
                      fixer.fix( "\nfor(int i=0; i < 2; i++) { /*do noithing*/ }" ) );
    }

    public void testMultipleInABracket() {
        assertEquals( "if (Foo.foo(bar)) { Bar.bar(baz); }",
                      fixer.fix( "if (foo(bar)) { bar(baz); }" ) );
    }

    public void testInBrackets() {
        assertEquals( "if (Foo.foo(bar))",
                      fixer.fix( "if (foo(bar))" ) );
    }

    public void testAlreadyAdded() {
        assertEquals( "Foo.foo(bar)",
                      fixer.fix( "Foo.foo(bar)" ) );
    }

    public void testInString() {
        assertEquals( "\"if (foo(bar))\"",
                      fixer.fix( "\"if (foo(bar))\"" ) );
    }

    public void testComplexWithBrackets() {
        assertEquals( "System.out.println(\"foo(\" + Foo.foo(bar) + Bar.bar(baz)",
                      fixer.fix( "System.out.println(\"foo(\" + foo(bar) + bar(baz)" ) );
    }
    
    public void testXPath() {
        assertEquals( "foo.executeXpath(\"//node1/node2/text()\")",
                      fixer.fix("foo.executeXpath(\"//node1/node2/text()\")" ) );
      }
      
      public void testExpressionGrouping() {
        assertEquals( "while((foo = bar.baz()) != null)",
                      fixer.fix( "while((foo = bar.baz()) != null)" ) );
      }     

}