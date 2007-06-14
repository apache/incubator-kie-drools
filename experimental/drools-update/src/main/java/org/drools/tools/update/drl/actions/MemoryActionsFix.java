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
package org.drools.tools.update.drl.actions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.tools.update.drl.UpdateAction;

/**
 * @author etirelli
 *
 */
public class MemoryActionsFix
    implements
    UpdateAction {
    
    static Pattern MODIFY               = Pattern.compile( "(.*\\b)modify(\\s*\\(([^)]+)\\)(\\s*;.*))",
                                                           Pattern.DOTALL );
    static Pattern ASSERT               = Pattern.compile( "(.*\\b)assert(\\s*\\((.*)\\)(\\s*;.*))",
                                                           Pattern.DOTALL );
    static Pattern ASSERT_LOGICAL       = Pattern.compile( "(.*\\b)assertLogical(\\s*\\((.*)\\)(\\s*;.*))",
                                                           Pattern.DOTALL );
    

    public void update(BaseDescr descr) {
        RuleDescr rule = (RuleDescr) descr;
        String consequence = (String) rule.getConsequence();
        
        Matcher m = MODIFY.matcher( consequence );
        consequence = m.replaceAll( "$1update$2" );
        
        m = ASSERT.matcher( consequence );
        consequence = m.replaceAll( "$1insert$2" );
        
        m = ASSERT_LOGICAL.matcher( consequence );
        consequence = m.replaceAll( "$1insertLogical$2" );
        
        rule.setConsequence( consequence );
    }

}
