/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.datamodel.rule.visitors;

import org.drools.workbench.models.datamodel.rule.ExpressionCollection;
import org.drools.workbench.models.datamodel.rule.ExpressionCollectionIndex;
import org.drools.workbench.models.datamodel.rule.ExpressionField;
import org.drools.workbench.models.datamodel.rule.ExpressionFieldVariable;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.ExpressionGlobalVariable;
import org.drools.workbench.models.datamodel.rule.ExpressionMethod;
import org.drools.workbench.models.datamodel.rule.ExpressionMethodParameter;
import org.drools.workbench.models.datamodel.rule.ExpressionText;
import org.drools.workbench.models.datamodel.rule.ExpressionUnboundFact;
import org.drools.workbench.models.datamodel.rule.ExpressionVariable;
import org.junit.Test;

import static org.junit.Assert.*;

public class CopyExpressionVisitorTest {

    @Test
    public void testExpressionFormLineCopy() {
        ExpressionFormLine efl = new ExpressionFormLine();
        efl.appendPart( new ExpressionCollection( "collection", "CT", "GT", "PT" ) );
        efl.appendPart( new ExpressionCollectionIndex( "collectionIndex", "CT", "GT" ) );
        efl.appendPart( new ExpressionField( "field", "CT", "FT", "PT" ) );
        efl.appendPart( new ExpressionFieldVariable( "fieldVariable", "Type" ) );
        efl.appendPart( new ExpressionGlobalVariable( "globalVariable", "CT", "GT", "PT" ) );
        efl.appendPart( new ExpressionMethod( "method", "CT", "GT" ) );
        efl.appendPart( new ExpressionMethodParameter( "methodParam", "CT", "GT" ) );
        efl.appendPart( new ExpressionText( "text" ) );
        efl.appendPart( new ExpressionUnboundFact( "FactType" ) );
        efl.appendPart( new ExpressionVariable( "binding", "FactType" ) );
        // verify that the new instance created with copy constructor is equal to original
        assertEquals( efl, new ExpressionFormLine( efl ) );
    }
}
