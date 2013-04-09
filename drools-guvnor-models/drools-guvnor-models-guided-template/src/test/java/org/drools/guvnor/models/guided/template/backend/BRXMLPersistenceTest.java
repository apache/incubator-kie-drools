/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.models.guided.template.backend;

import org.drools.guvnor.models.commons.shared.rule.DSLSentence;
import org.drools.guvnor.models.commons.shared.rule.DSLVariableValue;
import org.drools.guvnor.models.commons.shared.rule.RuleModel;
import org.junit.Test;

import static org.junit.Assert.*;

public class BRXMLPersistenceTest {

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

        RuleModel rm = BRXMLPersistence.getInstance().unmarshal( xml );

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
                + "<org.drools.guvnor.models.commons.shared.rule.DSLVariableValue>"
                + "<value>5-4 sample out</value>"
                + "</org.drools.guvnor.models.commons.shared.rule.DSLVariableValue>"
                + "<org.drools.guvnor.models.commons.shared.rule.DSLVariableValue>"
                + "<value>myout</value>"
                + "</org.drools.guvnor.models.commons.shared.rule.DSLVariableValue>"
                + "</values>"
                + "</dslSentence>"
                + "</rhs>"
                + "<isNegated>false</isNegated>"
                + "</rule>";

        RuleModel rm = BRXMLPersistence.getInstance().unmarshal( xml );

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

}
