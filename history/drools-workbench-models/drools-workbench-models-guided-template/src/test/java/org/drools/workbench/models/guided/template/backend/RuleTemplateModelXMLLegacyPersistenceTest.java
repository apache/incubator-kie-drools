/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.template.backend;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.DSLVariableValue;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.junit.Test;

import static org.junit.Assert.*;

public class RuleTemplateModelXMLLegacyPersistenceTest {

    @Test
    public void testUnmarshalDSLVariableValuesLegacy() {

        //See https://issues.jboss.org/browse/GUVNOR-1872
        final String xml = "<rule>"
                + "<name>BugReportRule</name>"
                + "<modelVersion>1.0</modelVersion>"
                + "<attributes/>"
                + "<metadataList/>"
                + "<lhs>"
                + "<dslSentence>"
                + "<definition>If processInstance</definition>"
                + "<values/>"
                + "</dslSentence>"
                + "</lhs>"
                + "<rhs>"
                + "<dslSentence>"
                + "<definition>MyLog {myout}</definition>"
                + "<values>"
                + "<string>sample out rule 1</string>"
                + "<string>myout</string>"
                + "</values>"
                + "</dslSentence>"
                + "</rhs>"
                + "<isNegated>false</isNegated>"
                + "</rule>";

        RuleModel rm = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( xml );

        assertNotNull( rm );

        assertEquals( 1,
                      rm.lhs.length );
        assertTrue( rm.lhs[ 0 ] instanceof DSLSentence );

        DSLSentence dslPattern = (DSLSentence) rm.lhs[ 0 ];

        assertEquals( "If processInstance",
                      dslPattern.getDefinition() );
        assertEquals( 0,
                      dslPattern.getValues().size() );

        assertEquals( 1,
                      rm.rhs.length );
        assertTrue( rm.rhs[ 0 ] instanceof DSLSentence );

        DSLSentence dslAction = (DSLSentence) rm.rhs[ 0 ];

        assertEquals( "MyLog {myout}",
                      dslAction.getDefinition() );
        assertEquals( 2,
                      dslAction.getValues().size() );

        assertTrue( dslAction.getValues().get( 0 ) instanceof DSLVariableValue );
        assertTrue( dslAction.getValues().get( 1 ) instanceof DSLVariableValue );

        assertEquals( "sample out rule 1",
                      dslAction.getValues().get( 0 ).getValue() );
        assertEquals( "myout",
                      dslAction.getValues().get( 1 ).getValue() );

    }

    @Test
    public void testUnmarshalDSLVariableValues() {

        //See https://issues.jboss.org/browse/GUVNOR-1872
        final String xml = "<rule>"
                + "<name>BugReportRule</name>"
                + "<modelVersion>1.0</modelVersion>"
                + "<attributes/>"
                + "<metadataList/>"
                + "<lhs>"
                + "<dslSentence>"
                + "<definition>If processInstance</definition>"
                + "<values/>"
                + "</dslSentence>"
                + "</lhs>"
                + "<rhs>"
                + "<dslSentence>"
                + "<definition>MyLog {myout}</definition>"
                + "<values>"
                + "<org.drools.workbench.models.datamodel.rule.DSLVariableValue>"
                + "<value>5-4 sample out</value>"
                + "</org.drools.workbench.models.datamodel.rule.DSLVariableValue>"
                + "<org.drools.workbench.models.datamodel.rule.DSLVariableValue>"
                + "<value>myout</value>"
                + "</org.drools.workbench.models.datamodel.rule.DSLVariableValue>"
                + "</values>"
                + "</dslSentence>"
                + "</rhs>"
                + "<isNegated>false</isNegated>"
                + "</rule>";

        RuleModel rm = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( xml );

        assertNotNull( rm );

        assertEquals( 1,
                      rm.lhs.length );
        assertTrue( rm.lhs[ 0 ] instanceof DSLSentence );

        DSLSentence dslPattern = (DSLSentence) rm.lhs[ 0 ];

        assertEquals( "If processInstance",
                      dslPattern.getDefinition() );
        assertEquals( 0,
                      dslPattern.getValues().size() );

        assertEquals( 1,
                      rm.rhs.length );
        assertTrue( rm.rhs[ 0 ] instanceof DSLSentence );

        DSLSentence dslAction = (DSLSentence) rm.rhs[ 0 ];

        assertEquals( "MyLog {myout}",
                      dslAction.getDefinition() );
        assertEquals( 2,
                      dslAction.getValues().size() );

        assertTrue( dslAction.getValues().get( 0 ) instanceof DSLVariableValue );
        assertTrue( dslAction.getValues().get( 1 ) instanceof DSLVariableValue );

        assertEquals( "5-4 sample out",
                      dslAction.getValues().get( 0 ).getValue() );
        assertEquals( "myout",
                      dslAction.getValues().get( 1 ).getValue() );

    }

    /**
     * This will verify that we can load an old BRL change. If this fails, then
     * backwards compatibility is broken.
     */
    @Test
    public void testBackwardsCompat() throws Exception {
        RuleModel m2 = RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( loadResource( "existing_brl.xml" ) );

        assertNotNull( m2 );
        assertEquals( 3,
                      m2.rhs.length );
    }

    public static String loadResource( final String name ) throws Exception {
        final InputStream in = RuleTemplateModelXMLLegacyPersistenceTest.class.getResourceAsStream( name );
        final Reader reader = new InputStreamReader( in );
        final StringBuilder text = new StringBuilder();

        final char[] buf = new char[ 1024 ];
        int len = 0;
        while ( ( len = reader.read( buf ) ) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }

        return text.toString();
    }

}
