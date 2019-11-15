/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ruleunit;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.ruleunit.RuleUnitDescriptionLoader;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.ruleunit.RuleUnitDescription;

import static org.drools.core.ruleunit.RuleUnitDescriptionRegistry.State;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RuleUnitDescriptionLoaderTest {

    private RuleUnitDescriptionLoader loader;

    @Before
    public void prepareRuleUnitDescriptionLoader() {
        KnowledgePackageImpl pkg = new KnowledgePackageImpl("org.test");
        pkg.setClassLoader(this.getClass().getClassLoader());
        loader = new RuleUnitDescriptionLoader(pkg);
    }

    @Test
    public void getStateUnitExists() {
        Assertions.assertThat(loader.getState()).isEqualTo(State.UNKNOWN);
        assertDescriptionIsLoaded();
        Assertions.assertThat(loader.getState()).isEqualTo(State.UNIT);
    }

    @Test
    public void getStateUnitNotExists() {
        Assertions.assertThat(loader.getState()).isEqualTo(State.UNKNOWN);
        final Optional<RuleUnitDescription> description = loader.getDescription("nonexisting");
        Assertions.assertThat(description).isNotPresent();
        Assertions.assertThat(loader.getState()).isEqualTo(State.NO_UNIT);
    }

    @Test
    public void getStateMixWithAndWithoutUnit() {
        Assertions.assertThat(loader.getState()).isEqualTo(State.UNKNOWN);
        assertDescriptionIsLoaded();
        Assertions.assertThat(loader.getState()).isEqualTo(State.UNIT);
        Assertions.assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> loader.getDescription("nonexisting"));
    }

    @Test
    public void getDescriptions() {
        Assertions.assertThat(loader.getDescriptions()).isEmpty();
        assertDescriptionIsLoaded();
        Assertions.assertThat(loader.getDescriptions()).hasSize(1);
    }

    @Test
    public void getDescriptionFromRuleImpl() {
        final RuleImpl ruleImpl = mock(RuleImpl.class);
        when(ruleImpl.getRuleUnitClassName()).thenReturn(TestRuleUnit.class.getName());

        final Optional<RuleUnitDescription> description = loader.getDescription(ruleImpl);
        Assertions.assertThat(description).isPresent();
        final Optional<RuleUnitDescription> description2 = loader.getDescription(ruleImpl);
        Assertions.assertThat(description).isPresent();
        Assertions.assertThat(description.get()).isSameAs(description2.get());
        Assertions.assertThat(description.get().getRuleUnitClass()).isEqualTo(TestRuleUnit.class);
    }

    @Test
    public void getDescriptionFromUnitClassName() {
        final RuleUnitDescription description = assertDescriptionIsLoaded();
        final RuleUnitDescription description2 = assertDescriptionIsLoaded();
        Assertions.assertThat(description).isSameAs(description2);
        Assertions.assertThat(description.getRuleUnitClass()).isEqualTo(TestRuleUnit.class);
    }

    private RuleUnitDescription assertDescriptionIsLoaded() {
        final Optional<RuleUnitDescription> description = loader.getDescription(TestRuleUnit.class.getName());
        Assertions.assertThat(description).isPresent();
        return description.get();
    }
}