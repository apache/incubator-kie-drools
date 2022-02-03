/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.ruleunits.impl;

import java.io.InputStream;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.common.ReteEvaluator;
import org.drools.ruleunits.impl.sessions.RuleUnitExecutorImpl;
import org.drools.core.io.impl.InputStreamResource;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.ruleunits.impl.factory.AbstractRuleUnit;
import org.drools.ruleunits.impl.factory.AbstractRuleUnits;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;

/**
 * A fully-runtime, reflective implementation of a rule unit, useful for testing
 */
public class InterpretedRuleUnit<T extends RuleUnitData> extends AbstractRuleUnit<T> {

    public static <T extends RuleUnitData> RuleUnit<T> of(Class<T> type) {
        return new InterpretedRuleUnit<>(type.getCanonicalName());
    }

    private InterpretedRuleUnit(String id) {
        super(id, DummyRuleUnits.INSTANCE);
    }

    @Override
    public RuleUnitInstance<T> internalCreateInstance(T data) {
        KnowledgeBuilder kBuilder = new KnowledgeBuilderImpl();
        Class<? extends RuleUnitData> wmClass = data.getClass();
        String canonicalName = wmClass.getCanonicalName();

        // transform foo.bar.Baz to /foo/bar/Baz.drl
        // this currently only works for single files
        InputStream resourceAsStream = wmClass.getResourceAsStream(
                String.format("/%s.drl", canonicalName.replace('.', '/')));
        kBuilder.add(new InputStreamResource(resourceAsStream), ResourceType.DRL);

        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addPackages(kBuilder.getKnowledgePackages());
        ReteEvaluator reteEvaluator = new RuleUnitExecutorImpl(kBase);

        return new InterpretedRuleUnitInstance<>(this, data, reteEvaluator);
    }

    public static class DummyRuleUnits extends AbstractRuleUnits {

        static final DummyRuleUnits INSTANCE = new DummyRuleUnits();

        @Override
        protected RuleUnit<?> create(String fqcn) {
            throw new UnsupportedOperationException();
        }
    }
}
