/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.kie.services.impl.query;

import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_DEPLOYMENTID;
import static org.jbpm.services.api.query.QueryResultMapper.COLUMN_EXTERNALID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataprovider.DataSetProviderRegistry;
import org.dashbuilder.dataprovider.sql.SQLDataSetProvider;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookupBuilder;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.SQLDataSetDefBuilder;
import org.dashbuilder.dataset.exception.DataSetLookupException;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.dataset.impl.AbstractDataSetLookupBuilder;
import org.jbpm.kie.services.impl.model.ProcessAssetDesc;
import org.jbpm.kie.services.impl.query.persistence.PersistDataSetListener;
import org.jbpm.kie.services.impl.query.persistence.QueryDefinitionEntity;
import org.jbpm.kie.services.impl.query.preprocessor.BusinessAdminPreprocessor;
import org.jbpm.kie.services.impl.query.preprocessor.BusinessAdminTasksPreprocessor;
import org.jbpm.kie.services.impl.query.preprocessor.DeploymentIdsPreprocessor;
import org.jbpm.kie.services.impl.query.preprocessor.PotOwnerTasksPreprocessor;
import org.jbpm.kie.services.impl.security.DeploymentRolesManager;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.services.api.model.DeployedAsset;
import org.jbpm.services.api.query.QueryAlreadyRegisteredException;
import org.jbpm.services.api.query.QueryNotFoundException;
import org.jbpm.services.api.query.QueryParamBuilder;
import org.jbpm.services.api.query.QueryResultMapper;
import org.jbpm.services.api.query.QueryService;
import org.jbpm.services.api.query.model.QueryDefinition;
import org.jbpm.services.api.query.model.QueryDefinition.Target;
import org.jbpm.services.api.query.model.QueryParam;
import org.jbpm.services.api.service.ServiceRegistry;
import org.jbpm.shared.services.impl.QueryManager;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.QueryNameCommand;
import org.kie.api.runtime.query.AdvancedQueryContext;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.identity.IdentityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;

public class QueryServiceImpl implements QueryService, DeploymentEventListener {

    private static final Logger logger = LoggerFactory.getLogger(QueryServiceImpl.class);

    private DataSetDefRegistry dataSetDefRegistry;
    private DataSetManager dataSetManager;

    private DataSetProviderRegistry providerRegistry;

    private IdentityProvider identityProvider;
    private TransactionalCommandService commandService;

    private DeploymentRolesManager deploymentRolesManager = new DeploymentRolesManager();
    
    
    public QueryServiceImpl() {
        ServiceRegistry.get().register(QueryService.class.getSimpleName(), this);
    }

    public void setDeploymentRolesManager(DeploymentRolesManager deploymentRolesManager) {
        this.deploymentRolesManager = deploymentRolesManager;
    }

    public void setIdentityProvider(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public void setCommandService(TransactionalCommandService commandService) {
        this.commandService = commandService;
    }

    public void setDataSetDefRegistry(DataSetDefRegistry dataSetDefRegistry) {
        this.dataSetDefRegistry = dataSetDefRegistry;
    }

    public void setProviderRegistry(DataSetProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    public void setDataSetManager(DataSetManager dataSetManager) {
        this.dataSetManager = dataSetManager;
    }

    public void init() {
        if (dataSetDefRegistry == null && dataSetManager == null && providerRegistry == null) {
            dataSetDefRegistry = DataSetCore.get().getDataSetDefRegistry();
            dataSetManager = DataSetCore.get().getDataSetManager();
            providerRegistry = DataSetCore.get().getDataSetProviderRegistry();

            providerRegistry.registerDataProvider(SQLDataSetProvider.get());

            dataSetDefRegistry.addListener(new PersistDataSetListener(commandService));
        }

        // load previously registered query definitions
        if (commandService != null) {

            List<QueryDefinitionEntity> queries = commandService.execute(new QueryNameCommand<List<QueryDefinitionEntity>>("getQueryDefinitions"));

            for (QueryDefinitionEntity entity : queries) {
                QueryDefinition definition = entity.toQueryDefinition();
                try {

                    registerQuery(definition);
                } catch (QueryAlreadyRegisteredException e) {
                    logger.debug("Query {} already registered, skipping...", definition.getName());
                }
            }

        }

    }

    @Override
    public void registerQuery(QueryDefinition queryDefinition) throws QueryAlreadyRegisteredException {

        if (dataSetDefRegistry.getDataSetDef(queryDefinition.getName()) != null) {
            throw new QueryAlreadyRegisteredException("Query" + queryDefinition.getName() + " is already registered");
        }
        replaceQuery(queryDefinition);
    }

    @Override
    public void replaceQuery(QueryDefinition queryDefinition) {
        logger.debug("About to register {} query...", queryDefinition);
        if (queryDefinition instanceof SqlQueryDefinition) {
            SqlQueryDefinition sqlQueryDefinition = (SqlQueryDefinition) queryDefinition;

            SQLDataSetDefBuilder<?> builder = DataSetDefFactory.newSQLDataSetDef().uuid(sqlQueryDefinition.getName()).name(sqlQueryDefinition.getName() + "::" + sqlQueryDefinition.getTarget().toString()).dataSource(sqlQueryDefinition.getSource()).dbSQL(sqlQueryDefinition.getExpression(), true);

            DataSetDef sqlDef = builder.buildDef();
            try {
                dataSetDefRegistry.registerDataSetDef(sqlDef);
                DataSetMetadata metadata = dataSetManager.getDataSetMetadata(sqlDef.getUUID());
                if (queryDefinition.getTarget().equals(Target.BA_TASK)) {
                    dataSetDefRegistry.registerPreprocessor(sqlDef.getUUID(), new BusinessAdminTasksPreprocessor(identityProvider, metadata));
                } else if (queryDefinition.getTarget().equals(Target.PO_TASK)) {
                    dataSetDefRegistry.registerPreprocessor(sqlDef.getUUID(), new PotOwnerTasksPreprocessor(identityProvider, metadata));
                } else if (queryDefinition.getTarget().equals(Target.FILTERED_PROCESS)) {
                    dataSetDefRegistry.registerPreprocessor(sqlDef.getUUID(), new DeploymentIdsPreprocessor(deploymentRolesManager, identityProvider, COLUMN_EXTERNALID));
                } else if (queryDefinition.getTarget().equals(Target.FILTERED_BA_TASK)) {
                    dataSetDefRegistry.registerPreprocessor(sqlDef.getUUID(), new BusinessAdminPreprocessor(identityProvider, metadata));
                    dataSetDefRegistry.registerPreprocessor(sqlDef.getUUID(), new DeploymentIdsPreprocessor(deploymentRolesManager, identityProvider, COLUMN_DEPLOYMENTID));
                } else if (queryDefinition.getTarget().equals(Target.FILTERED_PO_TASK)) {
                    dataSetDefRegistry.registerPreprocessor(sqlDef.getUUID(), new PotOwnerTasksPreprocessor(identityProvider, metadata));
                    dataSetDefRegistry.registerPreprocessor(sqlDef.getUUID(), new DeploymentIdsPreprocessor(deploymentRolesManager, identityProvider, COLUMN_DEPLOYMENTID));
                }
                
                for (String columnId : metadata.getColumnIds()) {
                    logger.debug("Column {} is of type {}", columnId, metadata.getColumnType(columnId));
                    sqlDef.addColumn(columnId, metadata.getColumnType(columnId));
                }
    
                logger.info("Registered {} query successfully", queryDefinition.getName());
            } catch (DataSetLookupException e) {
                unregisterQuery(queryDefinition.getName());
                
                throw e;
            }
        }

    }

    @Override
    public void unregisterQuery(final String uniqueQueryName) throws QueryNotFoundException {

        DataSetDef def = dataSetDefRegistry.removeDataSetDef(uniqueQueryName);

        if (def == null) {
            throw new QueryNotFoundException("Query " + uniqueQueryName + " not found");
        }

        logger.info("Unregistered {} query successfully", uniqueQueryName);
    }

    @Override
    public <T> T query(String queryName, QueryResultMapper<T> mapper, QueryContext queryContext, QueryParam... filterParams) throws QueryNotFoundException {
        return query(queryName, mapper, queryContext, new CoreFunctionQueryParamBuilder(filterParams));
    }

    @Override
    public <T> T query(String queryName, QueryResultMapper<T> mapper, QueryContext queryContext, QueryParamBuilder<?> paramBuilder) throws QueryNotFoundException {
        if (dataSetDefRegistry.getDataSetDef(queryName) == null) {
            throw new QueryNotFoundException("Query " + queryName + " not found");
        }
        logger.debug("About to query using {} definition with number of rows {} and starting at {} offset", queryName, queryContext.getCount(), queryContext.getOffset());

        DataSetLookupBuilder<?> builder = DataSetLookupFactory.newDataSetLookupBuilder().dataset(queryName).rowNumber(queryContext.getCount()).rowOffset(queryContext.getOffset());
        Object filter = paramBuilder.build();
        while (filter != null) {
            if (filter instanceof ColumnFilter) {
                // add filter
                builder.filter((ColumnFilter) filter);
            } else if (filter instanceof AggregateColumnFilter) {
                // add aggregate function
                builder.column(((AggregateColumnFilter) filter).getColumnId(), ((AggregateColumnFilter) filter).getType(), ((AggregateColumnFilter) filter).getColumnId());
            } else if (filter instanceof GroupColumnFilter) {
                GroupColumnFilter groupFilter = ((GroupColumnFilter) filter);
                // add group function
                builder.group(((GroupColumnFilter) filter).getColumnId(), ((GroupColumnFilter) filter).getNewColumnId());
                if (groupFilter.getIntervalSize() != null) {
                    ((AbstractDataSetLookupBuilder<?>) builder).dynamic(groupFilter.getMaxIntervals(), DateIntervalType.valueOf(groupFilter.getIntervalSize()), true);
                }
            } else if (filter instanceof ExtraColumnFilter) {
                // add extra column
                builder.column(((ExtraColumnFilter) filter).getColumnId(), ((ExtraColumnFilter) filter).getNewColumnId());
            } else {
                logger.warn("Unsupported filter '{}' generated by '{}'", filter, paramBuilder);
            }
            // call builder again in case more parameters are available
            filter = paramBuilder.build();
        }

        // if advanced ordering is used process the ORDER BY clause into order by and sort order pairs
        if (queryContext instanceof AdvancedQueryContext && ((AdvancedQueryContext) queryContext).getOrderByClause() != null && !((AdvancedQueryContext) queryContext).getOrderByClause().isEmpty()) {
            String[] orderBySortOrderItems = ((AdvancedQueryContext) queryContext).getOrderByClause().split(",");

            for (String orderBySortOrderItem : orderBySortOrderItems) {
                String[] orderBySortOrder = orderBySortOrderItem.trim().split(" ");
                //check that sort order is given.  default to 'asc'.
                String sortOrder = orderBySortOrder.length == 1 ? "asc" : orderBySortOrder[1].trim();
                logger.debug("Applying order by clause '{}' with {}ending sort order", orderBySortOrder[0].trim(), sortOrder);
                builder.sort(orderBySortOrder[0].trim(), sortOrder);
            }
        } else { // use default simple ordering
            if (queryContext.getOrderBy() != null) {
                String[] oderByItems = queryContext.getOrderBy().split(",");

                for (String orderBy : oderByItems) {
                    logger.debug("Applying order by {} and ascending {}", orderBy, queryContext.isAscending());
                    builder.sort(orderBy.trim(), queryContext.isAscending() ? "asc" : "desc");
                }
            }
        }

        DataSet result = dataSetManager.lookupDataSet(builder.buildLookup());
        logger.debug("Query result is {}", result);
        T mappedResult = mapper.map(result);

        logger.debug("Mapped result is {}", mappedResult);
        return mappedResult;
    }

    @Override
    public QueryDefinition getQuery(String uniqueQueryName) throws QueryNotFoundException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", uniqueQueryName);
        List<QueryDefinitionEntity> queries = commandService.execute(new QueryNameCommand<List<QueryDefinitionEntity>>("getQueryDefinitionByName", params));

        if (queries.size() == 1) {
            return queries.get(0).toQueryDefinition();
        }
        throw new QueryNotFoundException("Query " + uniqueQueryName + " not found");
    }

    @Override
    public List<QueryDefinition> getQueries(QueryContext queryContext) {
        List<QueryDefinition> result = new ArrayList<QueryDefinition>();

        Map<String, Object> params = new HashMap<String, Object>();
        applyQueryContext(params, queryContext);
        List<QueryDefinitionEntity> queries = commandService.execute(new QueryNameCommand<List<QueryDefinitionEntity>>("getQueryDefinitions", params));

        for (QueryDefinitionEntity entity : queries) {
            QueryDefinition definition = entity.toQueryDefinition();
            result.add(definition);
        }

        return result;
    }

    protected void applyQueryContext(Map<String, Object> params, QueryContext queryContext) {
        if (queryContext != null) {
            params.put("firstResult", queryContext.getOffset());
            params.put("maxResults", queryContext.getCount());

            if (queryContext.getOrderBy() != null && !queryContext.getOrderBy().isEmpty()) {
                params.put(QueryManager.ORDER_BY_KEY, queryContext.getOrderBy());

                if (queryContext.isAscending()) {
                    params.put(QueryManager.ASCENDING_KEY, "true");
                } else {
                    params.put(QueryManager.DESCENDING_KEY, "true");
                }
            }
        }
    }

    public void onDeploy(DeploymentEvent event) {
        Collection<DeployedAsset> assets = event.getDeployedUnit().getDeployedAssets();
        List<String> roles = null;
        for (DeployedAsset asset : assets) {
            if (asset instanceof ProcessAssetDesc) {
                if (roles == null) {
                    roles = ((ProcessAssetDesc) asset).getRoles();
                }
            }
        }
        if (roles == null) {
            roles = Collections.emptyList();
        }
        deploymentRolesManager.addRolesForDeployment(event.getDeploymentId(), roles);
    }

    public void onUnDeploy(DeploymentEvent event) {

        deploymentRolesManager.removeRolesForDeployment(event.getDeploymentId());
    }

    @Override
    public void onActivate(DeploymentEvent event) {
        // no op
    }

    @Override
    public void onDeactivate(DeploymentEvent event) {
        // no op

    }
}
