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
package org.kie.kogito.core.rules.incubation.quarkus.support;

import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.kogito.incubation.common.ExtendedDataContext;
import org.kie.kogito.incubation.common.ExtendedReferenceContext;
import org.kie.kogito.incubation.common.LocalId;
import org.kie.kogito.incubation.common.MetaDataContext;
import org.kie.kogito.incubation.rules.services.StatefulRuleUnitService;
import org.kie.kogito.rules.RuleUnits;

@ApplicationScoped
public class QuarkusStatefulRuleUnitService implements StatefulRuleUnitService {
    @Inject
    Instance<RuleUnits> ruleUnits;
    StatefulRuleUnitServiceImpl delegate;

    @PostConstruct
    void startup() {
        delegate = new StatefulRuleUnitServiceImpl(ruleUnits.get());
    }

    @Override
    public MetaDataContext create(LocalId localId, ExtendedReferenceContext extendedDataContext) {
        return delegate.create(localId, extendedDataContext);
    }

    @Override
    public MetaDataContext dispose(LocalId localId) {
        return delegate.dispose(localId);
    }

    @Override
    public MetaDataContext fire(LocalId localId) {
        return delegate.fire(localId);
    }

    @Override
    public Stream<ExtendedDataContext> query(LocalId localId, ExtendedReferenceContext params) {
        return delegate.query(localId, params);
    }
}
