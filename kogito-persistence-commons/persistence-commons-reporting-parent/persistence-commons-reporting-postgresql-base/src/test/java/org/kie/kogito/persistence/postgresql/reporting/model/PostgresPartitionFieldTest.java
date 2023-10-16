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
package org.kie.kogito.persistence.postgresql.reporting.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PostgresPartitionFieldTest {

    static final PostgresPartitionField PARTITION_FIELD = new PostgresPartitionField("field1", "value1");
    static final int PARTITION_FIELD_HASHCODE = PARTITION_FIELD.hashCode();

    @Test
    void testEquality() {
        assertEquals(PARTITION_FIELD,
                PARTITION_FIELD);
        assertNotEquals(PARTITION_FIELD,
                new PostgresPartitionField("different", "value1"));
        assertNotEquals(PARTITION_FIELD,
                new PostgresPartitionField("field1", "different"));
    }

    @Test
    void testHashCode() {
        assertEquals(PARTITION_FIELD_HASHCODE,
                PARTITION_FIELD.hashCode());
        assertNotEquals(PARTITION_FIELD_HASHCODE,
                new PostgresPartitionField("different", "value1").hashCode());
        assertNotEquals(PARTITION_FIELD_HASHCODE,
                new PostgresPartitionField("field1", "different").hashCode());
    }
}
