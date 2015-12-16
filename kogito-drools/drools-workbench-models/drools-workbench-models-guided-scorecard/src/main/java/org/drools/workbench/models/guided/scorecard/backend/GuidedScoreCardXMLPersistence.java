/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.guided.scorecard.backend;

import java.math.BigDecimal;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;

public class GuidedScoreCardXMLPersistence {

    private XStream xt;
    private static final GuidedScoreCardXMLPersistence INSTANCE = new GuidedScoreCardXMLPersistence();

    private GuidedScoreCardXMLPersistence() {
        xt = new XStream( new DomDriver() );
        //All numerical values are historically BigDecimal
        xt.alias( "valueNumeric", Number.class,
                  BigDecimal.class );
    }

    public static GuidedScoreCardXMLPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal( final ScoreCardModel model ) {
        return xt.toXML( model );
    }

    public ScoreCardModel unmarshall( final String xml ) {
        if ( xml == null || xml.trim().length() == 0 ) {
        }
        return (ScoreCardModel) xt.fromXML( xml );
    }
}
