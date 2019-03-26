/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.xes.dataset;

import java.util.List;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.sql.SQLDataSetProvider;
import org.dashbuilder.dataset.*;
import org.dashbuilder.dataset.def.*;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.sort.SortOrder;
import org.jbpm.xes.model.QueryDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSetServiceImpl implements DataSetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSetServiceImpl.class);

    private DataSetDefRegistry dataSetDefRegistry = DataSetCore.get().getDataSetDefRegistry();
    private DataSetManager dataSetManager = DataSetCore.get().getDataSetManager();
    private DataSetProviderRegistry providerRegistry = DataSetCore.get().getDataSetProviderRegistry();

    public DataSetServiceImpl(Supplier<DataSource> dataSourceResolver) {
        final SQLDataSetProvider sqlDataSetProvider = SQLDataSetProvider.get();
        sqlDataSetProvider.setDataSourceLocator(def -> dataSourceResolver.get());
        this.providerRegistry.registerDataProvider(sqlDataSetProvider);
        final List<QueryDefinition> queryDefinitions = QueryDefinitionLoader.get().loadQueryDefinitions();
        queryDefinitions.forEach(q -> registerDataSetDefinition(q));
    }

    protected void registerDataSetDefinition(final QueryDefinition queryDefinition) {
        LOGGER.info("Loaded query definition: {}", queryDefinition);
        SQLDataSetDefBuilder<?> builder = DataSetDefFactory.newSQLDataSetDef()
                .uuid(queryDefinition.getName())
                .name(queryDefinition.getTarget() + "-" + queryDefinition.getName())
                .dataSource("xes")
                .dbSQL(queryDefinition.getExpression(), true);

        DataSetDef dataSetDef = builder.buildDef();

        dataSetDef.setPublic(false);

        dataSetDefRegistry.registerDataSetDef(dataSetDef);
        LOGGER.info("Data Set registered {}", dataSetDef);
    }

    @Override
    public DataSet findTraces(ColumnFilter... filters) {
        DataSetLookupBuilder<?> builder = DataSetLookupFactory.newDataSetLookupBuilder().dataset("jbpmXESTraces");
        builder.filter(filters);
        builder.sort("id", SortOrder.ASCENDING);
        DataSet result = dataSetManager.lookupDataSet(builder.buildLookup());
        LOGGER.debug("Data set query result: {}", result);
        return result;
    }

    @Override
    public DataSet findEvents(ColumnFilter... filters) {
        DataSetLookupBuilder<?> builder = DataSetLookupFactory.newDataSetLookupBuilder().dataset("jbpmXESEvents");
        builder.filter(filters);
        builder.sort("processInstanceId", SortOrder.ASCENDING);
        builder.sort("log_date", SortOrder.ASCENDING);
        DataSet result = dataSetManager.lookupDataSet(builder.buildLookup());
        LOGGER.debug("Data set query result: {}", result);
        return result;
    }
}
