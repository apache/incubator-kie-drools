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

package org.kie.flyway.initializer.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.flyway.KieFlywayException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.kie.flyway.initializer.db.KieFlywayDataBaseHelper.readDataBaseInfo;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KieFlywayDataBaseHelperTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private DatabaseMetaData metaData;

    @BeforeEach
    public void setup() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
    }

    @Test
    public void testReadDataBaseInfoWithException() {
        Assertions.assertThatThrownBy(() -> readDataBaseInfo(dataSource))
                .isInstanceOf(KieFlywayException.class)
                .hasMessage("Kie Flyway: Couldn't extract database product name from datasource.");
    }

    @ParameterizedTest
    @MethodSource("getDataBaseData")
    public void testReadDataBaseInfo(String productName, String version, String normalizedName) throws Exception {
        when(metaData.getDatabaseProductName()).thenReturn(productName);
        when(metaData.getDatabaseProductVersion()).thenReturn(version);

        Assertions.assertThat(readDataBaseInfo(dataSource))
                .hasFieldOrPropertyWithValue("name", productName)
                .hasFieldOrPropertyWithValue("version", version)
                .hasFieldOrPropertyWithValue("normalizedName", normalizedName);
    }

    public static Stream<Arguments> getDataBaseData() {
        return Stream.of(Arguments.of("H2", "2.3.232", "h2"),
                Arguments.of("PostgreSQL", "42.7.4", "postgresql"),
                Arguments.of("My Custom DB Type.", "v1.0", "my-custom-db-type"));
    }

}
