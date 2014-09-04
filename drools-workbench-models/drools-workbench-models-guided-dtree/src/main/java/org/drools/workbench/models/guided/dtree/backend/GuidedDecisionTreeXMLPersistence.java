/*
 * Copyright 2014 JBoss Inc
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

package org.drools.workbench.models.guided.dtree.backend;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;

public class GuidedDecisionTreeXMLPersistence {

    private static final GuidedDecisionTreeXMLPersistence INSTANCE = new GuidedDecisionTreeXMLPersistence();

    private XStream xt;

    private GuidedDecisionTreeXMLPersistence() {
        xt = new XStream( new DomDriver() );
    }

    public static GuidedDecisionTreeXMLPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal( final GuidedDecisionTree dtree ) {
        return xt.toXML( dtree );
    }

    public GuidedDecisionTree unmarshal( final String xml ) {
        if ( xml == null || xml.trim().equals( "" ) ) {
            return new GuidedDecisionTree();
        }

        GuidedDecisionTree model = (GuidedDecisionTree) xt.fromXML( xml );
        return model;
    }

}
