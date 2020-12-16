/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.rules.units;

import java.io.InputStream;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.io.impl.InputStreamResource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.kogito.Config;
import org.kie.kogito.KogitoEngine;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitData;
import org.kie.kogito.rules.RuleUnitInstance;
import org.kie.kogito.rules.units.impl.AbstractRuleUnit;
import org.kie.kogito.rules.units.impl.AbstractRuleUnits;
import org.kie.kogito.uow.UnitOfWorkManager;

/**
 * A fully-runtime, reflective implementation of a rule unit, useful for testing
 */
public class InterpretedRuleUnit<T extends RuleUnitData> extends AbstractRuleUnit<T> {

    public static <T extends RuleUnitData> RuleUnit<T> of(Class<T> type) {
        return new InterpretedRuleUnit<>(type.getCanonicalName());
    }

    private InterpretedRuleUnit(String id) {
        super(id, DummyApplication.INSTANCE);
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
        KieSession kSession = kBase.newKieSession();

        return new InterpretedRuleUnitInstance<>(this, data, kSession);
    }

    public static class DummyApplication implements org.kie.kogito.Application {

        static final DummyApplication INSTANCE = new DummyApplication();

        RuleUnits ruleUnits = new RuleUnits();

        public Config config() {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends KogitoEngine> T get(Class<T> clazz) {
            if(clazz.isAssignableFrom(org.kie.kogito.rules.RuleUnits.class)) {
                return (T) ruleUnits;
            }
            return null;
        }

        public UnitOfWorkManager unitOfWorkManager() {
            return null;
        }

        public static class RuleUnits extends AbstractRuleUnits {
            @Override
            protected RuleUnit<?> create( String fqcn ) {
                throw new UnsupportedOperationException();
            }
        }
    }
}
