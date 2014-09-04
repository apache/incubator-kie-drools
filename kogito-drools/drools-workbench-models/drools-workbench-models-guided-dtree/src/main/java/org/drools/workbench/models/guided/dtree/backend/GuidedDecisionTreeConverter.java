/*
 * Copyright 2014 JBoss Inc
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
package org.drools.workbench.models.guided.dtree.backend;

import org.drools.compiler.kie.builder.impl.FormatConversionResult;
import org.drools.core.util.IoUtils;
import org.drools.workbench.models.commons.backend.BaseConverter;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;

public class GuidedDecisionTreeConverter extends BaseConverter {

    @Override
    public FormatConversionResult convert( String name,
                                           byte[] input ) {
        String xml = new String( input, IoUtils.UTF8_CHARSET );
        GuidedDecisionTree model = GuidedDecisionTreeXMLPersistence.getInstance().unmarshal( xml );

        String drl = new StringBuilder().append( GuidedDecisionTreeDRLPersistence.getInstance().marshal( model ) ).toString();

        return new FormatConversionResult( getDestinationName( name,
                                                               hasDSLSentences( model ) ),
                                           drl.getBytes( IoUtils.UTF8_CHARSET ) );
    }

    // Check is the model uses DSLSentences and hence requires expansion. This code is copied from GuidedDecisionTableUtils.
    // GuidedDecisionTableUtils also handles data-types, enums etc and hence requires a DataModelOracle to function. Loading
    // a DataModelOracle just to determine whether the model has DSLs is an expensive operation and not needed here.
    public static boolean hasDSLSentences( final GuidedDecisionTree model ) {

        return false;
    }

}
