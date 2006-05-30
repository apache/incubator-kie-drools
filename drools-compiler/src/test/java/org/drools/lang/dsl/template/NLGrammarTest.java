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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

public class NLGrammarTest extends TestCase {

    /** Check that it sets up priorities correctly */
    public void testLoadFromProperties() throws Exception {
        final NLGrammar grammar = new NLGrammar();
        grammar.addNLItem( new NLMappingItem( "znumber 1",
                                              "number 1",
                                              "*" ) );
        grammar.addNLItem( new NLMappingItem( "bnumber 2",
                                              "number 2",
                                              "*" ) );
        grammar.addNLItem( new NLMappingItem( "anumber 3",
                                              "number 3",
                                              "*" ) );

        final List list = grammar.getMappings();
        final Object[] items = list.toArray();

        NLMappingItem item = (NLMappingItem) items[0];
        assertEquals( "znumber 1",
                      item.getNaturalTemplate() );

        item = (NLMappingItem) items[1];
        assertEquals( "bnumber 2",
                      item.getNaturalTemplate() );
        assertEquals( "*",
                      item.getScope() );

        item = (NLMappingItem) items[2];
        assertEquals( "anumber 3",
                      item.getNaturalTemplate() );

    }

    /** 
     * Should load from an input stream to a properties-like format.
     * It is not strictly properties, as it has naughty spaces etc.
     */
    public void testLoadImproperProperties() throws Exception {

        final NLGrammar grammar = new NLGrammar();
        final InputStream stream = this.getClass().getResourceAsStream( "test.dsl.properties" );

        final InputStreamReader reader = new InputStreamReader( stream );
        grammar.load( reader );
        final List mappings = grammar.getMappings();
        assertEquals( 5,
                      mappings.size() );

        final NLMappingItem[] items = new NLMappingItem[mappings.size()];
        mappings.toArray( items );
        NLMappingItem test = items[0];

        assertEquals( "*",
                      test.getScope() );
        assertEquals( "This is something",
                      test.getNaturalTemplate() );
        assertEquals( "Another thing",
                      test.getTargetTemplate() );

        test = items[1];
        assertEquals( "when",
                      test.getScope() );
        assertEquals( "This is something for a condition",
                      test.getNaturalTemplate() );
        assertEquals( "yeah",
                      test.getTargetTemplate() );

        test = items[3];
        assertEquals( "then",
                      test.getScope() );
        assertEquals( "this is also",
                      test.getNaturalTemplate() );
        assertEquals( "woot",
                      test.getTargetTemplate() );

        test = items[4];
        assertEquals( "*",
                      test.getScope() );
        assertEquals( "This has spaces",
                      test.getNaturalTemplate() );
        assertEquals( "yup yep",
                      test.getTargetTemplate() );

    }

    public void testFiltering() {
        final NLGrammar g = new NLGrammar();
        g.addNLItem( new NLMappingItem( "This is something",
                                        "boo",
                                        "then" ) );
        g.addNLItem( new NLMappingItem( "This is another",
                                        "coo",
                                        "then" ) );
        g.addNLItem( new NLMappingItem( "This is another2",
                                        "coo2",
                                        "*" ) );
        g.addNLItem( new NLMappingItem( "This is another3",
                                        "coo3",
                                        "when" ) );

        assertEquals( 4,
                      g.getMappings().size() );
        assertEquals( 3,
                      g.getMappings( "then" ).size() );
        assertEquals( 2,
                      g.getMappings( "when" ).size() );
        assertEquals( 1,
                      g.getMappings( null ).size() );

    }

    public void testSave() {
        NLGrammar g = new NLGrammar();
        g.addNLItem( new NLMappingItem( "This is something",
                                        "boo",
                                        "then" ) );
        g.addNLItem( new NLMappingItem( "This is another",
                                        "coo",
                                        "then" ) );
        g.addNLItem( new NLMappingItem( "end",
                                        "it",
                                        "*" ) );
        g.setDescription( "my description" );

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final OutputStreamWriter writer = new OutputStreamWriter( out );
        g.save( writer );

        final String result = out.toString();
        assertEquals( "#my description\n[then]This is something=boo\n[then]This is another=coo\nend=it\n",
                      result );

        //now load it to double check
        final ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );
        final InputStreamReader reader = new InputStreamReader( in );
        g = new NLGrammar();
        g.load( reader );
        assertEquals( 3,
                      g.getMappings().size() );
        assertEquals( "my description",
                      g.getDescription() );

    }

    public void testAddRemove() {
        final NLMappingItem item = new NLMappingItem( "end",
                                                "it",
                                                "*" );
        final NLGrammar g = new NLGrammar();
        g.addNLItem( new NLMappingItem( "This is something",
                                        "boo",
                                        "then" ) );
        g.addNLItem( new NLMappingItem( "This is another",
                                        "coo",
                                        "then" ) );
        g.addNLItem( item );
        g.setDescription( "my description" );

        item.setNaturalTemplate( "something else" );
        final NLMappingItem item2 = (NLMappingItem) g.getMappings( "*" ).get( 0 );
        assertEquals( item2,
                      item );
        assertEquals( "something else",
                      item2.getNaturalTemplate() );

        assertEquals( 3,
                      g.getMappings().size() );
        g.removeMapping( item );
        assertEquals( 2,
                      g.getMappings().size() );
        assertEquals( 0,
                      g.getMappings( "*" ).size() );
    }

    public void testValidate() {
        final NLGrammar g = new NLGrammar();
        NLMappingItem item = new NLMappingItem( "This is a {valid} mapping",
                                                "because {valid} is used",
                                                "*" );

        List errors = g.validateMapping( item );
        Assert.assertTrue( "Error list should be empty",
                           errors.isEmpty() );

        item = new NLMappingItem( "Unused {token}",
                                  "token not used",
                                  "*" );
        errors = g.validateMapping( item );
        Assert.assertEquals( "Error list should have 1 error",
                             1,
                             errors.size() );
        MappingError error = (MappingError) errors.get( 0 );
        Assert.assertEquals( "Wrong reported error",
                             MappingError.ERROR_UNUSED_TOKEN,
                             error.getErrorCode() );

        item = new NLMappingItem( "Undeclared token",
                                  "as {token} is used",
                                  "*" );
        errors = g.validateMapping( item );
        Assert.assertEquals( "Error list should have 1 error",
                             1,
                             errors.size() );
        error = (MappingError) errors.get( 0 );
        Assert.assertEquals( "Wrong reported error",
                             MappingError.ERROR_UNDECLARED_TOKEN,
                             error.getErrorCode() );

        item = new NLMappingItem( "Invalid {tok en",
                                  "as token does not have closing braces",
                                  "*" );
        errors = g.validateMapping( item );
        Assert.assertEquals( "Error list should have 1 error",
                             1,
                             errors.size() );
        error = (MappingError) errors.get( 0 );
        Assert.assertEquals( "Wrong reported error",
                             MappingError.ERROR_INVALID_TOKEN,
                             error.getErrorCode() );

        item = new NLMappingItem( "Unmatched braces token}",
                                  "as token does not have starting braces",
                                  "*" );
        errors = g.validateMapping( item );
        Assert.assertEquals( "Error list should have 1 error",
                             1,
                             errors.size() );
        error = (MappingError) errors.get( 0 );
        Assert.assertEquals( "Wrong reported error",
                             MappingError.ERROR_UNMATCHED_BRACES,
                             error.getErrorCode() );

        //TODO: really should stop the following from barfing...        
        //        item = new NLMappingItem("this is something {here}", "yeah here is {here} and { doSomething(); }", "*");
        //        errors = g.validateMapping( item );
        //        Assert.assertEquals( 0, errors.size() );

    }

}