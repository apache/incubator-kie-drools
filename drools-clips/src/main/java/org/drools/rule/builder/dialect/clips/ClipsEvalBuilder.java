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

package org.drools.rule.builder.dialect.clips;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.base.mvel.MVELEvalExpression;
import org.drools.compiler.Dialect;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.MVELDialectData;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleConditionBuilder;
import org.drools.rule.builder.dialect.mvel.MVELEvalBuilder;
import org.drools.spi.DeclarationScopeResolver;
import org.mvel.compiler.AbstractParser;
import org.mvel.compiler.ExpressionCompiler;
import org.mvel.MVEL;
import org.mvel.ParserContext;
import org.mvel.util.ParseTools;

/**
 * @author etirelli
 *
 */
public class ClipsEvalBuilder extends MVELEvalBuilder
    implements
    RuleConditionBuilder {

    public RuleConditionElement build(final RuleBuildContext context,
                                      final BaseDescr descr) {
        return build( context,
                      descr,
                      null );
    }

    /**
     * Builds and returns an Eval Conditional Element
     * 
     * @param context The current build context
     * @param utils The current build utils instance
     * @param patternBuilder not used by EvalBuilder
     * @param descr The Eval Descriptor to build the eval conditional element from
     * 
     * @return the Eval Conditional Element
     */
    public RuleConditionElement build(final RuleBuildContext context,
                                      final BaseDescr descr,
                                      final Pattern prefixPattern) {
        return super.build(context, descr, prefixPattern);
    }

}
