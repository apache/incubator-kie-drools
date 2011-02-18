/*
 * Copyright 2010 JBoss Inc
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

package org.drools.rule.builder.dialect.clips;

import java.util.Iterator;
import java.util.List;

import org.drools.clips.Appendable;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispForm;
import org.drools.clips.StringBuilderAppendable;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.dialect.mvel.MVELConsequenceBuilder;

public class ClipsConsequenceBuilder extends MVELConsequenceBuilder  {

	public void build(final RuleBuildContext context, String consequenceName) {
		// TODO does not support named consequences
        Appendable builder = new StringBuilderAppendable();
        
        List list = (List) context.getRuleDescr().getConsequence();
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            FunctionHandlers.dump( (LispForm) it.next(),
                                   builder,
                                   true );
        }
        
        context.getRuleDescr().setConsequence( builder.toString() );
        
        super.build(  context, consequenceName );
        
//        Rule rule = context.getRule();
//        BlockExecutionEngine rhs = ( BlockExecutionEngine ) context.getRuleDescr().getConsequence();
//        Map vars = new HashMap();
//        
//        for(Declaration declaration : rule.getDeclarations() ) {
//            vars.put( declaration.getIdentifier(), declaration );
//        }
//        
//        rhs.replaceTempTokens( vars );
//        rule.setConsequence( rhs );
    }

}
