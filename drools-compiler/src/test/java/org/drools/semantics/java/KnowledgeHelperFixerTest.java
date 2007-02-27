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

import org.drools.dialect.java.KnowledgeHelperFixer;

import junit.framework.TestCase;

public class KnowledgeHelperFixerTest extends TestCase {

    private static final KnowledgeHelperFixer fixer = new KnowledgeHelperFixer();

    public void testAdd__Handle__Simple() {
        String result = KnowledgeHelperFixerTest.fixer.fix( "modify(myObject )" );
        assertEquals( "drools.modifyObject(myObject__Handle__, myObject)",
                      result );

        result = KnowledgeHelperFixerTest.fixer.fix( "modify ( myObject )" );
        assertEquals( "drools.modifyObject(myObject__Handle__, myObject)",
                      result );
    }

    public void testAdd__Handle__withNewLines() {
        final String result = KnowledgeHelperFixerTest.fixer.fix( "\n\t\n\tmodify(myObject )" );
        assertEquals( "\n\t\n\tdrools.modifyObject(myObject__Handle__, myObject)",
                      result );
    }

    public void testAdd__Handle__rComplex() {
        String result = KnowledgeHelperFixerTest.fixer.fix( "something modify(myObject ); other" );
        assertEquals( "something drools.modifyObject(myObject__Handle__, myObject); other",
                      result );

        result = KnowledgeHelperFixerTest.fixer.fix( "something modify (myObject )" );
        assertEquals( "something drools.modifyObject(myObject__Handle__, myObject)",
                      result );

        result = KnowledgeHelperFixerTest.fixer.fix( " modify(myObject ) x" );
        assertEquals( " drools.modifyObject(myObject__Handle__, myObject) x",
                      result );

        //should not touch, as it is not a stand alone word
        result = KnowledgeHelperFixerTest.fixer.fix( "xxmodify(myObject ) x" );
        assertEquals( "xxmodify(myObject ) x",
                      result );
    }

    public void testMultipleMatches() {
        String result = KnowledgeHelperFixerTest.fixer.fix( "modify(myObject) modify(myObject )" );
        assertEquals( "drools.modifyObject(myObject__Handle__, myObject) drools.modifyObject(myObject__Handle__, myObject)",
                      result );

        result = KnowledgeHelperFixerTest.fixer.fix( "xxx modify(myObject ) modify(myObject ) modify(yourObject ) yyy" );
        assertEquals( "xxx drools.modifyObject(myObject__Handle__, myObject) drools.modifyObject(myObject__Handle__, myObject) drools.modifyObject(yourObject__Handle__, yourObject) yyy",
                      result );

    }

    public void testAssert() {
        final String raw = "some code; assert(new String(\"foo\"));\n More();";
        final String result = "some code; drools.assertObject(new String(\"foo\"));\n More();";
        assertEquals( result,
                      KnowledgeHelperFixerTest.fixer.fix( raw ) );
    }

    public void testAssertLogical() {
        final String raw = "some code; assertLogical(new String(\"foo\"));\n More();";
        final String result = "some code; drools.assertLogicalObject(new String(\"foo\"));\n More();";
        assertEquals( result,
                      KnowledgeHelperFixerTest.fixer.fix( raw ) );
    }

    public void testAllActionsMushedTogether() {
        String result = KnowledgeHelperFixerTest.fixer.fix( "assert(myObject ) modify(ourObject);\t retract(herObject)" );
        assertEquals( "drools.assertObject(myObject ) drools.modifyObject(ourObject__Handle__, ourObject);\t drools.retractObject(herObject__Handle__)",
                      result );

        result = KnowledgeHelperFixerTest.fixer.fix( "assert(myObject ) modify(ourObject);\t retract(herObject)\nassert(myObject) modify(ourObject);\t retract(herObject)" );
        assertEquals( "drools.assertObject(myObject ) drools.modifyObject(ourObject__Handle__, ourObject);\t drools.retractObject(herObject__Handle__)\ndrools.assertObject(myObject) drools.modifyObject(ourObject__Handle__, ourObject);\t drools.retractObject(herObject__Handle__)",
                      result );
    }

    public void testLeaveLargeAlone() {
        final String original = "yeah yeah yeah massert( xxx ) this is a long() thing Person (name=='drools') modify a thing";
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
        final String original = "drools.assertObject(foo)";
        assertEquals( original,
                      KnowledgeHelperFixerTest.fixer.fix( original ) );
    }

    public void testLeaveAssertLogicalAlone() {
        final String original = "drools.assertLogicalObject(foo)";
        assertEquals( original,
                      KnowledgeHelperFixerTest.fixer.fix( original ) );
    }

    public void testWackyAssert() {
        final String raw = "System.out.println($person1.getName() + \" and \" + $person2.getName() +\" are sisters\");\n" + "assert($person1.getName(\"foo\") + \" and \" + $person2.getName() +\" are sisters\"); yeah();";
        final String expected = "System.out.println($person1.getName() + \" and \" + $person2.getName() +\" are sisters\");\n" + "drools.assertObject($person1.getName(\"foo\") + \" and \" + $person2.getName() +\" are sisters\"); yeah();";

        assertEquals( expected,
                      KnowledgeHelperFixerTest.fixer.fix( raw ) );

    }

    public void testMoreAssertCraziness() {
        final String raw = "foobar(); (assert(new String(\"blah\").get()); bangBangYudoHono();)";
        assertEquals( "foobar(); (drools.assertObject(new String(\"blah\").get()); bangBangYudoHono();)",
                      KnowledgeHelperFixerTest.fixer.fix( raw ) );
    }

}