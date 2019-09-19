/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.infinispan.query;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.Search;
import org.infinispan.query.dsl.FilterConditionContextQueryBuilder;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryBuilder;
import org.infinispan.query.dsl.QueryFactory;
import org.kie.kogito.index.infinispan.cache.InfinispanCacheManager;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.query.ProcessInstanceFilter;
import org.kie.kogito.index.query.QueryService;
import org.kie.kogito.index.query.UserTaskInstanceFilter;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class InfinispanQueryService implements QueryService {

    @Inject
    InfinispanCacheManager manager;

    @Override
    public Collection<JsonObject> queryDomain(String domain, String query) {
        if (query == null) {
            return manager.getDomainModelCache(domain).values();
        } else {
            QueryFactory qf = Search.getQueryFactory((RemoteCache) manager.getDomainModelCache(domain));
            Query q = qf.create(query);
            return q.<String>list().stream().map(json -> Json.createReader(new StringReader(json)).readObject()).collect(toList());
        }
    }

    @Override
    public Collection<JsonObject> queryProcessInstances(ProcessInstanceFilter filter) {
        QueryFactory qf = Search.getQueryFactory((RemoteCache) manager.getProcessInstancesCache());
        QueryBuilder qb = qf.from(ProcessInstance.class);
        if (filter != null) {
            FilterConditionContextQueryBuilder filterBuilder = filterList("state", filter.getState(), null, qb);
            filterBuilder = filterList("processId", filter.getProcessId(), filterBuilder, qb);
            filterList("id", filter.getId(), filterBuilder, qb);
            if (filter.getLimit() != null) {
                qb.maxResults(filter.getLimit());
            }
            if (filter.getOffset() != null) {
                qb.startOffset(filter.getOffset());
            }
        }
        return qb.build().list();
    }

    @Override
    public Collection<JsonObject> queryUserTaskInstances(UserTaskInstanceFilter filter) {
        QueryFactory qf = Search.getQueryFactory((RemoteCache) manager.getUserTaskInstancesCache());
        QueryBuilder qb = qf.from(UserTaskInstance.class);
        if (filter != null) {
            FilterConditionContextQueryBuilder filterBuilder = filterList("state", filter.getState(), null, qb);
            filterBuilder = filterList("processInstanceId", filter.getProcessInstanceId(), filterBuilder, qb);
            filterBuilder = filterList("id", filter.getId(), filterBuilder, qb);
            filterBuilder = filterList("actualOwner", filter.getActualOwner(), filterBuilder, qb);
            filterBuilder = filterList("potentialUsers", filter.getPotentialUsers(), filterBuilder, qb);
            filterList("potentialGroups", filter.getPotentialGroups(), filterBuilder, qb);
            if (filter.getLimit() != null) {
                qb.maxResults(filter.getLimit());
            }
            if (filter.getOffset() != null) {
                qb.startOffset(filter.getOffset());
            }
        }
        return qb.build().list();
    }

    private FilterConditionContextQueryBuilder filterList(String attribute, List values, FilterConditionContextQueryBuilder filter, QueryBuilder qb) {
        if (values == null || values.isEmpty()) {
            return filter;
        }
        if (filter == null) {
            return qb.having(attribute).in(values);
        } else {
            return filter.and().having(attribute).in(values);
        }
    }
}
