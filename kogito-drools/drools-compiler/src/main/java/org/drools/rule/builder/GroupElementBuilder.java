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

package org.drools.rule.builder;

import java.util.Iterator;

import org.drools.RuntimeDroolsException;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.rule.Column;
import org.drools.rule.ConditionalElement;
import org.drools.rule.GroupElement;
import org.drools.rule.GroupElementFactory;

/**
 * @author etirelli
 *
 */
public class GroupElementBuilder
    implements
    ConditionalElementBuilder {

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConditionalElementBuilder#build(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.semantics.java.builder.ColumnBuilder, org.drools.lang.descr.BaseDescr)
     */
    public ConditionalElement build(BuildContext context,
                                    BuildUtils utils,
                                    ColumnBuilder columnBuilder,
                                    BaseDescr descr) {
        ConditionalElementDescr cedescr = (ConditionalElementDescr) descr;

        final GroupElement ge = this.newGroupElementFor( cedescr.getClass() );
        context.getBuildStack().push( ge );

        // iterate over child descriptors
        for ( Iterator it = cedescr.getDescrs().iterator(); it.hasNext(); ) {
            // gets child to build
            BaseDescr child = (BaseDescr) it.next();

            // gets corresponding builder
            ConditionalElementBuilder cebuilder = utils.getBuilder( child.getClass() );

            if ( cebuilder != null ) {
                ConditionalElement ce = cebuilder.build( context,
                                                         utils,
                                                         columnBuilder,
                                                         (BaseDescr) child );
                if( ce != null ) {
                    ge.addChild( ce );
                }
            } else if ( child instanceof ColumnDescr ) {
                final Column column = columnBuilder.build( context,
                                                           utils,
                                                           (ColumnDescr) child );
                // in case there is a problem with the column building,
                // builder will return null. Ex: ClassNotFound for the column type
                if( column != null ) {
                    ge.addChild( column );
                }

            } else {
                throw new RuntimeDroolsException("BUG: no builder found for descriptor class "+child.getClass() );
            }

        }

        context.getBuildStack().pop();
        
        return ge;
    }

    private GroupElement newGroupElementFor(Class descr) {
        if ( AndDescr.class.isAssignableFrom( descr ) ) {
            return GroupElementFactory.newAndInstance();
        } else if ( OrDescr.class.isAssignableFrom( descr ) ) {
            return GroupElementFactory.newOrInstance();
        } else if ( NotDescr.class.isAssignableFrom( descr ) ) {
            return GroupElementFactory.newNotInstance();
        } else if ( ExistsDescr.class.isAssignableFrom( descr ) ) {
            return GroupElementFactory.newExistsInstance();
        } else {
            throw new RuntimeDroolsException( "BUG: Not able to create a group element for descriptor: " + descr.getName() );
        }
    }

}
