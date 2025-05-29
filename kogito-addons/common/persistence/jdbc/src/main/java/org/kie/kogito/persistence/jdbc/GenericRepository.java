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
package org.kie.kogito.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.sql.DataSource;

public class GenericRepository extends Repository {

    private static final String PAYLOAD = "payload";
    private static final String VERSION = "version";

    private final DataSource dataSource;

    public GenericRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    void insertInternal(String processId, String processVersion, UUID id, byte[] payload, String businessKey, String[] eventTypes) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT);
                PreparedStatement eventStatement = connection.prepareStatement(DELETE_ALL_WAITING_FOR_EVENT_TYPE);
                PreparedStatement insertEventStatement = connection.prepareStatement(INSERT_WAITING_FOR_EVENT_TYPE)) {

            eventStatement.setString(1, id.toString());
            eventStatement.executeUpdate();

            for (String eventType : eventTypes) {
                insertEventStatement.setString(1, id.toString());
                insertEventStatement.setString(2, eventType);
                insertEventStatement.executeUpdate();
            }

            String processInstanceId = id.toString();
            statement.setString(1, processInstanceId);
            statement.setBytes(2, payload);
            statement.setString(3, processId);
            statement.setString(4, processVersion);
            statement.setLong(5, 0L);
            statement.executeUpdate();
            if (businessKey != null) {
                try (PreparedStatement businessKeyStmt = connection.prepareStatement(INSERT_BUSINESS_KEY)) {
                    businessKeyStmt.setString(1, businessKey);
                    businessKeyStmt.setString(2, processInstanceId);
                    businessKeyStmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw uncheckedException(e, "Error inserting process instance id: %s, processId: %s processVersion: %s business key: %s", id, processId, processVersion, businessKey);
        }
    }

    @Override
    void updateInternal(String processId, String processVersion, UUID id, byte[] payload, String[] eventTypes) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlIncludingVersion(UPDATE, processVersion));
                PreparedStatement eventStatement = connection.prepareStatement(DELETE_ALL_WAITING_FOR_EVENT_TYPE);
                PreparedStatement insertEventStatement = connection.prepareStatement(INSERT_WAITING_FOR_EVENT_TYPE)) {

            eventStatement.setString(1, id.toString());
            eventStatement.executeUpdate();

            for (String eventType : eventTypes) {
                insertEventStatement.setString(1, id.toString());
                insertEventStatement.setString(2, eventType);
                insertEventStatement.executeUpdate();
            }

            statement.setBytes(1, payload);
            statement.setString(2, processId);
            statement.setString(3, id.toString());
            if (processVersion != null) {
                statement.setString(4, processVersion);
            }
            statement.executeUpdate();
        } catch (Exception e) {
            throw uncheckedException(e, "Error updating process instance %s", id);
        }
    }

    @Override
    boolean updateWithLock(String processId, String processVersion, UUID id, byte[] payload, long version, String[] eventTypes) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlIncludingVersion(UPDATE_WITH_LOCK, processVersion));
                PreparedStatement eventStatement = connection.prepareStatement(DELETE_ALL_WAITING_FOR_EVENT_TYPE);
                PreparedStatement insertEventStatement = connection.prepareStatement(INSERT_WAITING_FOR_EVENT_TYPE)) {

            eventStatement.setString(1, id.toString());
            eventStatement.executeUpdate();

            for (String eventType : eventTypes) {
                insertEventStatement.setString(1, id.toString());
                insertEventStatement.setString(2, eventType);
                insertEventStatement.executeUpdate();
            }

            statement.setBytes(1, payload);
            statement.setLong(2, version + 1);
            statement.setString(3, processId);
            statement.setString(4, id.toString());
            statement.setLong(5, version);
            if (processVersion != null) {
                statement.setString(6, processVersion);
            }
            int count = statement.executeUpdate();
            return count == 1;
        } catch (Exception e) {
            throw uncheckedException(e, "Error updating with lock process instance %s", id);
        }
    }

    @Override
    boolean deleteInternal(String processId, String processVersion, UUID id) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlIncludingVersion(DELETE, processVersion));
                PreparedStatement eventStatement = connection.prepareStatement(DELETE_ALL_WAITING_FOR_EVENT_TYPE)) {

            eventStatement.setString(1, id.toString());
            eventStatement.executeUpdate();

            statement.setString(1, processId);
            statement.setString(2, id.toString());
            if (processVersion != null) {
                statement.setString(3, processVersion);
            }
            int count = statement.executeUpdate();
            return count == 1;
        } catch (Exception e) {
            throw uncheckedException(e, "Error deleting process instance %s", id);
        }
    }

    private Record from(ResultSet rs) throws SQLException {
        return new Record(rs.getBytes(PAYLOAD), rs.getLong(VERSION));
    }

    @Override
    Optional<Record> findByIdInternal(String processId, String processVersion, UUID id) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlIncludingVersion(FIND_BY_ID, processVersion))) {
            statement.setString(1, processId);
            statement.setString(2, id.toString());
            if (processVersion != null) {
                statement.setString(3, processVersion);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(from(resultSet));
                }
            }
        } catch (Exception e) {
            throw uncheckedException(e, "Error finding process instance %s", id);
        }
        return Optional.empty();
    }

    @Override
    Stream<Record> findAllInternalWaitingFor(String processId, String processVersion, String eventType) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlIncludingVersion(FIND_ALL_WAITING_FOR_EVENT_TYPE, processVersion));) {
            statement.setString(1, processId);
            statement.setString(2, eventType);
            if (processVersion != null) {
                statement.setString(3, processVersion);
            }

            List<Record> data = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                data.add(from(resultSet));
            }
            resultSet.close();
            return data.stream();
        } catch (SQLException e) {
            throw uncheckedException(e, "Error finding all process instances, for processId %s waiting for %s", processId, eventType);
        }
    }

    @Override
    Optional<Record> findByBusinessKey(String processId, String processVersion, String businessKey) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlIncludingVersion(FIND_BY_BUSINESS_KEY, processVersion))) {
            statement.setString(1, businessKey);
            statement.setString(2, processId);
            if (processVersion != null) {
                statement.setString(3, processVersion);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(from(resultSet)) : Optional.empty();
            }
        } catch (Exception e) {
            throw uncheckedException(e, "Error finding process instance. Business key: %s, Process Id: %s, Process version: %s", businessKey, processId, processVersion);
        }
    }

    private static class CloseableWrapper implements Runnable {

        private Deque<AutoCloseable> wrapped = new ArrayDeque<>();

        public <T extends AutoCloseable> T nest(T c) {
            wrapped.addFirst(c);
            return c;
        }

        @Override
        public void run() {
            try {
                close();
            } catch (Exception ex) {
                throw new RuntimeException("Error closing resources", ex);
            }
        }

        public void close() throws Exception {
            Exception exception = null;
            for (AutoCloseable wrap : wrapped) {
                try {
                    wrap.close();
                } catch (Exception ex) {
                    if (exception != null) {
                        ex.addSuppressed(exception);
                    }
                    exception = ex;
                }
            }
            if (exception != null) {
                throw exception;
            }
        }
    }

    @Override
    Stream<Record> findAllInternal(String processId, String processVersion) {
        CloseableWrapper close = new CloseableWrapper();
        try {
            Connection connection = close.nest(dataSource.getConnection());
            PreparedStatement statement = close.nest(connection.prepareStatement(sqlIncludingVersion(FIND_ALL, processVersion)));
            statement.setString(1, processId);
            if (processVersion != null) {
                statement.setString(2, processVersion);
            }
            ResultSet resultSet = close.nest(statement.executeQuery());
            return StreamSupport.stream(new Spliterators.AbstractSpliterator<Record>(Long.MAX_VALUE, Spliterator.ORDERED) {
                @Override
                public boolean tryAdvance(Consumer<? super Record> action) {
                    try {
                        boolean hasNext = resultSet.next();
                        if (hasNext) {
                            action.accept(from(resultSet));
                        } else {
                            try {
                                close.close();
                            } catch (Exception e) {
                                throw uncheckedException(e, "Error finding all process instances, for processId %s", processId);
                            }
                        }
                        return hasNext;
                    } catch (SQLException e) {
                        throw uncheckedException(e, "Error finding all process instances, for processId %s", processId);
                    }
                }
            }, false).onClose(close);
        } catch (SQLException e) {
            try {
                close.close();
            } catch (Exception ex) {
                e.addSuppressed(ex);
            }
            throw uncheckedException(e, "Error finding all process instances, for processId %s", processId);
        }
    }

    private static String sqlIncludingVersion(String statement, String processVersion) {
        return statement + " " + (processVersion == null ? PROCESS_VERSION_IS_NULL : PROCESS_VERSION_EQUALS_TO);
    }

    @Override
    long migrate(String processId, String processVersion, String targetProcessId, String targetProcessVersion) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlIncludingVersion(Repository.MIGRATE_BULK, processVersion))) {
            statement.setString(1, targetProcessId);
            statement.setString(2, targetProcessVersion);
            statement.setString(3, processId);
            if (processVersion != null) {
                statement.setString(4, processVersion);
            }
            return statement.executeUpdate();
        } catch (Exception e) {
            throw uncheckedException(e, "Error updating process instance %s-%s", processId, processVersion);
        }
    }

    @Override
    void migrate(String processId, String processVersion, String targetProcessId, String targetProcessVersion, String[] processIds) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlIncludingVersion(Repository.MIGRATE_INSTANCE, processVersion))) {
            statement.setString(1, targetProcessId);
            statement.setString(2, targetProcessVersion);
            statement.setObject(3, connection.createArrayOf("VARCHAR", processIds));
            statement.setString(4, processId);
            if (processVersion != null) {
                statement.setString(5, processVersion);
            }
            statement.executeUpdate();
        } catch (Exception e) {
            throw uncheckedException(e, "Error updating process instance %s-%s", processId, processVersion);
        }
    }

}
