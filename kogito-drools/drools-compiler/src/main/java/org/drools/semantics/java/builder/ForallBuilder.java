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
 */

package org.drools.semantics.java.builder;

import java.util.Iterator;

import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.rule.Column;
import org.drools.rule.ConditionalElement;
import org.drools.rule.Forall;

/**
 * @author etirelli
 *
 */
public class ForallBuilder
    implements
    ConditionalElementBuilder {

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConditionalElementBuilder#build(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.semantics.java.builder.ColumnBuilder, org.drools.lang.descr.BaseDescr)
     */
    public ConditionalElement build(BuildContext context,
                                    BuildUtils utils,
                                    ColumnBuilder columnBuilder,
                                    BaseDescr descr) {
        ForallDescr forallDescr = (ForallDescr) descr;
        
        Column baseColumn = columnBuilder.build( context, utils, forallDescr.getBaseColumn() );

        if ( baseColumn == null ) {
            return null;
        }

        Forall forall = new Forall( baseColumn );
        
        // adding the newly created forall CE to the build stack
        // this is necessary in case of local declaration usage
        context.getBuildStack().push( forall );
        
        for( Iterator it = forallDescr.getRemainingColumns().iterator(); it.hasNext(); ) {
            Column anotherColumn = columnBuilder.build( context, utils, (ColumnDescr) it.next() );
            forall.addRemainingColumn( anotherColumn );
        }
        
        // poping the forall
        context.getBuildStack().pop();
        
        return forall;
    }

}
