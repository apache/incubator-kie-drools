/**
 * 
 */
package org.drools.xml;

import org.drools.xml.composition.CompositionHandler;
import org.drools.xml.composition.DecisionTableConfigurationHandler;
import org.drools.xml.composition.ResourceHandler;

public class CompositionSemanticModule  extends DefaultSemanticModule implements SemanticModule {

    public CompositionSemanticModule() {
        super( "http://drools.org/drools-4.0/composition" );

        addHandler( "composition",
                    new CompositionHandler() );
        
        addHandler( "resource",
                    new ResourceHandler() );  
        
        addHandler( "decisiontable-conf",
                    new DecisionTableConfigurationHandler() );             
    }
    
}