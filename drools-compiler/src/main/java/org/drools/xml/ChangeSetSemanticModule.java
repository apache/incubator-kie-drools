/**
 * 
 */
package org.drools.xml;

import org.drools.xml.changeset.AddHandler;
import org.drools.xml.changeset.ChangeSetHandler;
import org.drools.xml.changeset.DecisionTableConfigurationHandler;
import org.drools.xml.changeset.ModifyHandler;
import org.drools.xml.changeset.RemoveHandler;
import org.drools.xml.changeset.ResourceHandler;

public class ChangeSetSemanticModule  extends DefaultSemanticModule implements SemanticModule {

    public ChangeSetSemanticModule() {
        super( "http://drools.org/drools-5.0/change-set" );

        addHandler( "change-set",
                    new ChangeSetHandler() );
        
        addHandler( "add",
                    new AddHandler() ); 
        
        addHandler( "removed",
                    new RemoveHandler() );
        
        addHandler( "modified",
                    new ModifyHandler() );        
        
        addHandler( "resource",
                    new ResourceHandler() );  
        
        addHandler( "decisiontable-conf",
                    new DecisionTableConfigurationHandler() );             
    }
    
}