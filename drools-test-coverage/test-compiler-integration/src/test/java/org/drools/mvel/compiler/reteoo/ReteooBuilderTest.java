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

package org.drools.mvel.compiler.reteoo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.thoughtworks.xstream.XStream;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectSource;
import org.junit.Test;
import org.kie.api.KieBase;

import static org.junit.Assert.assertEquals;
import static org.kie.soup.xstream.XStreamUtils.createTrustingXStream;

public class ReteooBuilderTest {

    private final boolean writeTree = false;

    /** Implementation specific subclasses must provide this. */
    protected KieBase getKnowledgeBase() throws Exception {
        return KnowledgeBaseFactory.newKnowledgeBase();
    }

    @Test
    public void testThreePatternsWithConstraints() throws Exception {
        //checkRuleBase( "ThreePatternsWithConstraints" );
    }

    @Test
    public void testOneAndTwoOrs() throws Exception {
        //checkRuleBase( "OneAndTwoOrs" );
    }

    @Test
    public void testOneAndTwoOrsPerson() throws Exception {
        //checkRuleBase( "OneAndTwoOrsPerson" );
    }

    private void writeRuleBase(final InternalKnowledgeBase kBase,
                               final String fileName) throws IOException {
        final XStream xstream = createTrustingXStream();

        final PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter( "src/test/resources/org/drools/reteoo/" + fileName ) ) );

        xstream.toXML( kBase,
                       out );
    }

    private void checkRuleBase(final String name) throws Exception {
        final KnowledgeBuilderImpl builder = new KnowledgeBuilderImpl();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_" + name + ".drl" ) ) );
        InternalKnowledgePackage pkg = builder.getPackage("org.drools.mvel.compiler.test");

        final InternalKnowledgeBase kBase = (InternalKnowledgeBase) getKnowledgeBase();
        kBase.addPackage( pkg );

        if ( this.writeTree ) {
            writeRuleBase( kBase,
                           name );
        }

        final XStream xstream = createTrustingXStream();

        final InternalKnowledgeBase goodKBase = (InternalKnowledgeBase) xstream.fromXML( getClass().getResourceAsStream( name ) );

        nodesEquals( goodKBase.getRete(),
                     kBase.getRete() );
    }

    private void nodesEquals(final Object object1,
                             final Object object2) {
        assertEquals( object1 + " is not of the same type as " + object2,
                      object1.getClass(),
                      object2.getClass() );

        assertEquals( object1 + " is not equal to " + object2,
                      object1,
                      object2 );

        if ( object1 instanceof ObjectSource) {
            final ObjectSource source1 = (ObjectSource) object1;
            final ObjectSource source2 = (ObjectSource) object2;

            final ObjectSink[] list1 = source1.getObjectSinkPropagator().getSinks();
            final ObjectSink[] list2 = source2.getObjectSinkPropagator().getSinks();

            assertEquals( object1.getClass() + " nodes have different number of sinks",
                          list1.length,
                          list2.length );

            for ( int i = 0, size = list1.length; i < size; i++ ) {
                nodesEquals( list1[i],
                             list2[i] );
            }
        } else if ( object1 instanceof LeftTupleSource) {
            final LeftTupleSource source1 = (LeftTupleSource) object1;
            final LeftTupleSource source2 = (LeftTupleSource) object2;

            final LeftTupleSink[] list1 = source1.getSinkPropagator().getSinks();
            final LeftTupleSink[] list2 = source2.getSinkPropagator().getSinks();

            assertEquals( object1.getClass() + " nodes have different number of sinks",
                          list1.length,
                          list2.length );

            for ( int i = 0, size = list1.length; i < size; i++ ) {
                nodesEquals( list1[i],
                             list2[i] );
            }
        }

    }
}
