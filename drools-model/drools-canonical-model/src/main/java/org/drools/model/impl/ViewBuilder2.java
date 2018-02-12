/*
 * Copyright 2005 JBoss Inc
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

package org.drools.model.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.drools.model.Condition;
import org.drools.model.Consequence;
import org.drools.model.DSL2.PatternDef;
import org.drools.model.DSL2.PatternExprImpl;
import org.drools.model.DSL2.PatternItem;
import org.drools.model.RuleItem;
import org.drools.model.RuleItemBuilder;
import org.drools.model.consequences.NamedConsequenceImpl;
import org.drools.model.patterns.CompositePatterns;
import org.drools.model.patterns.PatternImpl;

import static java.util.stream.Collectors.toList;

import static org.drools.model.impl.NamesGenerator.generateName;

public class ViewBuilder2 {
    private ViewBuilder2() { }

    public static CompositePatterns viewItems2Patterns( RuleItemBuilder<?>[] viewItemBuilders ) {
        List<RuleItem> ruleItems = Stream.of( viewItemBuilders ).map( RuleItemBuilder::get ).collect( toList() );
        Iterator<RuleItem> ruleItemIterator = ruleItems.iterator();

        List<Condition> conditions = new ArrayList<>();
        Map<String, Consequence> consequences = new LinkedHashMap<>();

        while (ruleItemIterator.hasNext()) {
            RuleItem ruleItem = ruleItemIterator.next();

            if (ruleItem instanceof Consequence) {
                Consequence consequence = (Consequence) ruleItem;
                String name = ruleItemIterator.hasNext() ? generateName("consequence") : RuleImpl.DEFAULT_CONSEQUENCE_NAME;
                consequences.put(name, consequence);
                conditions.add( new NamedConsequenceImpl( name, consequence.isBreaking() ) );
                continue;
            }

            if (ruleItem instanceof PatternDef) {
                PatternDef patternDef = (PatternDef) ruleItem;
                PatternImpl pattern = new PatternImpl(patternDef.getVariable());
                for (PatternItem patternItem : patternDef.getItems()) {
                    if (patternItem instanceof PatternExprImpl) {
                        pattern.addConstraint( (( PatternExprImpl ) patternItem).asConstraint( patternDef ) );
                    }
                }
                conditions.add(pattern);
            }
        }

        return new CompositePatterns( Condition.Type.AND, conditions, null, consequences );
    }
}
