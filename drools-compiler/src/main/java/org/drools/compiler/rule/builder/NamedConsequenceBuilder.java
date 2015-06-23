/*
 * Copyright 2015 JBoss Inc
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

package org.drools.compiler.rule.builder;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.NamedConsequenceDescr;
import org.drools.core.rule.NamedConsequence;
import org.drools.core.rule.Pattern;

public class NamedConsequenceBuilder implements RuleConditionBuilder {

    public NamedConsequence build(RuleBuildContext context, BaseDescr descr) {
        return build( context, descr, null );
    }

    public NamedConsequence build(RuleBuildContext context, BaseDescr descr, Pattern prefixPattern) {
        NamedConsequenceDescr namedConsequence = (NamedConsequenceDescr) descr;
        return new NamedConsequence( namedConsequence.getName(), namedConsequence.isBreaking() );
    }
}
