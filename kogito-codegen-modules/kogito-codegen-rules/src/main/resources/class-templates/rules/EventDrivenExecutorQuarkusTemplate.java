/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package com.myspace.demo;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.eventdriven.rules.AbstractEventDrivenQueryExecutor;
import org.drools.ruleunits.api.RuleUnit;
import org.kie.kogito.eventdriven.rules.EventDrivenRulesController;
import io.quarkus.runtime.Startup;


@ApplicationScoped
@Startup
public class $QueryType$EventDrivenExecutor extends AbstractEventDrivenQueryExecutor<$DataType$> {

    @Inject
    RuleUnit<$DataType$> ruleUnit;
    
    @Inject
    EventDrivenRulesController controller;

    @PostConstruct
    private void onPostConstruct() {
        setup(controller, ruleUnit, "$name$", $QueryType$::execute, $DataType$.class);
    }
}
