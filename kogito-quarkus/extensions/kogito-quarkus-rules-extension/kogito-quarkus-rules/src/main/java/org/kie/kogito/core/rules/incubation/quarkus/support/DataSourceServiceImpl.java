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

import java.lang.reflect.Field;
import java.util.UUID;

import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.DataStream;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnits;
import org.drools.ruleunits.impl.datasources.ListDataStore;
import org.drools.ruleunits.impl.factory.DataHandleImpl;
import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.LocalId;
import org.kie.kogito.incubation.rules.RuleUnitId;
import org.kie.kogito.incubation.rules.RuleUnitInstanceId;
import org.kie.kogito.incubation.rules.data.DataId;
import org.kie.kogito.incubation.rules.data.DataSourceId;
import org.kie.kogito.incubation.rules.services.DataSourceService;

/**
 * A very rough implementation that goes straight to the DataStore
 */
class DataSourceServiceImpl implements DataSourceService {

    private final RuleUnits ruleUnits;

    public DataSourceServiceImpl(RuleUnits ruleUnits) {
        this.ruleUnits = ruleUnits;
    }

    @Override
    public DataContext get(DataId id) {
        DataSource<DataContext> dataSource = getDataSource(id);
        if (dataSource instanceof DataStore) {
            ListDataStore ds = (ListDataStore) dataSource;
            DataHandle handle = ds.findHandle(Long.parseLong(id.dataId()));
            return (DataContext) handle.getObject();
        }
        throw new UnsupportedOperationException("Unsupported operation for the given data source type (not a data store)");
    }

    @Override
    public DataId add(LocalId dataSourceId, DataContext ctx) {
        if (dataSourceId instanceof DataSourceId) {
            DataSourceId dsId = (DataSourceId) dataSourceId;
            DataSource<DataContext> dataSource = getDataSource(dsId);
            if (dataSource instanceof DataStore) {
                ListDataStore ds = (ListDataStore) dataSource;
                DataHandleImpl hdl = (DataHandleImpl) ds.add(ctx);
                long handleId = hdl.getId();
                return dsId.data().get(String.valueOf(handleId));
            } else if (dataSource instanceof DataStream) {
                DataStream ds = (DataStream) dataSource;
                ds.append(ctx);
                // for this impl, might as well be always random, we can't retrieve anything anyway
                return dsId.data().get(UUID.randomUUID().toString());
            }
        }
        throw new IllegalArgumentException("Invalid type " + dataSourceId);
    }

    @Override
    public void update(DataId dataId, DataContext ctx) {
        DataSource<DataContext> dataSource = getDataSource(dataId);
        if (dataSource instanceof DataStore) {
            ListDataStore ds = (ListDataStore) dataSource;
            DataHandle handle = ds.findHandle(Long.parseLong(dataId.dataId()));
            ds.update(handle, ctx);
        }
        throw new UnsupportedOperationException("Unsupported operation for the given data source type (not a data store)");
    }

    @Override
    public void remove(DataId dataId) {
        DataSource<DataContext> dataSource = getDataSource(dataId);
        if (dataSource instanceof DataStore) {
            ListDataStore ds = (ListDataStore) dataSource;
            DataHandle handle = ds.findHandle(Long.parseLong(dataId.dataId()));
            ds.remove(handle);
        }
        throw new UnsupportedOperationException("Unsupported operation for the given data source type (not a data store)");
    }

    private DataSource<DataContext> getDataSource(DataSourceId dataSourceId) {
        try {
            RuleUnitInstanceId instanceId = dataSourceId.ruleUnitInstanceId();
            RuleUnitId ruleUnitId = instanceId.ruleUnitId();
            Class<RuleUnitData> ruleUnitDataClass = toClass(ruleUnitId);
            RuleUnitInstance<?> registeredInstance = ruleUnits.getRegisteredInstance(instanceId.ruleUnitInstanceId());
            org.drools.ruleunits.api.RuleUnitData data = registeredInstance.ruleUnitData();
            // KOGITO-6530 Provide a registry of DataSources
            // this may not be necessary at all if we define an actual registry of data sources
            // CDI may also be used for this purpose, when available
            String expectedFieldName = dataSourceId.dataSourceId();
            Field declaredField = ruleUnitDataClass.getDeclaredField(expectedFieldName);
            declaredField.setAccessible(true);
            return (DataSource<DataContext>) declaredField.get(data);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private DataSource<DataContext> getDataSource(DataId dataId) {
        DataSourceId dataSourceId = dataId.dataSourceId();
        return getDataSource(dataSourceId);
    }

    private Class<RuleUnitData> toClass(RuleUnitId ruleUnitId) {
        try {
            return (Class<RuleUnitData>) Thread.currentThread().getContextClassLoader().loadClass(ruleUnitId.ruleUnitId());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
