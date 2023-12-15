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
package org.kie.kogito.jobs.service.repository.postgresql;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.kogito.jobs.service.model.JobServiceManagementInfo;
import org.kie.kogito.jobs.service.repository.JobServiceManagementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.SqlClient;
import io.vertx.mutiny.sqlclient.Tuple;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PostgreSqlJobServiceManagementRepository implements JobServiceManagementRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSqlJobServiceManagementRepository.class);

    private PgPool client;

    @Inject
    public PostgreSqlJobServiceManagementRepository(PgPool client) {
        this.client = client;
    }

    public Uni<JobServiceManagementInfo> getAndUpdate(String id, Function<JobServiceManagementInfo, JobServiceManagementInfo> computeUpdate) {
        LOGGER.info("get {}", id);
        return client.withTransaction(conn -> conn
                .preparedQuery("SELECT id, token, last_heartbeat FROM job_service_management WHERE id = $1 FOR UPDATE ")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null)
                .onItem().invoke(r -> LOGGER.trace("got {}", r))
                .onItem().transformToUni(r -> update(conn, computeUpdate.apply(r))));
    }

    JobServiceManagementInfo from(Row row) {
        return new JobServiceManagementInfo(row.getString("id"),
                row.getString("token"),
                row.getOffsetDateTime("last_heartbeat"));
    }

    @Override
    public Uni<JobServiceManagementInfo> set(JobServiceManagementInfo info) {
        LOGGER.info("set {}", info);
        return update(client, info);
    }

    private Uni<JobServiceManagementInfo> update(SqlClient conn, JobServiceManagementInfo info) {
        if (Objects.isNull(info)) {
            return Uni.createFrom().nullItem();
        }
        return conn.preparedQuery("INSERT INTO job_service_management (id, token, last_heartbeat) " +
                "VALUES ($1, $2, $3) " +
                "ON CONFLICT (id) DO " +
                "UPDATE SET token = $2, last_heartbeat = $3 " +
                "RETURNING id, token, last_heartbeat")
                .execute(Tuple.tuple(Stream.of(
                        info.getId(),
                        info.getToken(),
                        info.getLastHeartbeat()).collect(Collectors.toList())))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    @Override
    public Uni<JobServiceManagementInfo> heartbeat(JobServiceManagementInfo info) {
        return client.withTransaction(conn -> conn
                .preparedQuery("UPDATE job_service_management SET last_heartbeat = now() WHERE id = $1 AND token = $2 RETURNING id, token, last_heartbeat")
                .execute(Tuple.of(info.getId(), info.getToken()))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null)
                .onItem().invoke(r -> LOGGER.trace("Heartbeat {}", r)));
    }
}
