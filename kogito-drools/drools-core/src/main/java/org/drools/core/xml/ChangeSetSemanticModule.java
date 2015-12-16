/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.xml;

import org.drools.core.xml.changeset.AddHandler;
import org.drools.core.xml.changeset.ChangeSetHandler;
import org.drools.core.xml.changeset.DecisionTableConfigurationHandler;
import org.drools.core.xml.changeset.DefinitionHandler;
import org.drools.core.xml.changeset.ModifyHandler;
import org.drools.core.xml.changeset.RemoveHandler;
import org.drools.core.xml.changeset.ResourceHandler;

public class ChangeSetSemanticModule  extends DefaultSemanticModule implements SemanticModule {

    public ChangeSetSemanticModule() {
        super( "http://drools.org/drools-5.0/change-set" );

        addHandler( "change-set",
                    new ChangeSetHandler() );
        
        addHandler( "add",
                    new AddHandler() );
        
        addHandler( "remove",
                    new RemoveHandler() );
        
        addHandler( "modify",
                    new ModifyHandler() );
        
        addHandler( "resource",
                    new ResourceHandler() );

        addHandler( "definition",
                    new DefinitionHandler() );
        
        addHandler( "decisiontable-conf",
                    new DecisionTableConfigurationHandler() );
    }
    
}
