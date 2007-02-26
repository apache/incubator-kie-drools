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

import org.drools.lang.descr.RuleDescr;

/**
 * @author etirelli
 *
 */
public class RuleClassBuilder {

    public void buildRule(final BuildContext context,
                           final BuildUtils utils,
                           final RuleDescr ruleDescr) {
        // If there is no compiled code, return
        if ( context.getMethods().isEmpty() ) {
            context.setRuleClass( null );
            return;
        }
        final String lineSeparator = System.getProperty( "line.separator" );

        final StringBuffer buffer = new StringBuffer();
        buffer.append( "package " + context.getPkg().getName() + ";" + lineSeparator );

        for ( final Iterator it = context.getPkg().getImports().iterator(); it.hasNext(); ) {
            buffer.append( "import " + it.next() + ";" + lineSeparator );
        }
        
        for ( final Iterator it = context.getPkg().getStaticImports().iterator(); it.hasNext(); ) {
            buffer.append( "import static " + it.next() + ";" + lineSeparator );
        }        

        buffer.append( "public class " + utils.ucFirst( ruleDescr.getClassName() ) + " {" + lineSeparator );
        buffer.append( "    private static final long serialVersionUID  = 320L;" + lineSeparator );

        for ( int i = 0, size = context.getMethods().size() - 1; i < size; i++ ) {
            buffer.append( context.getMethods().get( i ) + lineSeparator );
        }

        final String[] lines = buffer.toString().split( lineSeparator );

        ruleDescr.setConsequenceOffset( lines.length + 1 );

        buffer.append( context.getMethods().get( context.getMethods().size() - 1 ) + lineSeparator );
        buffer.append( "}" );

        context.setRuleClass( buffer.toString() );
    }
}
