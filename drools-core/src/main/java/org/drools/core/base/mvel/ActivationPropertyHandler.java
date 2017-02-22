/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base.mvel;

import org.drools.core.common.AgendaItem;
import org.drools.core.rule.Declaration;
import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.VariableResolverFactory;

public class ActivationPropertyHandler implements PropertyHandler {    
    public ActivationPropertyHandler() {
    }  
    
    public Object getProperty(String name,
                              Object obj,
                              VariableResolverFactory variableFactory) {
        AgendaItem item = ( AgendaItem ) obj;
        if ( "rule".equals( name ) ) {
            return item.getRule();
        } else if ( "active".equals( name ) ) {
            return item.isQueued();
        } else if ( "objects".equals( name ) ) {
            return item.getObjects();
        } else if ( "factHandles".equals( name ) ) {
            return item.getFactHandles();
        } else if ( "declarationIds".equals( name ) ) {
            return item.getDeclarationIds();
        } else if ( "this".equals( name ) ) {
            return item;
        }

        // FIXME hack as MVEL seems to be ignoring indexed variables
        VariableResolver vr = variableFactory.getNextFactory().getVariableResolver( name );
        if ( vr != null ) {
            return vr.getValue();
        }
                
        Declaration declr = item.getTerminalNode().getSubRule().getOuterDeclarations().get( name );
        if ( declr != null ) {
            return declr.getValue( null, item.getTuple().get( declr ).getObject() );
        } else {
            return item.getRule().getMetaData( name );
        }
    }

    public Object setProperty(String name,
                              Object contextObj,
                              VariableResolverFactory variableFactory,
                              Object value) {
        throw new IllegalArgumentException( "Cannot set " + name + " as activations are immutable." );
    }

}
