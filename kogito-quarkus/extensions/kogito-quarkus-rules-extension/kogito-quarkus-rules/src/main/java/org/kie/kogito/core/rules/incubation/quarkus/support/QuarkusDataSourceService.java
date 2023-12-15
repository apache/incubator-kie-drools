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

import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.LocalId;
import org.kie.kogito.incubation.rules.data.DataId;
import org.kie.kogito.incubation.rules.services.DataSourceService;
import org.kie.kogito.rules.RuleUnits;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuarkusDataSourceService implements DataSourceService {
    @Inject
    Instance<RuleUnits> ruleUnits;
    DataSourceServiceImpl dataSourceService;

    @PostConstruct
    void setup() {
        dataSourceService = new DataSourceServiceImpl(ruleUnits.get());
    }

    @Override
    public DataContext get(DataId id) {
        return dataSourceService.get(id);
    }

    @Override
    public DataId add(LocalId dataSourceId, DataContext ctx) {
        return dataSourceService.add(dataSourceId, ctx);
    }

    @Override
    public void update(DataId dataId, DataContext ctx) {
        dataSourceService.update(dataId, ctx);
    }

    @Override
    public void remove(DataId dataId) {
        dataSourceService.remove(dataId);
    }
}
