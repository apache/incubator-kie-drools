/*
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
package org.kie.kogito.quarkus.drools;

import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;
import org.kie.kogito.incubation.common.ReferenceContext;

import io.quarkus.arc.Unremovable;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@Unremovable // temporary workaround to allow injecting RuleUnitInstance<AnotherService>
             // without injecting AnotherService directly (otherwise Quarkus ArC will ignore this)
             // KOGITO-6529 Quarkus extension should make RuleUnitData/DataContext @Unremovable
public class AnotherService implements RuleUnitData, ReferenceContext {
    @Inject
    DataStore<StringHolder> strings;
    @Inject
    DataStore<StringHolder> greetings;

    protected AnotherService() {
    }

    public AnotherService(DataStore<StringHolder> strings, DataStore<StringHolder> greetings) {
        this.strings = strings;
        this.greetings = greetings;
    }

    public DataStore<StringHolder> getStrings() {
        return strings;
    }

    public DataStore<StringHolder> getGreetings() {
        return greetings;
    }

}
