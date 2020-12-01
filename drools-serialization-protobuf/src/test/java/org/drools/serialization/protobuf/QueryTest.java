/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.drools.serialization.protobuf;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.drools.core.QueryResultsImpl;
import org.drools.core.QueryResultsRowImpl;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.runtime.rule.impl.FlatQueryResultRow;
import org.drools.core.runtime.rule.impl.FlatQueryResults;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.mvel.compiler.Cheese;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TestName;
import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class QueryTest extends CommonTestMethodBase {

    @org.junit.Rule
    public TestName testName = new TestName();

    @Before
    public void before() {
       System.out.println( "] " + testName.getMethodName());
    }

    private static QueryResults getQueryResults(KieSession session, String queryName, Object... arguments ) throws Exception {
        QueryResultsImpl results = (QueryResultsImpl) session.getQueryResults( queryName, arguments );

        FlatQueryResults flatResults = new FlatQueryResults(results);

        assertEquals( "Query results size", results.size(), flatResults.size() );
        assertEquals( "Query results identifiers", results.getIdentifiers().length, flatResults.getIdentifiers().length );
        Set<String> resultIds = new TreeSet<String>(Arrays.asList(results.getIdentifiers()));
        Set<String> flatIds = new TreeSet<String>(Arrays.asList(flatResults.getIdentifiers()));
        assertArrayEquals("Flat query results identifiers", resultIds.toArray(), flatIds.toArray() );

        String [] identifiers = results.getIdentifiers();
        Iterator<QueryResultsRow> copyFlatIter = flatResults.iterator();
        for( int i = 0; i < results.size(); ++i ) {
            QueryResultsRow row = results.get(i);
            assertTrue( "Round-tripped flat query results contain less rows than original query results", copyFlatIter.hasNext());
            QueryResultsRow copyRow = copyFlatIter.next();
            for( String id : identifiers ) {
                Object obj = row.get(id);
                if( obj != null ) {
                    Object copyObj = copyRow.get(id);
                    assertTrue( "Flat query result [" + i + "] does not contain result: '" + id + "': " + obj + "/" + copyObj, obj != null && obj.equals(copyObj));
                }
                FactHandle fh = row.getFactHandle(id);
                FactHandle copyFh = copyRow.getFactHandle(id);
                if( fh != null ) {
                    assertNotNull( "Flat query result [" + i + "] does not contain facthandle: '" + ((InternalFactHandle) fh).getId() + "'", copyFh);
                    String fhStr = fh.toExternalForm();
                    fhStr = fhStr.substring(0, fhStr.lastIndexOf(":"));
                    String copyFhStr = copyFh.toExternalForm();
                    copyFhStr = copyFhStr.substring(0, copyFhStr.lastIndexOf(":"));
                    assertEquals( "Unequal fact handles for fact handle '" + ((InternalFactHandle) fh).getId() + "':",
                                  fhStr, copyFhStr );
                }
            }
        }

        // check identifiers
        Set<String> copyFlatIds = new TreeSet<String>(Arrays.asList(flatResults.getIdentifiers()));
        assertArrayEquals("Flat query results identifiers", flatIds.toArray(), copyFlatIds.toArray() );
        return flatResults;
    }

    @Test
    public void testQuery() throws Exception {
        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("simple_query_test.drl"));
        KieSession session = createKieSession( kbase );

        final Cheese stilton = new Cheese( "stinky",
                5 );
        FactHandle factHandle = session.insert( stilton );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);

        String queryName = "simple query";
        org.kie.api.runtime.rule.QueryResults results = getQueryResults(session, queryName);
        assertEquals( 1,
                results.size() );

        QueryResultsRow row = results.iterator().next();
        if( row instanceof FlatQueryResultRow ) {
            FlatQueryResultRow flatRow = (FlatQueryResultRow) row;
            assertEquals( 0, flatRow.getIdentifiers().size() );
        } else if( row instanceof QueryResultsRowImpl ) {
            QueryResultsRowImpl rowImpl = (QueryResultsRowImpl) row;
            assertEquals( 0, rowImpl.getDeclarations().size() );
        }
    }

    @Test
    public void testQueryRemoval() throws Exception {
        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("simple_query_test.drl"));
        KieSession session = createKieSession( kbase );

        final Cheese stilton = new Cheese( "stinky",
                                           5 );
        session.insert( stilton );
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);
        QueryResults results = session.getQueryResults( "simple query" );
        assertEquals( 1,
                      results.size() );

        Rule rule = kbase.getKiePackage( "org.drools.compiler.test" ).getRules().iterator().next();

        assertEquals( "simple query",
                      rule.getName());

        kbase.removeQuery( "org.drools.compiler.test",
                           "simple query" );

        assertTrue( kbase.getKiePackage( "org.drools.compiler.test" ).getRules().isEmpty() );

        try {
            results = session.getQueryResults( "simple query" );
        } catch ( Exception e ) {
            assertTrue( e.getMessage().endsWith( "does not exist") );
        }
    }
}
