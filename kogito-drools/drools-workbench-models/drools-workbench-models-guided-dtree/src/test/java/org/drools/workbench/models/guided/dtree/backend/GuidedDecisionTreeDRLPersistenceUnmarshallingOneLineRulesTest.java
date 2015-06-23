/*
 * Copyright 2015 JBoss Inc
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

package org.drools.workbench.models.guided.dtree.backend;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.junit.Test;

public class GuidedDecisionTreeDRLPersistenceUnmarshallingOneLineRulesTest extends AbstractGuidedDecisionTreeDRLPersistenceUnmarshallingTest {

    @Test
    public void testOneLineEmpty() throws Exception {
        final String drl = "";

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "integerField",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        getAndTestUnmarshalledModel( drl, "test", 0 );
    }

    @Test
    public void testOneLinePackage() throws Exception {
        final String drl = "package a.b";

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "integerField",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        getAndTestUnmarshalledModel( drl, "test", 0 );
    }

    @Test
    public void testOneLineEmptyRule() throws Exception {
        final String drl = "rule \"test\" " +
                "when " +
                "then " +
                "end";

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "integerField",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        getAndTestUnmarshalledModel( drl, "test", 0 );
    }

    @Test
    public void testOneLineRule() throws Exception {
        final String drl = "rule \"test\" " +
                "when " +
                "  Person( integerField == \"someText\" ) " +
                "then " +
                "end";

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "integerField",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        getAndTestUnmarshalledModel( drl, "test", 1 );
    }

    @Test
    public void testOneLineRules() throws Exception {
        final String drl = "rule \"test1\" " +
                "when " +
                "  Person( integerField == \"someText\" ) " +
                "then " +
                "end " +
                "rule \"test2\" " +
                "when " +
                "  Person( integerField == 10 ) " +
                "then " +
                "end";

        addModelField( "Person",
                       "this",
                       "Person",
                       DataType.TYPE_THIS );
        addModelField( "Person",
                       "integerField",
                       Integer.class.getName(),
                       DataType.TYPE_NUMERIC_INTEGER );

        getAndTestUnmarshalledModel( drl, "test", 1 );
    }

}
