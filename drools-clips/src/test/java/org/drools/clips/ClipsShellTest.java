/**
 * Copyright 2010 JBoss Inc
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

package org.drools.clips;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.drools.Person;
import org.drools.WorkingMemory;
import org.drools.clips.functions.AssertFunction;
import org.drools.clips.functions.BindFunction;
import org.drools.clips.functions.CallFunction;
import org.drools.clips.functions.CreateListFunction;
import org.drools.clips.functions.EqFunction;
import org.drools.clips.functions.GetFunction;
import org.drools.clips.functions.IfFunction;
import org.drools.clips.functions.LessThanFunction;
import org.drools.clips.functions.LessThanOrEqFunction;
import org.drools.clips.functions.MinusFunction;
import org.drools.clips.functions.ModifyFunction;
import org.drools.clips.functions.MoreThanFunction;
import org.drools.clips.functions.MoreThanOrEqFunction;
import org.drools.clips.functions.MultiplyFunction;
import org.drools.clips.functions.NewFunction;
import org.drools.clips.functions.PlusFunction;
import org.drools.clips.functions.PrintoutFunction;
import org.drools.clips.functions.PrognFunction;
import org.drools.clips.functions.ReturnFunction;
import org.drools.clips.functions.RunFunction;
import org.drools.clips.functions.SetFunction;
import org.drools.clips.functions.SwitchFunction;
import org.drools.common.InternalRuleBase;
import org.drools.rule.Package;
import org.drools.rule.Rule;

public class ClipsShellTest extends TestCase {
    private ByteArrayOutputStream baos;

    ClipsShell                    shell;

    public void setUp() {
        FunctionHandlers handlers = FunctionHandlers.getInstance();
        handlers.registerFunction( new PlusFunction() );
        handlers.registerFunction( new MinusFunction() );
        handlers.registerFunction( new MultiplyFunction() );
        handlers.registerFunction( new ModifyFunction() );
        handlers.registerFunction( new CreateListFunction() );
        handlers.registerFunction( new PrintoutFunction() );
        handlers.registerFunction( new PrognFunction() );
        handlers.registerFunction( new IfFunction() );
        handlers.registerFunction( new LessThanFunction() );
        handlers.registerFunction( new LessThanOrEqFunction() );
        handlers.registerFunction( new MoreThanFunction() );
        handlers.registerFunction( new MoreThanOrEqFunction() );
        handlers.registerFunction( new EqFunction() );
        handlers.registerFunction( new SwitchFunction() );
        //handlers.registerFunction( new DeffunctionFunction() );
        handlers.registerFunction( new ReturnFunction() );
        handlers.registerFunction( new RunFunction() );
        handlers.registerFunction( new BindFunction() );
        handlers.registerFunction( new NewFunction() );
        handlers.registerFunction( new SetFunction() );
        handlers.registerFunction( new GetFunction() );
        handlers.registerFunction( new CallFunction() );
        handlers.registerFunction( new AssertFunction() );

        this.shell = new ClipsShell();

        this.baos = new ByteArrayOutputStream();
        shell.addRouter( "t",
                         new PrintStream( baos ) );
    }

    //    public void test1() {
    //        String expr = "(* (+ 4 4 ) 2) (create$ 10 20 (+ 10 10) a) (modify ?p (name mark) (location \"london\")(age (+ 16 16) ) ) (printout t a b c (+ 4 4) )";
    //
    //        SExpression[] lisplists = evalString( expr );
    //
    //        StringBuilderAppendable appendable = new StringBuilderAppendable();
    //        MVELClipsContext context = new MVELClipsContext();
    //        for ( SExpression sExpression : lisplists ) {
    //            FunctionHandlers.dump( sExpression, appendable, context );
    //        }
    //
    //        System.out.println( appendable );
    //    }

    public void testBind() {
        String expr = "(bind ?x (create$ 10 20 30) ) (printout t ?x)";

        this.shell.eval( expr );

        assertEquals( "[10, 20, 30]",
                      new String( baos.toByteArray() ) );
    }

    public void testProgn() {
        String expr = "(progn (?x (create$ 10 20 30) ) (printout t ?x) )";

        this.shell.eval( expr );

        assertEquals( "102030",
                      new String( baos.toByteArray() ) );
    }

    public void testIf() {
        String expr = "(if (< 1 3) then (printout t hello) (printout t hello) )";

        this.shell.eval( expr );

        assertEquals( "hellohello",
                      new String( baos.toByteArray() ) );

        if ( 1 <= 10 ) {

        }
    }

    public void testIfElse() {
        String expr = "(if (eq 1 3) then (printout t hello)  (printout t 1) else (printout t hello)  (printout t 2))";

        this.shell.eval( expr );

        assertEquals( "hello2",
                      new String( baos.toByteArray() ) );
    }

    public void testSwitch() throws IOException {
        String expr = "(switch (?x) (case a then (printout t hello)(printout t 1)) (case b then (printout t hello)(printout t 2)) (default (printout t hello)(printout t 3)) )";

        // check case a
        this.shell.addVariable( "$x",
                                "a" );
        this.shell.eval( expr );
        assertEquals( "hello1",
                      new String( baos.toByteArray() ) );

        // check default
        this.shell.addVariable( "$x",
                                "M" );
        this.shell.eval( expr );
        assertEquals( "hello1hello3",
                      new String( baos.toByteArray() ) );

        // check case b
        this.shell.addVariable( "$x",
                                "b" );
        this.shell.eval( expr );
        assertEquals( "hello1hello3hello2",
                      new String( baos.toByteArray() ) );
    }

    // @FIXME - org.mvel.CompileException: unable to resolve property: unable to resolve method: org.drools.clips.Shell.max(java.lang.Integer, java.lang.Integer) [arglength=2]
    public void testDeffunction() {
        String function = "(deffunction max (?a ?b) (if (> ?a ?b) then (return ?a) else (return ?b) ) )";
        this.shell.eval( function );

        String expr = "(if (eq (max 3 5) 5) then (printout t hello) )";
        this.shell.eval( expr );
        assertEquals( "hello",
                      new String( baos.toByteArray() ) );

        expr = "(if (eq (max ?a ?b) 5) then (printout t hello) )";
        this.shell.addVariable( "$a",
                                "3" );
        this.shell.addVariable( "$b",
                                "5" );
        this.shell.eval( expr );
        assertEquals( "hellohello",
                      new String( baos.toByteArray() ) );
    }

    public void testDirectImportAndNew() {
        String t = "(import org.drools.Person) (bind ?p (new Person mark cheddar) ) (printout t ?p)";
        this.shell.eval( t );
        assertEquals( "[Person name='mark']",
                      new String( this.baos.toByteArray() ) );
    }

    public void testDynamicImportAndNew() {
        String t = "(import org.drools.*) (bind ?p (new Person mark cheddar) ) (printout t ?p)";
        this.shell.eval( t );
        assertEquals( "[Person name='mark']",
                      new String( this.baos.toByteArray() ) );
    }

    public void testSet() {
        String t = "(import org.drools.*) (bind ?p (new Person mark cheddar) ) (set ?p name bob) (printout t ?p)";
        this.shell.eval( t );
        assertEquals( "[Person name='bob']",
                      new String( this.baos.toByteArray() ) );
    }

    public void testGet() {
        String t = "(import org.drools.*) (bind ?p (new Person mark cheddar) )(printout t (get ?p name))";
        this.shell.eval( t );
        assertEquals( "mark",
                      new String( this.baos.toByteArray() ) );
    }

    public void testExplicitCall() {
        String t = "(import org.drools.*) (bind ?p (new Person mark cheddar) ) (call ?p setFields bob stilton 35)  (printout t (call ?p toLongString))";
        this.shell.eval( t );
        assertEquals( "[Person name='bob' likes='stilton' age='35']",
                      new String( this.baos.toByteArray() ) );
    }

    public void testImplicitCall() {
        String t = "(import org.drools.*) (bind ?p (new Person mark cheddar) ) (?p setFields bob stilton 35)  (printout t (call ?p toLongString))";
        this.shell.eval( t );
        assertEquals( "[Person name='bob' likes='stilton' age='35']",
                      new String( this.baos.toByteArray() ) );
    }

    public void FIXME_testRuleCreation() {
        this.shell.eval( "(import org.drools.Person)" );

        this.shell.eval( "(defrule yyy  => (printout t yy \" \" (eq 1 1) ) ) )" );
        Package pkg = shell.getStatefulSession().getRuleBase().getPackage( "MAIN" );

        Rule rule = pkg.getRule( "yyy" );
        assertEquals( "yyy",
                      rule.getName() );

        this.shell.eval( "(defrule xxx (Person (name ?name&bob) (age 30) ) (Person  (name ?name) (age 35)) => (printout t xx \" \" (eq 1 1) ) )" );

        rule = pkg.getRule( "xxx" );
        assertEquals( "xxx",
                      rule.getName() );

        assertEquals( 2,
                      pkg.getRules().length );

        assertTrue( pkg.getImports().containsKey( "org.drools.Person" ) );

        WorkingMemory wm = shell.getStatefulSession();
        wm.insert( new Person( "bob",
                               "cheddar",
                               30 ) );
        wm.insert( new Person( "bob",
                               "stilton",
                               35 ) );
        wm.fireAllRules();
        assertEquals( "yy truexx true",
                      new String( this.baos.toByteArray() ) );
    }

    public void FIXME_testTemplateCreation2() throws Exception {
        this.shell.eval( "(deftemplate PersonTemplate (slot name (type String) ) (slot age (type int) ) )" );
        this.shell.eval( "(defrule xxx (PersonTemplate (name ?name&bob) (age 30) ) (PersonTemplate  (name ?name) (age 35)) => (printout t xx \" \" (eq 1 1) ) )" );
        this.shell.eval( "(assert (PersonTemplate (name 'mike') (age 34)))" );

        Class personClass = ((InternalRuleBase)this.shell.getStatefulSession().getRuleBase()).getRootClassLoader().loadClass( "MAIN.PersonTemplate" );
        assertNotNull( personClass );
    }

    public void FIXME_testTemplateCreation() throws Exception {
        this.shell.eval( "(deftemplate Person (slot name (type String) ) (slot age (type int) ) )" );

        this.shell.eval( "(defrule xxx (Person (name ?name&bob) (age 30) ) => (printout t hello bob ) )" );

        this.shell.eval( "(assert (Person (name bob) (age 30) ) )" );
        this.shell.eval( "(run)" );

        assertEquals( "hellobob",
                      new String( this.baos.toByteArray() ) );
    }

    public void FIXME_testTemplateCreationWithJava() throws Exception {
        this.shell.eval( "(deftemplate Person (slot name (type String) ) (slot age (type int) ) )" );

        this.shell.eval( "(defrule yyy  => (printout t yy \" \" (eq 1 1) ) ) )" );
        Package pkg = shell.getStatefulSession().getRuleBase().getPackage( "MAIN" );

        Rule rule = pkg.getRule( "yyy" );
        assertEquals( "yyy",
                      rule.getName() );

        this.shell.eval( "(defrule xxx (Person (name ?name&bob) (age 30) ) (Person  (name ?name) (age 35)) => (printout t xx \" \" (eq 1 1) ) )" );

        rule = pkg.getRule( "xxx" );
        assertEquals( "xxx",
                      rule.getName() );

        assertEquals( 2,
                      pkg.getRules().length );

        WorkingMemory wm = shell.getStatefulSession();
        Class personClass = ((InternalRuleBase)this.shell.getStatefulSession().getRuleBase()).getRootClassLoader().loadClass( "MAIN.Person" );

        Method nameMethod = personClass.getMethod( "setName",
                                                   new Class[]{String.class} );
        Method ageMethod = personClass.getMethod( "setAge",
                                                  new Class[]{int.class} );

        Object bob1 = personClass.newInstance();
        nameMethod.invoke( bob1,
                           "bob" );
        ageMethod.invoke( bob1,
                          30 );

        Object bob2 = personClass.newInstance();
        nameMethod.invoke( bob2,
                           "bob" );
        ageMethod.invoke( bob2,
                          35 );
        //Constructor constructor = personClass.getConstructor( new Class[] { String.class,String.class, int.class} );
        wm.insert( bob1 );
        wm.insert( bob2 );

        wm.fireAllRules();
        assertEquals( "yy truexx true",
                      new String( this.baos.toByteArray() ) );
    }

    public void FIXME_testEmptyLHSRule() {
        String rule1 = "(defrule testRule => (printout t hello) (printout t goodbye))";
        this.shell.eval( rule1 );
        assertEquals( "hellogoodbye",
                      new String( this.baos.toByteArray() ) );
    }

    public void FIXME_testSimpleLHSRule() {
        this.shell.eval( "(import org.drools.*)" );
        this.shell.eval( "(defrule testRule (Person (name ?name&mark) ) => (printout t hello) (printout t \" \" ?name))" );
        this.shell.eval( "(assert (Person (name mark) ) )" );
        this.shell.eval( "(run)" );
        assertEquals( "hello mark",
                      new String( this.baos.toByteArray() ) );
    }

    public void FIXME_testRuleCallDeftemplate() {
        String function = "(deffunction max (?a ?b) (if (> ?a ?b) then (return ?a) else (return ?b) ) )";
        this.shell.eval( function );

        this.shell.eval( "(import org.drools.*)" );
        this.shell.eval( "(defrule testRule (Person (age ?age) ) => (printout t hello) (printout t \" \" (max 3 ?age) ) )" );
        this.shell.eval( "(assert (Person (name mark) (age 32) ) )" );
        this.shell.eval( "(run)" );
        assertEquals( "hello 32",
                      new String( this.baos.toByteArray() ) );
    }

    public void FIXME_testTwoSimpleRulesWithModify() {
        this.shell.eval( "(import org.drools.*)" );
        this.shell.eval( "(defrule testRule1 ?p <- (Person (name ?name&mark) ) => (printout t hello) (printout t \" \" ?name) (modify ?p (name bob) ) )" );
        this.shell.eval( "(defrule testRule2 (Person (name ?name&bob) ) => (printout t hello) (printout t \" \" ?name))" );
        this.shell.eval( "(assert (Person (name mark) ) )" );
        this.shell.eval( "(run)" );
        assertEquals( "hello markhello bob",
                      new String( this.baos.toByteArray() ) );
    }

    public void FIXME_testBlockEval() {
        String text = "(import org.drools.*)";
        text += "(defrule testRule1 ?p <- (Person (name ?name&mark) ) => (printout t hello) (printout t \" \" ?name) (modify ?p (name bob) ) )";
        text += "(defrule testRule2 (Person (name ?name&bob) ) => (printout t hello) (printout t \" \" ?name))";
        text += "(assert (Person (name mark) ) )";
        text += "(run)";
        this.shell.eval( text );
        assertEquals( "hello markhello bob",
                      new String( this.baos.toByteArray() ) );
    }

    public void FIXME_testPredicate() {
        this.shell.eval( "(import org.drools.Person)" );
        this.shell.eval( "(defrule testRule1 (Person (name ?name) (age ?age&:(> ?age 30)) ) => (printout t hello) (printout t \" \" ?name) )" );
        this.shell.eval( "(assert (Person (name mark) (age 27) ) )" );
        this.shell.eval( "(assert (Person (name bob) (age 35) ) )" );
        this.shell.eval( "(run)" );
        assertEquals( "hello bob",
                      new String( this.baos.toByteArray() ) );
    }

    public void FIXME_testReturnValue() {
        this.shell.eval( "(import org.drools.Person)" );
        this.shell.eval( "(defrule testRule1 (Person (age ?age) ) (Person (name ?name) (age =(- ?age 3)) ) => (printout t hello) (printout t \" \" ?name) )" );
        this.shell.eval( "(assert (Person (name mark) (age 32) ) )" );
        this.shell.eval( "(assert (Person (name bob) (age 35) ) )" );
        this.shell.eval( "(run)" );
        assertEquals( "hello mark",
                      new String( this.baos.toByteArray() ) );
    }

    public void FIXME_testTest() {
        this.shell.eval( "(import org.drools.Person)" );
        this.shell.eval( "(defrule testRule1 (Person (age ?age1) ) (Person (name ?name) (age ?age2) ) (test(eq ?age1 (+ ?age2 3) )) => (printout t hello) )" );
        this.shell.eval( "(assert (Person (name mark) (age 32) ) )" );
        this.shell.eval( "(assert (Person (name bob) (age 35) ) )" );
        this.shell.eval( "(run)" );
        assertEquals( "hello",
                      new String( this.baos.toByteArray() ) );
    }

    public void testRun() {
        this.shell.eval( "(run)" );
    }

    public void FIXME_testMixed() {
        this.shell.eval( "(import org.drools.Cheese)" );
        String str ="";
        str += "(deftemplate Person ";
        str += "  (slot name ";
        str += "    (type String) ) ";
        str += "  (slot age";
        str += "    (type String) ) ";
        str += "  (slot location";
        str += "    (type String) ) ";        
        str += "  (slot cheese";
        str += "    (type String) ) ";
        str += ")";
        this.shell.eval( str );
        this.shell.eval( "(deffunction max (?a ?b) (if (> ?a ?b) then (return ?a) else (return ?b) ) )" );

        str = "";
        str += "(defrule sendsomecheese ";
        str += "  (Person (name ?name) (age ?personAge) (cheese ?cheeseType) (location \"london\") ) ";
        str += "  (Cheese (type ?cheeseType) (price ?cheesePrice&:(eq (max ?personAge ?cheesePrice) ?cheesePrice)  ) )";
        str += "\n=>\n";
        str += "  (printout t \"send some \" ?cheeseType \" \" to \" \" ?name) ";
        str += ")";
        this.shell.eval( str );
        
        this.shell.eval( "(assert (Person (name mark) (location \"london\") (cheese \"cheddar\") (age 25) ) )" );
        this.shell.eval( "(assert (Cheese (type \"cheddar\") (price  30) ) ) " );
        this.shell.eval( "(run)" );
        
        assertEquals( "send some cheddar to mark",
                      new String( this.baos.toByteArray() ) );        
    }
}
