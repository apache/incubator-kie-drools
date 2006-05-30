package org.drools.decisiontable.model;

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

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 * 
 */
public class SnippetBuilderTest extends TestCase {

    public void testBuildSnippet() {
        final String snippet = "something.param.getAnother().equals($param);";
        final SnippetBuilder snip = new SnippetBuilder( snippet );
        final String cellValue = "$42";
        final String result = snip.build( cellValue );
        assertNotNull( result );

        assertEquals( "something.param.getAnother().equals($42);",
                      result );
    }

    public void testBuildSnippetNoPlaceHolder() {
        final String snippet = "something.getAnother().equals(blah);";
        final SnippetBuilder snip = new SnippetBuilder( snippet );
        final String cellValue = "this is ignored...";
        final String result = snip.build( cellValue );

        assertEquals( snippet,
                      result );
    }

    public void testSingleParamMultipleTimes() {
        final String snippet = "something.param.getAnother($param).equals($param);";
        final SnippetBuilder snip = new SnippetBuilder( snippet );
        final String cellValue = "42";
        final String result = snip.build( cellValue );
        assertNotNull( result );

        assertEquals( "something.param.getAnother(42).equals(42);",
                      result );

    }

    public void testMultiPlaceHolder() {
        final String snippet = "something.getAnother($1,$2).equals($2, '$2');";
        final SnippetBuilder snip = new SnippetBuilder( snippet );
        final String result = snip.build( "x, y" );
        assertEquals( "something.getAnother(x,y).equals(y, 'y');",
                      result );

    }

    public void testMultiPlaceHolderSingle() {
        final String snippet = "something.getAnother($1).equals($1);";
        final SnippetBuilder snip = new SnippetBuilder( snippet );
        final String result = snip.build( "x" );
        assertEquals( "something.getAnother(x).equals(x);",
                      result );

    }

}