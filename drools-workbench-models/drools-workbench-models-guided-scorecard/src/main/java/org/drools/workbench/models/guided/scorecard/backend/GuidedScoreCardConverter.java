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

package org.drools.workbench.models.guided.scorecard.backend;

import org.drools.compiler.kie.builder.impl.FormatConversionResult;
import org.drools.compiler.kie.builder.impl.FormatConverter;
import org.drools.core.util.IoUtils;
import org.drools.workbench.models.commons.backend.BaseConverter;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;

public class GuidedScoreCardConverter extends BaseConverter implements FormatConverter {

    @Override
    public FormatConversionResult convert( String name,
                                           byte[] input ) {
        String xml = new String( input, IoUtils.UTF8_CHARSET );
        ScoreCardModel model = GuidedScoreCardXMLPersistence.getInstance().unmarshall( xml );

        String drl = new StringBuilder().append( GuidedScoreCardDRLPersistence.marshal( model ) ).toString();

        return new FormatConversionResult( getDestinationName( name ), drl.getBytes( IoUtils.UTF8_CHARSET ) );
    }
}
