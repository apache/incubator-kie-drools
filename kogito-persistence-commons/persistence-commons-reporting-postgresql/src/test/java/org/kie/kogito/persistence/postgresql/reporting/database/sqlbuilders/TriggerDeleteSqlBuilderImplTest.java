/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.persistence.postgresql.reporting.database.sqlbuilders;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TriggerDeleteSqlBuilderImplTest extends BaseSqlBuilderImplTest {

    private final TriggerDeleteSqlBuilderImpl triggerDeleteSqlBuilder = new TriggerDeleteSqlBuilderImpl();

    @Override
    protected TriggerDeleteSqlBuilderImpl getTriggerDeleteBuilder() {
        return triggerDeleteSqlBuilder;
    }

    @Override
    protected String getCreateSql(final PostgresContext context) {
        return getTriggerDeleteBuilder().createDeleteTriggerSql(context);
    }

    @Override
    protected String getDestroySql(final PostgresContext context) {
        return getTriggerDeleteBuilder().dropDeleteTriggerSql(context);
    }

    @Override
    protected void assertCreateSql(final String sql) {
        assertNotNull(sql);
        assertSequentialContent(sql,
                "CREATE TRIGGER trgDelete_mappingId AFTER DELETE OR UPDATE ON sourceTableName",
                "FOR EACH ROW",
                "EXECUTE PROCEDURE spDelete_mappingId()");
    }

    @Override
    protected void assertDestroySql(final String sql) {
        assertNotNull(sql);
        assertSequentialContent(sql,
                "DROP TRIGGER IF EXISTS trgDelete_mappingId ON sourceTableName");
    }

    @Test
    void testCreateDeleteTriggerFunctionSql() {
        final PostgresContext context = manager.createContext(DEFINITION);

        final String sql = getTriggerDeleteBuilder().createDeleteTriggerFunctionSql(context);

        assertNotNull(sql);
        assertSequentialContent(sql,
                "CREATE FUNCTION spDelete_mappingId() RETURNS trigger AS",
                "DELETE FROM targetTableName",
                "WHERE",
                "id = OLD.id");
    }

    @Test
    void testDropDeleteTriggerFunctionSql() {
        final PostgresContext context = manager.createContext(DEFINITION);

        final String sql = getTriggerDeleteBuilder().dropDeleteTriggerFunctionSql(context);

        assertNotNull(sql);
        assertSequentialContent(sql,
                "DROP FUNCTION IF EXISTS spDelete_mappingId");
    }
}
