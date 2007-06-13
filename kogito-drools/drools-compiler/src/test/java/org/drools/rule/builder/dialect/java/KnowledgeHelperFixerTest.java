package org.drools.rule.builder.dialect.java;

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

import org.drools.rule.builder.dialect.java.KnowledgeHelperFixer;

import junit.framework.TestCase;

public class KnowledgeHelperFixerTest extends TestCase {

    private static final KnowledgeHelperFixer fixer = new KnowledgeHelperFixer();

    public void testAdd__Handle__Simple() {
        String result = KnowledgeHelperFixerTest.fixer.fix( "update(myObject );" );
        assertEquals( "drools.update( myObject );",
                      result );

        result = KnowledgeHelperFixerTest.fixer.fix( "update ( myObject );" );
        assertEquals( "drools.update( myObject );",
                      result );
    }

    public void testAdd__Handle__withNewLines() {
        final String result = KnowledgeHelperFixerTest.fixer.fix( "\n\t\n\tupdate(myObject );" );
        assertEquals( "\n\t\n\tdrools.update( myObject );",
                      result );
    }

    public void testAdd__Handle__rComplex() {
        String result = KnowledgeHelperFixerTest.fixer.fix( "something update(myObject ); other" );
        assertEquals( "something drools.update( myObject ); other",
                      result );

        result = KnowledgeHelperFixerTest.fixer.fix( "something update (myObject );" );
        assertEquals( "something drools.update( myObject );",
                      result );

        result = KnowledgeHelperFixerTest.fixer.fix( " update(myObject ); x" );
        assertEquals( " drools.update( myObject ); x",
                      result );

        //should not touch, as it is not a stand alone word
        result = KnowledgeHelperFixerTest.fixer.fix( "xxupdate(myObject ) x" );
        assertEquals( "xxupdate(myObject ) x",
                      result );
    }

    public void testMultipleMatches() {
        String result = KnowledgeHelperFixerTest.fixer.fix( "update(myObject); update(myObject );" );
        assertEquals( "drools.update( myObject ); drools.update( myObject );",
                      result );

        result = KnowledgeHelperFixerTest.fixer.fix( "xxx update(myObject ); update(myObject ); update(yourObject ); yyy" );
        assertEquals( "xxx drools.update( myObject ); drools.update( myObject ); drools.update( yourObject ); yyy",
                      result );

    }

    public void testAssert() {
        final String raw = "some code; insert(new String(\"foo\"));\n More();";
        final String result = "some code; drools.insert(new String(\"foo\"));\n More();";
        assertEquals( result,
                      KnowledgeHelperFixerTest.fixer.fix( raw ) );
    }

    public void testAssertLogical() {
        final String raw = "some code; insertLogical(new String(\"foo\"));\n More();";
        final String result = "some code; drools.insertLogical(new String(\"foo\"));\n More();";
        assertEquals( result,
                      KnowledgeHelperFixerTest.fixer.fix( raw ) );
    }

    public void testAllActionsMushedTogether() {
        String result = KnowledgeHelperFixerTest.fixer.fix( "insert(myObject ); update(ourObject);\t retract(herObject);" );
        assertEquals( "drools.insert(myObject ); drools.update( ourObject );\t drools.retract( herObject );",
                      result );

        result = KnowledgeHelperFixerTest.fixer.fix( "insert(myObject ); update(ourObject);\t retract(herObject);\ninsert(myObject); update(ourObject);\t retract(herObject);" );
        assertEquals( "drools.insert(myObject ); drools.update( ourObject );\t drools.retract( herObject);\ndrools.insert(myObject); drools.update( ourObject );\t drools.retract( herObject );",
                      result );
    }

    public void testLeaveLargeAlone() {
        final String original = "yeah yeah yeah minsert( xxx ) this is a long() thing Person (name=='drools') modify a thing";
        final String result = KnowledgeHelperFixerTest.fixer.fix( original );
        assertEquals( original,
                      result );
    }

    public void testWithNull() {
        final String original = null;
        final String result = KnowledgeHelperFixerTest.fixer.fix( original );
        assertEquals( original,
                      result );
    }

    public void testLeaveAssertAlone() {
        final String original = "drools.insert(foo)";
        assertEquals( original,
                      KnowledgeHelperFixerTest.fixer.fix( original ) );
    }

    public void testLeaveAssertLogicalAlone() {
        final String original = "drools.insertLogical(foo)";
        assertEquals( original,
                      KnowledgeHelperFixerTest.fixer.fix( original ) );
    }

    public void testWackyAssert() {
        final String raw = "System.out.println($person1.getName() + \" and \" + $person2.getName() +\" are sisters\");\n" + "insert($person1.getName(\"foo\") + \" and \" + $person2.getName() +\" are sisters\"); yeah();";
        final String expected = "System.out.println($person1.getName() + \" and \" + $person2.getName() +\" are sisters\");\n" + "drools.insert($person1.getName(\"foo\") + \" and \" + $person2.getName() +\" are sisters\"); yeah();";

        assertEquals( expected,
                      KnowledgeHelperFixerTest.fixer.fix( raw ) );

    }

    public void testMoreAssertCraziness() {
        final String raw = "foobar(); (insert(new String(\"blah\").get()); bangBangYudoHono();)";
        assertEquals( "foobar(); (drools.insert(new String(\"blah\").get()); bangBangYudoHono();)",
                      KnowledgeHelperFixerTest.fixer.fix( raw ) );
    }

}