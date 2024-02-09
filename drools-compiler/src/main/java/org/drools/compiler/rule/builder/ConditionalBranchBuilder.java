/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.rule.builder;

import java.util.List;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.ConditionalBranch;
import org.drools.base.rule.EvalCondition;
import org.drools.base.rule.GroupElement;
import org.drools.base.rule.NamedConsequence;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;
import org.drools.compiler.compiler.RuleBuildError;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ConditionalBranchDescr;
import org.drools.drl.ast.descr.EvalDescr;
import org.drools.drl.ast.descr.NamedConsequenceDescr;
import org.drools.drl.parser.DroolsError;

public class ConditionalBranchBuilder implements RuleConditionBuilder {

    public ConditionalBranch build(RuleBuildContext context, BaseDescr descr) {
        return build( context, descr, null );
    }

    public ConditionalBranch build(RuleBuildContext context, BaseDescr descr, Pattern prefixPattern) {
        ConditionalBranchDescr conditionalBranch = (ConditionalBranchDescr) descr;

        String consequenceName = conditionalBranch.getConsequence().getName();
        if ( !context.getRuleDescr().getNamedConsequences().containsKey( consequenceName ) ) {
            DroolsError err = new RuleBuildError( context.getRule(), context.getParentDescr(), null,
                                                  "Unknown consequence name: " + consequenceName );
            context.addError( err  );
            return null;
        }

        RuleConditionBuilder evalBuilder = (RuleConditionBuilder) context.getDialect().getBuilder( EvalDescr.class );
        EvalCondition condition = (EvalCondition) evalBuilder.build(context, conditionalBranch.getCondition(), getLastPattern(context));

        NamedConsequenceBuilder namedConsequenceBuilder = (NamedConsequenceBuilder) context.getDialect().getBuilder( NamedConsequenceDescr.class );
        NamedConsequence consequence = namedConsequenceBuilder.build(context, conditionalBranch.getConsequence());

        ConditionalBranchDescr elseBranchDescr = conditionalBranch.getElseBranch();
        return new ConditionalBranch( condition, consequence, elseBranchDescr != null ? build(context, elseBranchDescr, prefixPattern) : null );
    }

    private Pattern getLastPattern(RuleBuildContext context) {
        GroupElement ge = (GroupElement)context.getDeclarationResolver().peekBuildStack();
        Pattern lastPattern = getLastPattern(ge.getChildren());
        if (lastPattern == null) {
            RuleImpl parent = context.getRule().getParent();
            if (parent != null) {
                lastPattern = getLastPattern(parent.getLhs().getChildren());
            }
        }
        return lastPattern;
    }

    private Pattern getLastPattern(List<RuleConditionElement> siblings) {
        for (int i = siblings.size()-1; i >= 0; i--) {
            RuleConditionElement element = siblings.get(i);
            if (element instanceof Pattern) {
                return (Pattern) element;
            }
        }
        return null;
    }
}
