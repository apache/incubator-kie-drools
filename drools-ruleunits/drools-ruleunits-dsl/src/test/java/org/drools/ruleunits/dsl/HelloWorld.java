/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.drools.ruleunits.dsl;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.Index;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;

public class HelloWorld implements RuleUnitDefinition {

    private final DataStore<String> strings;
    private final List<String> results = new ArrayList<>();

    public HelloWorld() {
        this(DataSource.createStore());
    }

    public HelloWorld(DataStore<String> strings) {
        this.strings = strings;
    }

    public DataStore<String> getStrings() {
        return strings;
    }

    public List<String> getResults() {
        return results;
    }

    @Override
    public void defineRules(RulesContext rulesContext) {
        rulesContext.addRule()
                .on(strings, String.class).check(Index.ConstraintType.EQUAL, "Hello World")
                .on(results).execute(r -> r.add("it worked!"));

        rulesContext.addRule()
                .on(strings, String.class).check(s -> s.length(), Index.ConstraintType.GREATER_THAN, 5)
                .on(results).execute(r -> r.add("it also worked!"));

        rulesContext.addRule()
                .on(strings, String.class).check(s -> s.length(), Index.ConstraintType.LESS_THAN, 5)
                .on(results).execute(r -> r.add("this shouldn't fire"));
    }
}
