/*
 * Copyright 2006 JBoss Inc
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
 *
 * Created on Jun 13, 2007
 */
package org.drools.tools.update.drl;

import java.util.Iterator;

import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.util.ReflectiveVisitor;

/**
 * A visitor for a package descriptor structure
 * 
 * @author etirelli
 */
public class DescriptorsVisitor  extends ReflectiveVisitor {
    
    ActionsRegistry actionsRegistry = null;
    
    public DescriptorsVisitor( ActionsRegistry actionsRegistry ) {
        this.actionsRegistry = actionsRegistry;
    }
    
    public void visitPackageDescr(final PackageDescr packageDescr) {
        this.executeActions( packageDescr );
        for ( final Iterator iterator = packageDescr.getRules().iterator(); iterator.hasNext(); ) {
            this.visit( iterator.next() );
        }
    }

    public void visitRuleDescr(final RuleDescr descr) {
        this.executeActions( descr );
    }
    
    private void executeActions( final BaseDescr descr ) {
        for( UpdateAction action : this.actionsRegistry.getActionsForClass( descr.getClass() ) ) {
            action.update( descr );
        }
    }

}
