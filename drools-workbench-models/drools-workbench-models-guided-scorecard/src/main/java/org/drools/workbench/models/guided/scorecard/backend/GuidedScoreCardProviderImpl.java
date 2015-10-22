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

import org.drools.compiler.compiler.GuidedScoreCardProvider;
import org.drools.core.util.IoUtils;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;

import java.io.IOException;
import java.io.InputStream;

public class GuidedScoreCardProviderImpl implements GuidedScoreCardProvider {

    @Override
    public String loadFromInputStream(InputStream is) throws IOException {
        String xml = new String( IoUtils.readBytesFromInputStream(is), IoUtils.UTF8_CHARSET );
        ScoreCardModel model = GuidedScoreCardXMLPersistence.getInstance().unmarshall( xml );

        return GuidedScoreCardDRLPersistence.marshal( model );
    }
}
