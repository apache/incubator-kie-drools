package org.drools.lang.dsl.template;

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

import java.util.HashMap;

import junit.framework.TestCase;

public class TemplateContextTest extends TestCase {

//    public void testAllInOne() {
//        final Template ctx = new Template();
//        //chunks represent a lexed grammar "left hand side"
//        ctx.addChunk( "baby on board" ).addChunk( "{0}" ).addChunk( "and" ).addChunk( "{1}" ).addChunk( "burt ward" );
//        String result = ctx.expandOnce( "yeah this is an expression baby on board exp1 and exp2 burt ward end.",
//                                        "something({0}, {1})" );
//        assertEquals( "yeah this is an expression something(exp1, exp2) end.",
//                      result );
//
//        //and check that the iterative one is OK.
//        result = ctx.expandOnce( "yeah this is an expression baby on board exp1 and exp2 burt ward end.",
//                                 "something({0}, {1})" );
//        assertEquals( "yeah this is an expression something(exp1, exp2) end.",
//                      result );
//    }
//
//    public void testBuildStrings() {
//
//        final Template ctx = new Template();
//
//        //chunks represent a lexed grammar "left hand side"
//        ctx.addChunk( "baby on board" ).addChunk( "{0}" ).addChunk( "and" ).addChunk( "{1}" ).addChunk( "burt ward" );
//
//        //and this is the right hand side grammar mapping (no lexing required, simple hole filling !).
//        final String grammar_r = "something({0}, {1})";
//
//        //and this is the full expression
//        final String nl = "yeah this is an expression baby on board exp1 and exp2 burt ward end.";
//
//        //match the pattern in nl, put the values in the map        
//        final HashMap map = new HashMap();
//        ctx.processNL( nl,
//                       map );
//
//        //now get the chunk of nl that will be replaced with the target later.
//        final String subKey = ctx.getSubstitutionKey();
//        assertEquals( "baby on board exp1 and exp2 burt ward",
//                      subKey );
//
//        final String target = ctx.populateTargetString( map,
//                                                  grammar_r );
//        assertEquals( "something(exp1, exp2)",
//                      target );
//
//        final String result = ctx.interpolate( nl,
//                                         subKey,
//                                         target );
//
//        assertEquals( "yeah this is an expression something(exp1, exp2) end.",
//                      result );
//
//    }
//
//    public void testMultipleReplacement() {
//
//        final Template ctx = new Template();
//
//        //chunks represent a lexed grammar "left hand side"
//        ctx.addChunk( "{0}" ).addChunk( "likes cheese" );
//
//        final String nl = "bob likes cheese and michael likes cheese conan likes cheese";
//        final String grammarTemplate = "{0}.likesCheese()";
//        final String expected = "bob.likesCheese() and michael.likesCheese() conan.likesCheese()";
//
//        final String result = ctx.expandAll( nl,
//                                       grammarTemplate );
//        assertEquals( expected,
//                      result );
//    }
//
//    public void testBasicExpression() {
//
//        final Chunk chunk1 = new Chunk( "baby on board" );
//        final Chunk chunk2 = new Chunk( "{0}" );
//        final Chunk chunk3 = new Chunk( "and" );
//        final Chunk chunk4 = new Chunk( "{1}" );
//        final Chunk chunk5 = new Chunk( "burt ward" );
//
//        chunk1.next = chunk2;
//        chunk2.next = chunk3;
//        chunk3.next = chunk4;
//        chunk4.next = chunk5;
//
//        final String nl = "yeah this is an expression baby on board exp1 and exp2 burt ward";
//        chunk1.process( nl );
//
//        final HashMap map = new HashMap();
//        chunk1.buildValueMap( map );
//
//        assertEquals( "exp1",
//                      map.get( "{0}" ) );
//        assertEquals( "exp2",
//                      map.get( "{1}" ) );
//
//    }
//
//    public void testStartWith() {
//
//        final Chunk chunk1 = new Chunk( "{0}" );
//        final Chunk chunk2 = new Chunk( "a thing" );
//        final Chunk chunk3 = new Chunk( "and" );
//        final Chunk chunk4 = new Chunk( "{1}" );
//        final Chunk chunk5 = new Chunk( "one more" );
//
//        chunk1.next = chunk2;
//        chunk2.next = chunk3;
//        chunk3.next = chunk4;
//        chunk4.next = chunk5;
//
//        final String nl = "exp1 a thing and exp2 one more";
//        chunk1.process( nl );
//
//        final HashMap map = new HashMap();
//        chunk1.buildValueMap( map );
//
//        assertEquals( "exp1",
//                      map.get( "{0}" ) );
//        assertEquals( "exp2",
//                      map.get( "{1}" ) );
//
//    }
//
//    public void testEndWith() {
//
//        final Chunk chunk1 = new Chunk( "blah blah blah" );
//        final Chunk chunk2 = new Chunk( "{1}" );
//
//        chunk1.next = chunk2;
//
//        final String nl = "blah blah blah exp1";
//        chunk1.process( nl );
//
//        final HashMap map = new HashMap();
//        chunk1.buildValueMap( map );
//
//        assertEquals( "exp1",
//                      map.get( "{1}" ) );
//        assertEquals( 1,
//                      map.size() );
//    }
//
//    public void testOneInTheMiddle() {
//        final Chunk chunk1 = new Chunk( "yeah " );
//        final Chunk chunk2 = new Chunk( "{abc}" );
//        final Chunk chunk3 = new Chunk( "one more" );
//
//        chunk1.next = chunk2;
//        chunk2.next = chunk3;
//
//        final String nl = "yeah exp1 one more ";
//        chunk1.process( nl );
//
//        final HashMap map = new HashMap();
//        chunk1.buildValueMap( map );
//
//        assertEquals( "exp1",
//                      map.get( "{abc}" ) );
//
//    }
//
    public void testNoTokens() {
//        final Chunk chunk1 = new Chunk( "yeah " );
//
//        final String nl = "yeah exp1 one more ";
//        chunk1.process( nl );
//
//        final HashMap map = new HashMap();
//        chunk1.buildValueMap( map );
//
//        assertEquals( 0,
//                      map.size() );
    }

}