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
package org.kie.kogito.incubation.rules.services;

import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.LocalId;
import org.kie.kogito.incubation.rules.data.DataId;

/**
 * this may also act internally as a registry (?)
 */
public interface DataSourceService {
    /**
     * @param id identifier of the data source
     * @param ctx data that should be inserted into the data source
     * @return id of the inserted fact
     */
    // "/data-sources/my-data-source", val
    DataId add(LocalId id, DataContext ctx);

    // "/data-sources/7574598375943/data/7525792847584395"
    DataContext get(DataId id);

    void update(DataId id, DataContext ctx);

    void remove(DataId id);
}
