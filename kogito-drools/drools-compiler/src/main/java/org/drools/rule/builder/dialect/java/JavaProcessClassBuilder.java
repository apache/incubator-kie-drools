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

package org.drools.rule.builder.dialect.java;

import java.util.Iterator;

import org.drools.lang.descr.ProcessDescr;
import org.drools.rule.ImportDeclaration;
import org.drools.rule.builder.ProcessBuildContext;
import org.drools.rule.builder.ProcessClassBuilder;
import org.drools.util.StringUtils;

/**
 * @author etirelli
 *
 */
public class JavaProcessClassBuilder
    implements
    ProcessClassBuilder {

    /* (non-Javadoc)
     * @see org.drools.rule.builder.dialect.java.RuleClassBuilder#buildRule(org.drools.rule.builder.BuildContext, org.drools.rule.builder.dialect.java.BuildUtils, org.drools.lang.descr.RuleDescr)
     */
    public String  buildRule(final ProcessBuildContext context) {
        // If there is no compiled code, return
        if ( context.getMethods().isEmpty() ) {
            return null;
        }
        
        final String lineSeparator = System.getProperty( "line.separator" );

        final StringBuilder buffer = new StringBuilder();
        buffer.append( "package " + context.getPkg().getName() + ";" + lineSeparator );

        for ( ImportDeclaration decl : context.getPkg().getImports().values() ) {
            buffer.append( "import " +  decl.getTarget() + ";" + lineSeparator );
        }

        for ( final Iterator it = context.getPkg().getStaticImports().iterator(); it.hasNext(); ) {
            buffer.append( "import static " + it.next() + ";" + lineSeparator );
        }

        final ProcessDescr processDescr = context.getProcessDescr();
        
        buffer.append( "public class " + StringUtils.ucFirst( processDescr.getClassName() ) + " {" + lineSeparator );
        buffer.append( "    private static final long serialVersionUID = 400L;" + lineSeparator );

        // @TODO record line numbers for each Action method
        for ( int i = 0, size = context.getMethods().size(); i < size; i++ ) {
            buffer.append( context.getMethods().get( i ) + lineSeparator );           
        }

        final String[] lines = buffer.toString().split( lineSeparator );

        buffer.append( "}" );

        return buffer.toString();
    }
}
