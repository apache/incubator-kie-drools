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
package org.kie.kogito.core.rules.incubation.quarkus.support;

import java.util.stream.Stream;

import org.drools.ruleunits.api.RuleUnits;
import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.Id;
import org.kie.kogito.incubation.rules.services.RuleUnitService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuarkusRuleUnitService implements RuleUnitService {

    @Inject
    Instance<RuleUnits> ruleUnits;
    RuleUnitServiceImpl delegate;

    @PostConstruct
    void startup() {
        this.delegate = new RuleUnitServiceImpl(ruleUnits.get());
    }

    @Override
    public Stream<DataContext> evaluate(Id id, DataContext inputContext) {
        return this.delegate.evaluate(id, inputContext);
    }

}
