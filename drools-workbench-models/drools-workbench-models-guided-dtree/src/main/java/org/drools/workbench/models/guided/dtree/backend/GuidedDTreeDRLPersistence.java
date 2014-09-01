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

import org.drools.workbench.models.commons.backend.imports.ImportsWriter;
import org.drools.workbench.models.commons.backend.packages.PackageNameWriter;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;

/**
 * This takes care of converting GuidedDecisionTree object to DRL
 */
public class GuidedDTreeDRLPersistence {

    public static GuidedDTreeDRLPersistence getInstance() {
        return new GuidedDTreeDRLPersistence();
    }

    public String marshal( final GuidedDecisionTree dtree ) {

        StringBuilder sb = new StringBuilder();

        //Append package name and imports to DRL
        PackageNameWriter.write( sb,
                                 dtree );
        ImportsWriter.write( sb,
                             dtree );

        return sb.toString();
    }

}
